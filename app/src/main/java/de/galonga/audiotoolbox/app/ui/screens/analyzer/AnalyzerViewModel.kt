package de.galonga.audiotoolbox.app.ui.screens.analyzer

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.galonga.audiotoolbox.app.audio.MicAnalyzerEngine
import de.galonga.audiotoolbox.app.audio.TapTempoCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class AnalyzerUiState(
    val isRunning: Boolean = false,
    val errorMessage: String? = null,
    val waveform: FloatArray = FloatArray(0),
    val spectrumDb: FloatArray = FloatArray(0),
    val sampleRate: Int = MicAnalyzerEngine.SAMPLE_RATE,
    val fftSize: Int = MicAnalyzerEngine.FFT_SIZE,
    val autoBpm: Double? = null,
    val tapBpm: Double? = null
)

sealed interface AnalyzerAction {
    object Start : AnalyzerAction
    object Stop : AnalyzerAction
    object TapTempo : AnalyzerAction
    object ResetTapTempo : AnalyzerAction
}

class AnalyzerViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(AnalyzerUiState())
    val state: StateFlow<AnalyzerUiState> get() = _state

    private var engine: MicAnalyzerEngine? = null
    private val tapTempoCalculator = TapTempoCalculator()

    fun onAction(action: AnalyzerAction) {
        when (action) {
            is AnalyzerAction.Start -> start()
            is AnalyzerAction.Stop -> stop()
            is AnalyzerAction.TapTempo -> tap()
            is AnalyzerAction.ResetTapTempo -> resetTap()
        }
    }

    private fun start() {
        if (engine != null) return
        engine = MicAnalyzerEngine(
            scope = viewModelScope,
            onData = { waveform, spectrum, bpm ->
                _state.value = _state.value.copy(waveform = waveform, spectrumDb = spectrum, autoBpm = bpm, errorMessage = null)
            },
            onError = { msg ->
                _state.value = _state.value.copy(isRunning = false, errorMessage = msg)
            },
            onStopped = {
                _state.value = _state.value.copy(isRunning = false)
            }
        )
        engine!!.start()
        _state.value = _state.value.copy(isRunning = true, errorMessage = null)
    }

    private fun stop() {
        engine?.stop()
        engine = null
        _state.value = _state.value.copy(autoBpm = null)
    }

    private fun tap() {
        val bpm = tapTempoCalculator.onTap(System.currentTimeMillis())
        _state.value = _state.value.copy(tapBpm = bpm)
    }

    private fun resetTap() {
        tapTempoCalculator.reset()
        _state.value = _state.value.copy(tapBpm = null)
    }

    override fun onCleared() {
        engine?.stop()
        super.onCleared()
    }
}
