package de.galonga.audiotoolbox.app.settings

import androidx.annotation.StringRes
import de.galonga.audiotoolbox.app.R

enum class ThemeMode(@StringRes val labelRes: Int) {
    SYSTEM(R.string.label_system_default),
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark)
}

/** Language names are shown in their own language regardless of the current UI locale — not translated. */
enum class AppLanguage(val tag: String, @StringRes val labelRes: Int?, val nativeName: String?) {
    SYSTEM("system", R.string.label_system_default, null),
    ENGLISH("en", null, "English"),
    GERMAN("de", null, "Deutsch")
}
