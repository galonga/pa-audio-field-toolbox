package de.minmon.app.ui.screens.record

import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.sqrt

/**
 * Records audio from a USB audio device (e.g. Pioneer DJM mixer) using:
 *   AudioRecord.setPreferredDevice(usbDevice) → PCM
 *   → gain applied in-place on PCM samples
 *   → MediaCodec (AAC-LC, 192kbps stereo)
 *   → MediaMuxer (M4A container)
 *
 * MediaRecorder cannot route to a specific AudioDeviceInfo, so this pipeline
 * is required for USB audio capture.
 */
class UsbAudioRecorder(
    private val device: AudioDeviceInfo,
    private val outputFile: File,
    private val scope: CoroutineScope,
    private val gainProvider: () -> Float,
    private val onLevelUpdate: (Float) -> Unit,
    private val onError: (String) -> Unit,
    private val onStopped: () -> Unit
) {
    private var audioRecord: AudioRecord? = null
    private var codec: MediaCodec? = null
    private var muxer: MediaMuxer? = null
    private var recordingJob: Job? = null

    @Volatile private var stopRequested = false

    fun start() {
        stopRequested = false

        val sampleRate = chooseSampleRate(device)
        val channelCount = if (device.channelCounts.isEmpty() || device.channelCounts.contains(2)) 2 else 1
        val channelConfig = if (channelCount == 2) AudioFormat.CHANNEL_IN_STEREO else AudioFormat.CHANNEL_IN_MONO

        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT)
        if (minBuffer == AudioRecord.ERROR_BAD_VALUE || minBuffer == AudioRecord.ERROR) {
            onError("USB audio format not supported by this device (${sampleRate}Hz ${if (channelCount == 2) "stereo" else "mono"})")
            return
        }
        val bufferSize = maxOf(minBuffer * 8, 16384)

        // UNPROCESSED bypasses Android's audio effects pipeline for clean professional audio.
        // Fall back to MIC source if UNPROCESSED is rejected by the HAL.
        val ar = tryBuildAudioRecord(MediaRecorder.AudioSource.UNPROCESSED, sampleRate, channelConfig, bufferSize)
            ?: tryBuildAudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, bufferSize)
            ?: run {
                onError("Failed to open AudioRecord at ${sampleRate}Hz. Try reconnecting the mixer.")
                return
            }

        if (!ar.setPreferredDevice(device)) {
            ar.release()
            onError("Could not route audio to ${device.productName}. The device may not be supported.")
            return
        }
        audioRecord = ar

        val codecFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount).apply {
            setInteger(MediaFormat.KEY_BIT_RATE, if (channelCount == 2) 192_000 else 96_000)
            setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSize * 2)
            setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        }

        val c = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
        c.configure(codecFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        codec = c

        val m = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        muxer = m

        recordingJob = scope.launch(Dispatchers.IO) {
            try {
                c.start()
                ar.startRecording()

                if (ar.state != AudioRecord.STATE_INITIALIZED || ar.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                    withContext(Dispatchers.Main) {
                        onError("AudioRecord failed to start. Mixer may use USB Audio Class 2.0 which is not supported on this Android device.")
                    }
                    outputFile.delete()
                    return@launch
                }

                // Verify the recording is actually routed to the USB device, not the mic.
                val routedDevice = ar.routedDevice
                if (routedDevice != null && routedDevice.id != device.id) {
                    withContext(Dispatchers.Main) {
                        onError("Audio routed to '${routedDevice.productName}' instead of '${device.productName}'. Reconnect the mixer and try again.")
                    }
                    ar.stop()
                    outputFile.delete()
                    return@launch
                }

                val pcmBuffer = ByteArray(bufferSize)
                val bytesPerFrame = channelCount * 2 // PCM_16BIT = 2 bytes/sample
                var presentationTimeUs = 0L
                var audioTrackIndex = -1
                var muxerStarted = false

                while (!stopRequested) {
                    val bytesRead = ar.read(pcmBuffer, 0, pcmBuffer.size)
                    if (bytesRead <= 0) continue

                    // Apply software gain and compute RMS level in one pass
                    val level = applyGainAndCalcRms(pcmBuffer, bytesRead, gainProvider())
                    onLevelUpdate(level)

                    // Feed gain-adjusted PCM into the encoder
                    val inputIdx = c.dequeueInputBuffer(10_000L)
                    if (inputIdx >= 0) {
                        val inputBuf = c.getInputBuffer(inputIdx)!!
                        inputBuf.clear()
                        inputBuf.put(pcmBuffer, 0, bytesRead)
                        val samplesRead = bytesRead / bytesPerFrame
                        c.queueInputBuffer(inputIdx, 0, bytesRead, presentationTimeUs, 0)
                        presentationTimeUs += samplesRead * 1_000_000L / sampleRate
                    }

                    muxerStarted = drainEncoder(c, m, audioTrackIndex, muxerStarted, endOfStream = false)
                        .also { audioTrackIndex = it.second }.first
                }

                // Signal end of stream
                val inputIdx = c.dequeueInputBuffer(10_000L)
                if (inputIdx >= 0) {
                    c.queueInputBuffer(inputIdx, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                }
                drainEncoder(c, m, audioTrackIndex, muxerStarted, endOfStream = true)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e.message ?: "USB recording failed") }
                outputFile.delete()
            } finally {
                audioRecord?.runCatching { stop(); release() }
                codec?.runCatching { stop(); release() }
                muxer?.runCatching { stop(); release() }
                audioRecord = null; codec = null; muxer = null
                withContext(Dispatchers.Main) { onStopped() }
            }
        }
    }

    fun stop() {
        stopRequested = true
        audioRecord?.stop() // Unblocks any pending ar.read()
    }

    /**
     * Applies software gain to 16-bit little-endian PCM samples in-place,
     * and returns the RMS level normalised to [0, 1].
     */
    private fun applyGainAndCalcRms(buffer: ByteArray, bytesRead: Int, gain: Float): Float {
        val numSamples = bytesRead / 2
        var sumSquares = 0.0
        for (i in 0 until numSamples) {
            // Read 16-bit LE signed sample
            val lo = buffer[i * 2].toInt() and 0xFF
            val hi = buffer[i * 2 + 1].toInt() // sign-extends automatically (Byte is signed)
            val raw = (hi shl 8) or lo
            // Apply gain and clamp
            val gained = (raw * gain).coerceIn(-32768f, 32767f).toInt()
            // Write back
            buffer[i * 2] = (gained and 0xFF).toByte()
            buffer[i * 2 + 1] = ((gained shr 8) and 0xFF).toByte()
            sumSquares += gained.toDouble() * gained
        }
        val rms = if (numSamples > 0) sqrt(sumSquares / numSamples) else 0.0
        return (rms / 32767.0).toFloat().coerceIn(0f, 1f)
    }

    private fun drainEncoder(
        c: MediaCodec,
        m: MediaMuxer,
        trackIndex: Int,
        muxerAlreadyStarted: Boolean,
        endOfStream: Boolean
    ): Pair<Boolean, Int> {
        var muxerStarted = muxerAlreadyStarted
        var audioTrackIndex = trackIndex
        val timeoutUs = if (endOfStream) 10_000L else 0L
        val bufInfo = MediaCodec.BufferInfo()

        while (true) {
            val outputIdx = c.dequeueOutputBuffer(bufInfo, timeoutUs)
            when {
                outputIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    if (!muxerStarted) {
                        audioTrackIndex = m.addTrack(c.outputFormat)
                        m.start()
                        muxerStarted = true
                    }
                }
                outputIdx == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    if (!endOfStream) break
                }
                outputIdx >= 0 -> {
                    val outputBuf = c.getOutputBuffer(outputIdx)!!
                    val isConfig = bufInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0
                    if (muxerStarted && bufInfo.size > 0 && !isConfig) {
                        outputBuf.position(bufInfo.offset)
                        outputBuf.limit(bufInfo.offset + bufInfo.size)
                        m.writeSampleData(audioTrackIndex, outputBuf, bufInfo)
                    }
                    c.releaseOutputBuffer(outputIdx, false)
                    if (bufInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) break
                }
            }
        }
        return Pair(muxerStarted, audioTrackIndex)
    }

    private fun chooseSampleRate(device: AudioDeviceInfo): Int {
        val supported = device.sampleRates
        // Always prefer 48kHz or 44.1kHz regardless of what else the device advertises.
        // The DJM-750MK2 reports 96kHz (UAC2) but many Android HALs can only open 48kHz USB audio.
        return when {
            supported.isEmpty() -> 48000
            supported.contains(48000) -> 48000
            supported.contains(44100) -> 44100
            else -> supported.min() // prefer lowest supported rather than highest for compatibility
        }
    }

    private fun tryBuildAudioRecord(
        audioSource: Int,
        sampleRate: Int,
        channelConfig: Int,
        bufferSize: Int
    ): AudioRecord? {
        return try {
            val ar = AudioRecord(audioSource, sampleRate, channelConfig, AudioFormat.ENCODING_PCM_16BIT, bufferSize)
            if (ar.state == AudioRecord.STATE_INITIALIZED) ar else { ar.release(); null }
        } catch (e: Exception) {
            null
        }
    }
}
