package de.galonga.audiotoolbox.app.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

/**
 * Continuously captures phone-mic audio and emits a waveform + FFT magnitude spectrum
 * for each fixed-size window, for live visualization (no file output).
 */
class MicAnalyzerEngine(
    private val scope: CoroutineScope,
    private val onData: (waveform: FloatArray, spectrumDb: FloatArray, bpm: Double?) -> Unit,
    private val onError: (String) -> Unit,
    private val onStopped: () -> Unit
) {
    private var audioRecord: AudioRecord? = null
    private var job: Job? = null
    private val window = FftMath.hannWindow(FFT_SIZE)
    private val bpmDetector = BpmDetector(envelopeSampleRateHz = SAMPLE_RATE.toDouble() / FFT_SIZE)

    @Volatile private var stopRequested = false

    fun start() {
        stopRequested = false

        val minBufferBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (minBufferBytes == AudioRecord.ERROR_BAD_VALUE || minBufferBytes == AudioRecord.ERROR) {
            onError("Microphone not supported at ${SAMPLE_RATE}Hz")
            return
        }
        val bufferSizeBytes = maxOf(minBufferBytes, FFT_SIZE * 2) * 4

        val ar = tryBuildAudioRecord(MediaRecorder.AudioSource.UNPROCESSED, bufferSizeBytes)
            ?: tryBuildAudioRecord(MediaRecorder.AudioSource.MIC, bufferSizeBytes)
            ?: run {
                onError("Failed to open the microphone")
                return
            }
        audioRecord = ar

        job = scope.launch(Dispatchers.Default) {
            try {
                ar.startRecording()
                if (ar.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                    withContext(Dispatchers.Main) { onError("Microphone failed to start") }
                    return@launch
                }

                val pcm = ShortArray(FFT_SIZE)
                while (!stopRequested) {
                    val read = ar.read(pcm, 0, pcm.size)
                    if (read < FFT_SIZE) continue

                    val waveform = FloatArray(FFT_SIZE) { pcm[it] / 32768f }
                    val spectrum = FftMath.magnitudeSpectrumDb(waveform, window)
                    val rms = sqrt(waveform.sumOf { (it * it).toDouble() } / waveform.size)
                    val bpm = bpmDetector.addEnergySample(rms)
                    withContext(Dispatchers.Main) { onData(waveform, spectrum, bpm) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "Microphone capture failed") }
            } finally {
                audioRecord?.runCatching { stop(); release() }
                audioRecord = null
                withContext(Dispatchers.Main) { onStopped() }
            }
        }
    }

    fun stop() {
        stopRequested = true
        audioRecord?.stop() // Unblocks any pending ar.read()
    }

    private fun tryBuildAudioRecord(source: Int, bufferSizeBytes: Int): AudioRecord? = try {
        val ar = AudioRecord(source, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeBytes)
        if (ar.state == AudioRecord.STATE_INITIALIZED) ar else { ar.release(); null }
    } catch (e: Exception) {
        null
    }

    companion object {
        const val SAMPLE_RATE = 44100
        const val FFT_SIZE = 2048
    }
}
