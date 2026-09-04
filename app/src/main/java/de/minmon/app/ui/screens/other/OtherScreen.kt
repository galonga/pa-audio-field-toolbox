package de.minmon.app.ui.screens.other

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.minmon.app.R
import de.minmon.app.data.ThemeMode
import de.minmon.app.ui.components.EnhancedCard
import de.minmon.app.ui.components.EnhancedCardModel
import de.minmon.app.ui.components.IconAnimation
import de.minmon.app.ui.components.StorybookLayoutType
import de.minmon.app.ui.components.about.FlexibleRow
import de.minmon.app.ui.components.about.ScreenContentContainer
import de.minmon.app.ui.components.pref.CheckboxPref
import de.minmon.app.ui.components.pref.ListPreference
import de.minmon.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OtherScreen(windowSizeClass: WindowSizeClass, viewModel: OtherViewModel = koinViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        OtherScreenHolder(
            uiState = uiState,
            onOtherAction = viewModel::onOtherAction,
            windowSizeClass = windowSizeClass,

            )
    }
}

@Composable
fun OtherScreenHolder(
    uiState: OtherScreenUiState,
    onOtherAction: (OtherAction) -> Unit,
    windowSizeClass: WindowSizeClass,
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {
        EnhancedCard(
            model = EnhancedCardModel(
                title = null,
                accentColor = MaterialTheme.colorScheme.primary,
                layoutType = StorybookLayoutType.TONAL_SURFACE
            )
        ) {
            FlexibleRow(
                title = stringResource(R.string.app_name),
                //summary = "Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                painterResource = painterResource(R.drawable.ic_launcher_foreground)
            )
        }

        EnhancedCard(
            model = EnhancedCardModel(
                title = stringResource(R.string.page_settings),
                layoutType = StorybookLayoutType.TONAL_SURFACE,
                showAccentBar = false,
                icon = Icons.Default.Settings,
                iconAnimation = IconAnimation.ROTATE
            )
        ) {
            Column(modifier = Modifier.padding(RbSpacing.space16)) {
                ListPreference(
                    title = stringResource(R.string.theme_mode),
                    entries = listOf(
                        stringResource(R.string.theme_system),
                        stringResource(R.string.theme_light),
                        stringResource(R.string.theme_dark)
                    ),
                    values = (0..2).map { it.toString() },
                    defaultValue = ThemeMode.AUTO.toString()
                ) {
                    onOtherAction(OtherAction.OnChangeThemeMode(it.toInt()))
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    CheckboxPref(
                        title = stringResource(R.string.theme_dynamic_colors_mode),
                        summary = stringResource(id = R.string.theme_dynamic_colors_mode_desc),
                        defaultValue = uiState.themeDynColorMode,
                        onCheckedChange = {
                            onOtherAction(OtherAction.OnChangeThemeDynColorMode(it))
                        }
                    )
                }
            }
        }
    }
}
