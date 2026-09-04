package de.galonga.audiotoolbox.app.settings

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/** Persists user-facing app settings (theme, language) and exposes them reactively. */
class SettingsRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        ThemeMode.entries.firstOrNull { it.name == prefs.getString(KEY_THEME_MODE, null) } ?: ThemeMode.SYSTEM
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, AppLanguage.SYSTEM.tag) ?: AppLanguage.SYSTEM.tag)
    val language: StateFlow<String> = _language.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(prefs.getBoolean(KEY_DYNAMIC_COLOR, true))
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun setLanguage(tag: String) {
        prefs.edit().putString(KEY_LANGUAGE, tag).apply()
        _language.value = tag
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _dynamicColorEnabled.value = enabled
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color_enabled"

        /** Synchronous read for MainActivity.attachBaseContext(), before the DI graph is available. */
        fun readLanguageTag(context: Context): String =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_LANGUAGE, AppLanguage.SYSTEM.tag) ?: AppLanguage.SYSTEM.tag
    }
}

/** Wraps a Context so its resources resolve against the given language tag instead of the device locale. */
fun Context.withLocale(languageTag: String): Context {
    val locale = Locale.forLanguageTag(languageTag)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}
