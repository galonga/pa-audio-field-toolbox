package de.galonga.audiotoolbox.app.ui.screens.record

import android.app.Application
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** MIDI connection state for the DJM mixer's secondary control channel. */
enum class MidiConnectionState {
    /** Android MIDI API hasn't found the mixer's MIDI interface yet. */
    DISCONNECTED,
    /** MIDI is connected and the rec-start SysEx was sent. LED should be solid. */
    CONNECTED,
    /** MIDI API not available on this device (API < 23). */
    UNAVAILABLE
}

@Immutable
data class UsbAudioDevice(
    val id: Int,
    val name: String,
    val sampleRates: List<Int>,
    val channelCounts: List<Int>
) {
    val displayName: String get() = name.ifBlank { "USB Audio Device" }
    val formatSummary: String get() {
        val rateStr = sampleRates.maxOrNull()?.let { "${it / 1000}kHz" } ?: "48kHz"
        val chStr = if (channelCounts.isEmpty() || channelCounts.contains(2)) "Stereo" else "Mono"
        return "$chStr · $rateStr"
    }
}

@Immutable
data class Recording(
    val id: String,
    val name: String,
    val filePath: String,
    val durationMs: Long,
    val createdAt: Long
) {
    val formattedDuration: String get() {
        val s = durationMs / 1000
        return "%02d:%02d".format(s / 60, s % 60)
    }
}

enum class RecordingSource { MICROPHONE, USB_MIXER }

@Immutable
data class RecordScreenUiState(
    val isRecording: Boolean = false,
    val recordingDurationSeconds: Int = 0,
    val recordingSource: RecordingSource = RecordingSource.MICROPHONE,
    val usbDevice: UsbAudioDevice? = null,
    val midiState: MidiConnectionState = MidiConnectionState.DISCONNECTED,
    val inputLevel: Float = 0f,  // 0.0–1.0 normalised RMS
    val inputGain: Float = 1f,   // 0.0–2.0 software gain multiplier
    val recordings: List<Recording> = emptyList(),
    val playingId: String? = null,
    val errorMessage: String? = null
) {
    val formattedTimer: String get() {
        val s = recordingDurationSeconds
        return "%02d:%02d".format(s / 60, s % 60)
    }
}

sealed interface RecordAction {
    object StartRecording : RecordAction
    object StopRecording : RecordAction
    data class PlayRecording(val id: String) : RecordAction
    object StopPlayback : RecordAction
    data class DeleteRecording(val id: String) : RecordAction
    data class SetGain(val gain: Float) : RecordAction
}

class RecordViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val audioManager = context.getSystemService(AudioManager::class.java)

    private var currentUsbDevice: AudioDeviceInfo? = null

    private var mediaRecorder: MediaRecorder? = null
    private var usbRecorder: UsbAudioRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var timerJob: Job? = null
    private var levelJob: Job? = null
    private var currentRecordingFile: File? = null

    // Volatile so UsbAudioRecorder can read it from the IO thread without locking
    @Volatile private var currentGain: Float = 1f

    private val recordingsDir: File
        get() = File(context.filesDir, "recordings").also { it.mkdirs() }

    private val _state = MutableStateFlow(RecordScreenUiState())
    val state: StateFlow<RecordScreenUiState> get() = _state

    // MIDI manager for DJM mixer handshake (makes LED go solid, activates On-Air mode)
    private val djmMidi = DjmMidiManager(
        context = context,
        onMixerMidiConnected = {
            _state.value = _state.value.copy(midiState = MidiConnectionState.CONNECTED)
        },
        onMixerMidiDisconnected = {
            _state.value = _state.value.copy(midiState = MidiConnectionState.DISCONNECTED)
        }
    )

    private val usbDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<AudioDeviceInfo>) {
            val device = addedDevices.firstOrNull { it.isUsbInput() } ?: return
            currentUsbDevice = device
            _state.value = _state.value.copy(usbDevice = device.toUsbAudioDevice())
        }

        override fun onAudioDevicesRemoved(removedDevices: Array<AudioDeviceInfo>) {
            val removedIds = removedDevices.map { it.id }.toSet()
            if (currentUsbDevice?.id in removedIds) {
                currentUsbDevice = null
                _state.value = _state.value.copy(usbDevice = null)
            }
        }
    }

    init {
        audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS)
            .firstOrNull { it.isUsbInput() }
            ?.let { device ->
                currentUsbDevice = device
                _state.value = _state.value.copy(usbDevice = device.toUsbAudioDevice())
            }

        audioManager.registerAudioDeviceCallback(usbDeviceCallback, null)

        djmMidi.start()

        loadRecordings()
    }

    fun onRecordAction(action: RecordAction) {
        when (action) {
            is RecordAction.StartRecording -> startRecording()
            is RecordAction.StopRecording -> stopRecording()
            is RecordAction.PlayRecording -> playRecording(action.id)
            is RecordAction.StopPlayback -> stopPlayback()
            is RecordAction.DeleteRecording -> deleteRecording(action.id)
            is RecordAction.SetGain -> setGain(action.gain)
        }
    }

    private fun setGain(gain: Float) {
        currentGain = gain
        _state.value = _state.value.copy(inputGain = gain)
    }

    private fun startRecording() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(recordingsDir, "mix_$timestamp.m4a")
        currentRecordingFile = file

        val usbDevice = currentUsbDevice
        if (usbDevice != null) {
            startUsbRecording(usbDevice, file)
        } else {
            startMicRecording(file)
        }
    }

    private fun startUsbRecording(device: AudioDeviceInfo, file: File) {
        usbRecorder = UsbAudioRecorder(
            device = device,
            outputFile = file,
            scope = viewModelScope,
            gainProvider = { currentGain },
            onLevelUpdate = { level ->
                _state.value = _state.value.copy(inputLevel = level)
            },
            onError = { msg ->
                _state.value = _state.value.copy(isRecording = false, inputLevel = 0f, errorMessage = msg)
                stopTimer()
            },
            onStopped = {
                _state.value = _state.value.copy(isRecording = false, recordingDurationSeconds = 0, inputLevel = 0f)
                usbRecorder = null
                loadRecordings()
            }
        )
        usbRecorder!!.start()
        djmMidi.sendRecStart()  // Tell DJM to show solid REC LED and confirm On-Air mode
        _state.value = _state.value.copy(
            isRecording = true,
            recordingDurationSeconds = 0,
            recordingSource = RecordingSource.USB_MIXER,
            errorMessage = null
        )
        startTimer()
    }

    private fun startMicRecording(file: File) {
        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(192000)
            setOutputFile(file.absolutePath)
            try {
                prepare()
                start()
                mediaRecorder = this
                _state.value = _state.value.copy(
                    isRecording = true,
                    recordingDurationSeconds = 0,
                    recordingSource = RecordingSource.MICROPHONE,
                    errorMessage = null
                )
                startTimer()
                startMicLevelPolling()
            } catch (e: Exception) {
                release()
                file.delete()
                _state.value = _state.value.copy(errorMessage = "Failed to start recording: ${e.message}")
            }
        }
    }

    /**
     * Polls MediaRecorder.getMaxAmplitude() every 80ms for mic-path level metering.
     * getMaxAmplitude() returns 0–32767 (peak since last call) and resets on each call.
     */
    private fun startMicLevelPolling() {
        levelJob = viewModelScope.launch {
            while (true) {
                delay(80)
                val peak = mediaRecorder?.maxAmplitude ?: 0
                _state.value = _state.value.copy(inputLevel = (peak / 32767f).coerceIn(0f, 1f))
            }
        }
    }

    private fun stopRecording() {
        stopTimer()
        stopLevelPolling()

        usbRecorder?.stop()
        djmMidi.sendRecStop()

        if (mediaRecorder != null) {
            try {
                mediaRecorder?.apply { stop(); release() }
            } catch (e: Exception) {
                currentRecordingFile?.delete()
            } finally {
                mediaRecorder = null
            }
            _state.value = _state.value.copy(isRecording = false, recordingDurationSeconds = 0, inputLevel = 0f)
            loadRecordings()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.value = _state.value.copy(
                    recordingDurationSeconds = _state.value.recordingDurationSeconds + 1
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel(); timerJob = null
    }

    private fun stopLevelPolling() {
        levelJob?.cancel(); levelJob = null
        _state.value = _state.value.copy(inputLevel = 0f)
    }

    private fun playRecording(id: String) {
        val recording = _state.value.recordings.find { it.id == id } ?: return
        stopPlayback()

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(recording.filePath)
                prepare()
                start()
                _state.value = _state.value.copy(playingId = id)
                setOnCompletionListener {
                    _state.value = _state.value.copy(playingId = null)
                    release()
                    mediaPlayer = null
                }
            } catch (e: Exception) {
                release()
                mediaPlayer = null
                _state.value = _state.value.copy(errorMessage = "Playback failed: ${e.message}")
            }
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply { runCatching { stop() }; release() }
        mediaPlayer = null
        _state.value = _state.value.copy(playingId = null)
    }

    private fun deleteRecording(id: String) {
        val recording = _state.value.recordings.find { it.id == id } ?: return
        if (id == _state.value.playingId) stopPlayback()
        File(recording.filePath).delete()
        loadRecordings()
    }

    private fun loadRecordings() {
        val files = recordingsDir.listFiles { f -> f.extension == "m4a" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

        val recordings = files.map { file ->
            Recording(
                id = file.nameWithoutExtension,
                name = file.nameWithoutExtension.replace("mix_", "Mix ").replace("_", " "),
                filePath = file.absolutePath,
                durationMs = getFileDurationMs(file),
                createdAt = file.lastModified()
            )
        }
        _state.value = _state.value.copy(recordings = recordings)
    }

    private fun getFileDurationMs(file: File): Long = try {
        MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(file.absolutePath)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        }
    } catch (e: Exception) { 0L }

    override fun onCleared() {
        audioManager.unregisterAudioDeviceCallback(usbDeviceCallback)
        stopTimer()
        stopLevelPolling()
        usbRecorder?.stop()
        djmMidi.sendRecStop()
        djmMidi.stop()
        mediaRecorder?.runCatching { stop() }; mediaRecorder?.release()
        mediaPlayer?.runCatching { stop() }; mediaPlayer?.release()
        super.onCleared()
    }
}

private fun AudioDeviceInfo.isUsbInput(): Boolean =
    isSource && (type == AudioDeviceInfo.TYPE_USB_DEVICE
            || type == AudioDeviceInfo.TYPE_USB_HEADSET
            || type == AudioDeviceInfo.TYPE_USB_ACCESSORY)

private fun AudioDeviceInfo.toUsbAudioDevice() = UsbAudioDevice(
    id = id,
    name = productName.toString(),
    sampleRates = sampleRates.toList(),
    channelCounts = channelCounts.toList()
)
