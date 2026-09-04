package de.minmon.app.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object ThemeConfigurator {
    private val _sharedFlow =
        MutableSharedFlow<ThemeConfig>(extraBufferCapacity = 1)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun dispatch(themeConfig: ThemeConfig) {
        _sharedFlow.tryEmit(themeConfig)
    }

    sealed class ThemeConfig {
        data class ChangeThemeConfiguration(val themeConfig: ThemeConfiguration) : ThemeConfig()
    }
}

data class ThemeConfiguration(val themeMode: Int, val themeDynColorMode: Boolean)
