package de.galonga.audiotoolbox.app.ui.screens.generator

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.galonga.audiotoolbox.app.audio.GeneratorParams
import de.galonga.audiotoolbox.app.audio.SignalGeneratorEngine
import de.galonga.audiotoolbox.app.audio.WaveformType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Immutable
data class GeneratorUiState(
    val isPlaying: Boolean = false,
    val errorMessage: String? = null,
    val waveform: WaveformType = WaveformType.SINE,
    val frequencyHz: Float = 440f,
    val amplitude: Float = 0.5f,
    val sweepStartHz: Float = 20f,
    val sweepEndHz: Float = 20000f,
    val sweepDurationSeconds: Float = 8f,
    val currentSweepFrequencyHz: Float = 0f
)

sealed interface GeneratorAction {
    object Start : GeneratorAction
    object Stop : GeneratorAction
    data class SetWaveform(val waveform: WaveformType) : GeneratorAction
    data class SetFrequency(val hz: Float) : GeneratorAction
    data class SetAmplitude(val amplitude: Float) : GeneratorAction
    data class SetSweepStart(val hz: Float) : GeneratorAction
    data class SetSweepEnd(val hz: Float) : GeneratorAction
    data class SetSweepDuration(val seconds: Float) : GeneratorAction
}

class SignalGeneratorViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(GeneratorUiState())
    val state: StateFlow<GeneratorUiState> get() = _state

    private var engine: SignalGeneratorEngine? = null

    fun onAction(action: GeneratorAction) {
        when (action) {
            is GeneratorAction.Start -> start()
            is GeneratorAction.Stop -> stop()
            is GeneratorAction.SetWaveform -> _state.value = _state.value.copy(waveform = action.waveform)
            is GeneratorAction.SetFrequency -> _state.value = _state.value.copy(frequencyHz = action.hz)
            is GeneratorAction.SetAmplitude -> _state.value = _state.value.copy(amplitude = action.amplitude)
            is GeneratorAction.SetSweepStart -> _state.value = _state.value.copy(sweepStartHz = action.hz)
            is GeneratorAction.SetSweepEnd -> _state.value = _state.value.copy(sweepEndHz = action.hz)
            is GeneratorAction.SetSweepDuration -> _state.value = _state.value.copy(sweepDurationSeconds = action.seconds)
        }
    }

    private fun start() {
        if (engine != null) return
        engine = SignalGeneratorEngine(
            scope = viewModelScope,
            paramsProvider = {
                val s = _state.value
                GeneratorParams(
                    waveform = s.waveform,
                    frequencyHz = s.frequencyHz,
                    amplitude = s.amplitude,
                    sweepStartHz = s.sweepStartHz,
                    sweepEndHz = s.sweepEndHz,
                    sweepDurationSeconds = s.sweepDurationSeconds
                )
            },
            onSweepFrequencyUpdate = { freq ->
                _state.value = _state.value.copy(currentSweepFrequencyHz = freq)
            },
            onError = { msg ->
                _state.value = _state.value.copy(isPlaying = false, errorMessage = msg)
            },
            onStopped = {
                _state.value = _state.value.copy(isPlaying = false)
            }
        )
        engine!!.start()
        _state.value = _state.value.copy(isPlaying = true, errorMessage = null)
    }

    private fun stop() {
        engine?.stop()
        engine = null
    }

    override fun onCleared() {
        engine?.stop()
        super.onCleared()
    }
}
