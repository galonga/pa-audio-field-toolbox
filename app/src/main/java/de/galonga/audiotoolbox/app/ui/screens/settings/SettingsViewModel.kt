package de.galonga.audiotoolbox.app.ui.screens.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.galonga.audiotoolbox.app.settings.SettingsRepository
import de.galonga.audiotoolbox.app.settings.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: String = "system",
    val dynamicColorEnabled: Boolean = true
)

sealed interface SettingsAction {
    data class SetThemeMode(val mode: ThemeMode) : SettingsAction
    data class SetLanguage(val tag: String) : SettingsAction
    data class SetDynamicColorEnabled(val enabled: Boolean) : SettingsAction
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    val state: StateFlow<SettingsUiState> = combine(
        repository.themeMode, repository.language, repository.dynamicColorEnabled
    ) { themeMode, language, dynamicColorEnabled ->
        SettingsUiState(themeMode = themeMode, language = language, dynamicColorEnabled = dynamicColorEnabled)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SetThemeMode -> repository.setThemeMode(action.mode)
            is SettingsAction.SetLanguage -> repository.setLanguage(action.tag)
            is SettingsAction.SetDynamicColorEnabled -> repository.setDynamicColorEnabled(action.enabled)
        }
    }
}
