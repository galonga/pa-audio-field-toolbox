package de.minmon.app.ui.screens.other


import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.minmon.app.data.ThemeMode
import de.minmon.app.util.ThemeConfiguration
import de.minmon.app.util.ThemeConfigurator
import de.minmon.design.utils.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OtherViewModel : ViewModel() {

    private val _state = MutableStateFlow(OtherScreenUiState())

    val state: StateFlow<OtherScreenUiState>
        get() = _state

    init {
        viewModelScope.launch {
            _state.value = OtherScreenUiState(
                themeMode = Preferences.getString(Preferences.themeModeKey, ThemeMode.AUTO.toString())!!.toInt(),
                themeDynColorMode = Preferences.getBoolean(Preferences.themeDynColorModeKey, false),
                isLoading = false
            )
        }
    }

    fun onOtherAction(action: OtherAction) {
        when (action) {
            is OtherAction.InitView -> initView()
            is OtherAction.OnChangeThemeMode -> onChangeThemeMode(action.themeMode)
            is OtherAction.OnChangeThemeDynColorMode -> onChangeThemeDynColorMode(action.themeDynColorMode)
        }
    }

    private fun onChangeThemeDynColorMode(dynColorMode: Boolean) {
        Preferences.edit { putBoolean(Preferences.themeDynColorModeKey, dynColorMode) }
        ThemeConfigurator.dispatch(
            ThemeConfigurator.ThemeConfig.ChangeThemeConfiguration(
                ThemeConfiguration(
                    _state.value.themeMode,
                    dynColorMode
                )
            )
        )
        _state.value = _state.value.copy(
            themeDynColorMode = dynColorMode
        )
    }

    private fun onChangeThemeMode(mode: Int) {
        Preferences.edit { putString(Preferences.themeModeKey, mode.toString()) }
        ThemeConfigurator.dispatch(
            ThemeConfigurator.ThemeConfig.ChangeThemeConfiguration(
                ThemeConfiguration(
                    mode,
                    _state.value.themeDynColorMode
                )
            )
        )
        _state.value = _state.value.copy(
            themeMode = mode
        )
    }

    private fun initView() {
        //Todo
    }
}


@Immutable
sealed interface OtherAction {
    object InitView : OtherAction
    data class OnChangeThemeMode(val themeMode: Int) : OtherAction
    data class OnChangeThemeDynColorMode(val themeDynColorMode: Boolean) : OtherAction
}

@Immutable
data class OtherScreenUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    var themeMode: Int = ThemeMode.AUTO,
    var themeDynColorMode: Boolean = false,
)
