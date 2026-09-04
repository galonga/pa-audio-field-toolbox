package de.galonga.audiotoolbox.app.ui.screens.settings

import android.app.Activity
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.settings.AppLanguage
import de.galonga.audiotoolbox.app.settings.ThemeMode
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_screen)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        ScreenContentContainer(modifier = Modifier.padding(padding)) {
            ThemeCard(
                selected = uiState.themeMode,
                onSelect = { viewModel.onAction(SettingsAction.SetThemeMode(it)) }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                DynamicColorCard(
                    enabled = uiState.dynamicColorEnabled,
                    onToggle = { viewModel.onAction(SettingsAction.SetDynamicColorEnabled(it)) }
                )
            }
            LanguageCard(
                selectedTag = uiState.language,
                onSelect = { tag ->
                    viewModel.onAction(SettingsAction.SetLanguage(tag))
                    (context as? Activity)?.recreate()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeCard(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    SettingsCard(titleRes = R.string.settings_theme_title) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(RbSpacing.space8),
            verticalArrangement = Arrangement.spacedBy(RbSpacing.space8)
        ) {
            ThemeMode.entries.forEach { mode ->
                FilterChip(
                    selected = selected == mode,
                    onClick = { onSelect(mode) },
                    label = { Text(stringResource(mode.labelRes)) }
                )
            }
        }
    }
}

@Composable
private fun DynamicColorCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    SettingsCard(titleRes = R.string.settings_dynamic_color_title) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_dynamic_color_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f).padding(end = RbSpacing.space16)
            )
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageCard(selectedTag: String, onSelect: (String) -> Unit) {
    SettingsCard(titleRes = R.string.settings_language_title) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(RbSpacing.space8),
            verticalArrangement = Arrangement.spacedBy(RbSpacing.space8)
        ) {
            AppLanguage.entries.forEach { language ->
                val label = language.labelRes?.let { stringResource(it) } ?: language.nativeName.orEmpty()
                FilterChip(
                    selected = selectedTag == language.tag,
                    onClick = { onSelect(language.tag) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(@StringRes titleRes: Int, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = RbSpacing.space16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(RbSpacing.space16)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = RbSpacing.space8)
            )
            content()
        }
    }
}
