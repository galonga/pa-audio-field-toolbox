package de.galonga.audiotoolbox.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import de.galonga.audiotoolbox.app.settings.AppLanguage
import de.galonga.audiotoolbox.app.settings.SettingsRepository
import de.galonga.audiotoolbox.app.settings.ThemeMode
import de.galonga.audiotoolbox.app.settings.withLocale
import de.galonga.audiotoolbox.app.ui.AudioToolboxApp
import de.galonga.audiotoolbox.design.theme.AudioToolboxTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private var keepSplashOnScreen = true
    private val splashScreenDuration = 2000L

    override fun attachBaseContext(newBase: Context) {
        val languageTag = SettingsRepository.readLanguageTag(newBase)
        val context = if (languageTag == AppLanguage.SYSTEM.tag) newBase else newBase.withLocale(languageTag)
        super.attachBaseContext(context)
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            kotlinx.coroutines.delay(splashScreenDuration)
            keepSplashOnScreen = false
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val settingsRepository = koinInject<SettingsRepository>()
            val themeMode by settingsRepository.themeMode.collectAsStateWithLifecycle()
            val dynamicColorEnabled by settingsRepository.dynamicColorEnabled.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AudioToolboxTheme(
                darkTheme = darkTheme,
                dynamicColor = dynamicColorEnabled
            ) {
                AudioToolboxApp(windowSizeClass)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val CompactWindowSizeClass = WindowSizeClass.calculateFromSize(size = DpSize(400.dp, 900.dp))
