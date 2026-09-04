package de.galonga.audiotoolbox.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

data class GeneratorParams(
    val waveform: WaveformType,
    val frequencyHz: Float,
    val amplitude: Float,
    val sweepStartHz: Float,
    val sweepEndHz: Float,
    val sweepDurationSeconds: Float
)

/**
 * Continuously synthesizes audio for the current [GeneratorParams] snapshot and streams it to
 * the speaker via AudioTrack. Params are re-read once per chunk, so changes while playing
 * (waveform, frequency, amplitude) take effect within one chunk's latency.
 */
class SignalGeneratorEngine(
    private val scope: CoroutineScope,
    private val paramsProvider: () -> GeneratorParams,
    private val onSweepFrequencyUpdate: (Float) -> Unit,
    private val onError: (String) -> Unit,
    private val onStopped: () -> Unit
) {
    private var audioTrack: AudioTrack? = null
    private var job: Job? = null

    @Volatile private var stopRequested = false

    fun start() {
        stopRequested = false

        val minBufferBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (minBufferBytes == AudioTrack.ERROR_BAD_VALUE || minBufferBytes == AudioTrack.ERROR) {
            onError("Audio output not supported at ${SAMPLE_RATE}Hz")
            return
        }

        val track = try {
            AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferBytes * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } catch (e: Exception) {
            onError(e.message ?: "Failed to open audio output")
            return
        }
        if (track.state != AudioTrack.STATE_INITIALIZED) {
            track.release()
            onError("Failed to initialize audio output")
            return
        }
        audioTrack = track

        job = scope.launch(Dispatchers.Default) {
            try {
                track.play()
                var phase = 0.0
                var sweepSamplePos = 0L
                val pink = PinkNoiseGenerator()
                val chunk = ShortArray(CHUNK_SIZE)

                while (!stopRequested) {
                    val params = paramsProvider()
                    val amplitude = params.amplitude.coerceIn(0f, 1f)
                    var lastSweepFreq = -1.0

                    for (i in chunk.indices) {
                        val sample: Float = when (params.waveform) {
                            WaveformType.SINE -> {
                                phase = WaveformMath.advancePhase(phase, params.frequencyHz.toDouble(), SAMPLE_RATE)
                                WaveformMath.sine(phase)
                            }
                            WaveformType.SQUARE -> {
                                phase = WaveformMath.advancePhase(phase, params.frequencyHz.toDouble(), SAMPLE_RATE)
                                WaveformMath.square(phase)
                            }
                            WaveformType.TRIANGLE -> {
                                phase = WaveformMath.advancePhase(phase, params.frequencyHz.toDouble(), SAMPLE_RATE)
                                WaveformMath.triangle(phase)
                            }
                            WaveformType.SAWTOOTH -> {
                                phase = WaveformMath.advancePhase(phase, params.frequencyHz.toDouble(), SAMPLE_RATE)
                                WaveformMath.sawtooth(phase)
                            }
                            WaveformType.SWEEP -> {
                                val totalSamples = (params.sweepDurationSeconds * SAMPLE_RATE).toLong().coerceAtLeast(1)
                                val t = (sweepSamplePos % totalSamples).toDouble() / totalSamples
                                val freq = WaveformMath.logSweepFrequency(
                                    t, params.sweepStartHz.toDouble(), params.sweepEndHz.toDouble()
                                )
                                sweepSamplePos++
                                lastSweepFreq = freq
                                phase = WaveformMath.advancePhase(phase, freq, SAMPLE_RATE)
                                WaveformMath.sine(phase)
                            }
                            WaveformType.WHITE_NOISE -> Random.nextFloat() * 2f - 1f
                            WaveformType.PINK_NOISE -> pink.next()
                        }
                        chunk[i] = (sample.coerceIn(-1f, 1f) * amplitude * Short.MAX_VALUE).toInt().toShort()
                    }

                    if (lastSweepFreq >= 0.0) {
                        val freqSnapshot = lastSweepFreq.toFloat()
                        withContext(Dispatchers.Main) { onSweepFrequencyUpdate(freqSnapshot) }
                    }
                    track.write(chunk, 0, chunk.size)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Signal generation failed") }
            } finally {
                audioTrack?.runCatching { stop(); release() }
                audioTrack = null
                withContext(Dispatchers.Main) { onStopped() }
            }
        }
    }

    fun stop() {
        stopRequested = true
    }

    companion object {
        const val SAMPLE_RATE = 44100
        const val CHUNK_SIZE = 1024
    }
}
