package de.galonga.audiotoolbox.app.ui.screens.generator

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.audio.WaveformType
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

private const val MIN_TONE_HZ = 10.0
private const val MAX_TONE_HZ = 20000.0

private fun hzToSliderFraction(hz: Float): Float =
    (ln(hz / MIN_TONE_HZ) / ln(MAX_TONE_HZ / MIN_TONE_HZ)).toFloat().coerceIn(0f, 1f)

private fun sliderFractionToHz(fraction: Float): Float =
    (MIN_TONE_HZ * (MAX_TONE_HZ / MIN_TONE_HZ).pow(fraction.toDouble())).toFloat()

@Composable
fun SignalGeneratorScreen(viewModel: SignalGeneratorViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    // Stop playback whenever this screen leaves composition (e.g. switching bottom-nav tabs).
    DisposableEffect(Unit) {
        onDispose { viewModel.onAction(GeneratorAction.Stop) }
    }

    SignalGeneratorScreenHolder(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignalGeneratorScreenHolder(
    uiState: GeneratorUiState,
    onAction: (GeneratorAction) -> Unit
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.generator_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = RbSpacing.space16)
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = RbSpacing.space16)
            )
        }

        GeneratorCard(titleRes = R.string.generator_waveform_card_title) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(RbSpacing.space8),
                verticalArrangement = Arrangement.spacedBy(RbSpacing.space8)
            ) {
                WaveformType.entries.forEach { type ->
                    FilterChip(
                        selected = uiState.waveform == type,
                        onClick = { onAction(GeneratorAction.SetWaveform(type)) },
                        label = { Text(stringResource(type.labelRes)) }
                    )
                }
            }
        }

        if (uiState.waveform.isTonal) {
            GeneratorCard(titleRes = R.string.generator_frequency_card_title) {
                Slider(
                    value = hzToSliderFraction(uiState.frequencyHz),
                    onValueChange = { onAction(GeneratorAction.SetFrequency(sliderFractionToHz(it))) }
                )
                Text(
                    text = "${uiState.frequencyHz.roundToInt()} Hz",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (uiState.waveform == WaveformType.SWEEP) {
            SweepCard(uiState, onAction)
        }

        GeneratorCard(titleRes = R.string.generator_amplitude_card_title) {
            Slider(
                value = uiState.amplitude,
                onValueChange = { onAction(GeneratorAction.SetAmplitude(it)) }
            )
            Text(
                text = "${(uiState.amplitude * 100).roundToInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = RbSpacing.space16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledIconButton(
                onClick = { onAction(if (uiState.isPlaying) GeneratorAction.Stop else GeneratorAction.Start) },
                modifier = Modifier.size(72.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (uiState.isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (uiState.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = stringResource(
                        if (uiState.isPlaying) R.string.generator_stop_action else R.string.generator_play_action
                    ),
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = stringResource(if (uiState.isPlaying) R.string.generator_playing else R.string.generator_tap_to_play),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = RbSpacing.space8)
            )
            Text(
                text = stringResource(R.string.generator_volume_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = RbSpacing.space4)
            )
        }
    }
}

@Composable
private fun SweepCard(uiState: GeneratorUiState, onAction: (GeneratorAction) -> Unit) {
    var startText by rememberSaveable { mutableStateOf(uiState.sweepStartHz.roundToInt().toString()) }
    var endText by rememberSaveable { mutableStateOf(uiState.sweepEndHz.roundToInt().toString()) }

    fun onStartChange(text: String) {
        startText = text
        val hz = text.toFloatOrNull()
        if (hz != null && hz in MIN_TONE_HZ.toFloat()..MAX_TONE_HZ.toFloat()) {
            onAction(GeneratorAction.SetSweepStart(hz))
        }
    }

    fun onEndChange(text: String) {
        endText = text
        val hz = text.toFloatOrNull()
        if (hz != null && hz in MIN_TONE_HZ.toFloat()..MAX_TONE_HZ.toFloat()) {
            onAction(GeneratorAction.SetSweepEnd(hz))
        }
    }

    GeneratorCard(titleRes = R.string.generator_sweep_card_title) {
        Column(verticalArrangement = Arrangement.spacedBy(RbSpacing.space12)) {
            OutlinedTextField(
                value = startText,
                onValueChange = ::onStartChange,
                label = { Text(stringResource(R.string.generator_sweep_start_label)) },
                suffix = { Text("Hz") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endText,
                onValueChange = ::onEndChange,
                label = { Text(stringResource(R.string.generator_sweep_end_label)) },
                suffix = { Text("Hz") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.generator_sweep_duration_label, uiState.sweepDurationSeconds.roundToInt()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = uiState.sweepDurationSeconds,
                valueRange = 1f..20f,
                onValueChange = { onAction(GeneratorAction.SetSweepDuration(it)) }
            )
            if (uiState.isPlaying) {
                Text(
                    text = stringResource(R.string.generator_sweep_current_label, uiState.currentSweepFrequencyHz.roundToInt()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun GeneratorCard(@StringRes titleRes: Int, content: @Composable () -> Unit) {
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
