package de.minmon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import de.minmon.app.data.ThemeMode
import de.minmon.app.ui.MinMonApp
import de.minmon.app.util.ThemeConfiguration
import de.minmon.app.util.ThemeConfigurator
import de.minmon.design.theme.MinMonTheme
import de.minmon.design.utils.Preferences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var keepSplashOnScreen = true
    private val splashScreenDuration = 2000L

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashOnScreen }

        enableEdgeToEdge()

        lifecycleScope.launch {
            kotlinx.coroutines.delay(splashScreenDuration)
            keepSplashOnScreen = false
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            // val displayFeatures = calculateDisplayFeatures(this)
            val themeMode = Preferences.getString(Preferences.themeModeKey, ThemeMode.AUTO.toString())!!.toInt()
            val themeDynColorMode = Preferences.getBoolean(Preferences.themeDynColorModeKey, false)

            val themConfig = remember { mutableStateOf(ThemeConfiguration(themeMode, themeDynColorMode)) }

            LaunchedEffect("theme") {
                ThemeConfigurator.sharedFlow.onEach {
                    when (it) {
                        is ThemeConfigurator.ThemeConfig.ChangeThemeConfiguration -> {
                            themConfig.value = it.themeConfig
                        }
                    }
                }.launchIn(this)
            }


            MinMonTheme(
                darkTheme = when (themConfig.value.themeMode) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    else -> isSystemInDarkTheme()
                },
                dynamicColor = themConfig.value.themeDynColorMode
            ) {
                MinMonApp(windowSizeClass)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
val CompactWindowSizeClass = WindowSizeClass.calculateFromSize(size = DpSize(400.dp, 900.dp))
