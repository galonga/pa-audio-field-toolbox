package de.galonga.audiotoolbox.app.ui.screens.analyzer

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.components.ExpressivePrimaryButton
import de.galonga.audiotoolbox.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun AnalyzerScreen(viewModel: AnalyzerViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onAction(AnalyzerAction.Start)
    }

    // Stop the mic whenever this screen leaves composition (e.g. switching bottom-nav tabs).
    DisposableEffect(Unit) {
        onDispose { viewModel.onAction(AnalyzerAction.Stop) }
    }

    AnalyzerScreenHolder(
        uiState = uiState,
        onStartClick = {
            val permission = Manifest.permission.RECORD_AUDIO
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                viewModel.onAction(AnalyzerAction.Start)
            } else {
                permissionLauncher.launch(permission)
            }
        },
        onStopClick = { viewModel.onAction(AnalyzerAction.Stop) },
        onTapTempoClick = { viewModel.onAction(AnalyzerAction.TapTempo) },
        onResetTapTempoClick = { viewModel.onAction(AnalyzerAction.ResetTapTempo) }
    )
}

@Composable
fun AnalyzerScreenHolder(
    uiState: AnalyzerUiState,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onTapTempoClick: () -> Unit,
    onResetTapTempoClick: () -> Unit
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.analyzer_screen),
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

        AnalyzerCard(titleRes = R.string.analyzer_bpm_title) {
            BpmCounter(
                autoBpm = uiState.autoBpm,
                tapBpm = uiState.tapBpm,
                onTapTempoClick = onTapTempoClick,
                onResetTapTempoClick = onResetTapTempoClick
            )
        }
        AnalyzerCard(titleRes = R.string.analyzer_waveform_title) { WaveformPlot(uiState.waveform) }
        AnalyzerCard(titleRes = R.string.analyzer_spectrum_title) { SpectrumPlot(uiState.spectrumDb, uiState.sampleRate, uiState.fftSize) }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = RbSpacing.space16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledIconButton(
                onClick = if (uiState.isRunning) onStopClick else onStartClick,
                modifier = Modifier.size(72.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (uiState.isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = stringResource(
                        if (uiState.isRunning) R.string.analyzer_stop_action else R.string.analyzer_start_action
                    ),
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = stringResource(if (uiState.isRunning) R.string.analyzer_listening else R.string.analyzer_tap_to_start),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = RbSpacing.space8)
            )
        }

    }
}

@Composable
private fun AnalyzerCard(@StringRes titleRes: Int, content: @Composable () -> Unit) {
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

// ─────────────────────────────────────────────────────────────────────────────
// BPM counter — automatic (onset detection + autocorrelation) and manual tap tempo
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BpmCounter(
    autoBpm: Double?,
    tapBpm: Double?,
    onTapTempoClick: () -> Unit,
    onResetTapTempoClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BpmReadout(
                label = stringResource(R.string.analyzer_bpm_auto_label),
                value = autoBpm?.let { stringResource(R.string.analyzer_bpm_value, it.roundToInt()) }
                    ?: stringResource(R.string.analyzer_bpm_detecting)
            )
            BpmReadout(
                label = stringResource(R.string.analyzer_bpm_tap_label),
                value = tapBpm?.let { stringResource(R.string.analyzer_bpm_value, it.roundToInt()) } ?: "—"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = RbSpacing.space16),
            horizontalArrangement = Arrangement.spacedBy(RbSpacing.space8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExpressivePrimaryButton(
                onClick = onTapTempoClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.analyzer_tap_tempo_button))
            }
            TextButton(onClick = onResetTapTempoClick) {
                Text(stringResource(R.string.action_clear))
            }
        }
    }
}

@Composable
private fun BpmReadout(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Waveform (time domain)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WaveformPlot(samples: FloatArray) {
    val waveColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val midY = size.height / 2f
        drawLine(axisColor, Offset(0f, midY), Offset(size.width, midY), strokeWidth = 1.dp.toPx())
        if (samples.isEmpty()) return@Canvas

        // Downsample for a cheap draw — full sample resolution isn't visually necessary.
        val maxPoints = 512
        val step = (samples.size / maxPoints).coerceAtLeast(1)
        val pointCount = samples.size / step
        val pxStep = size.width / pointCount.coerceAtLeast(1)

        val path = Path()
        var px = 0
        var i = 0
        while (i < samples.size) {
            val x = px * pxStep
            val y = midY - samples[i].coerceIn(-1f, 1f) * midY
            if (px == 0) path.moveTo(x, y) else path.lineTo(x, y)
            i += step
            px++
        }
        drawPath(path, color = waveColor, style = Stroke(width = 2.dp.toPx()))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// FFT spectrum (frequency domain) with a log frequency scale
// ─────────────────────────────────────────────────────────────────────────────

private const val MIN_FREQ_HZ = 20.0
private const val MIN_DB = -80f
private const val MAX_DB = 0f
private val FREQ_TICKS_HZ = listOf(20.0, 50.0, 100.0, 200.0, 500.0, 1000.0, 2000.0, 5000.0, 10000.0, 20000.0)
private val DB_TICKS = listOf(0f, -20f, -40f, -60f, -80f)

private fun formatFreqLabel(hz: Double): String =
    if (hz >= 1000) "${(hz / 1000).toInt()}k" else hz.toInt().toString()

@Composable
private fun SpectrumPlot(spectrumDb: FloatArray, sampleRate: Int, fftSize: Int) {
    val textMeasurer = rememberTextMeasurer()
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val traceColor = MaterialTheme.colorScheme.primary
    val labelStyle = MaterialTheme.typography.labelSmall

    val maxFreq = (sampleRate / 2).toDouble().coerceAtMost(20000.0)
    val binHz = sampleRate.toDouble() / fftSize

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val axisLabelWidth = 56f
        val axisLabelHeight = 28f
        val plotLeft = axisLabelWidth
        val plotBottom = size.height - axisLabelHeight
        val plotWidth = size.width - plotLeft

        fun xForFreq(freq: Double): Float =
            plotLeft + (ln(freq / MIN_FREQ_HZ) / ln(maxFreq / MIN_FREQ_HZ)).toFloat() * plotWidth

        fun yForDb(db: Float): Float =
            plotBottom - ((db - MIN_DB) / (MAX_DB - MIN_DB)) * plotBottom

        DB_TICKS.forEach { db ->
            val y = yForDb(db)
            drawLine(gridColor, Offset(plotLeft, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
            val label = textMeasurer.measure("${db.toInt()}", labelStyle)
            drawText(label, color = labelColor, topLeft = Offset(0f, y - label.size.height / 2f))
        }

        FREQ_TICKS_HZ.filter { it <= maxFreq }.forEach { freq ->
            val x = xForFreq(freq)
            drawLine(gridColor, Offset(x, 0f), Offset(x, plotBottom), strokeWidth = 1.dp.toPx())
            val label = textMeasurer.measure(formatFreqLabel(freq), labelStyle)
            drawText(label, color = labelColor, topLeft = Offset(x - label.size.width / 2f, plotBottom + 4.dp.toPx()))
        }

        if (spectrumDb.isNotEmpty()) {
            val path = Path()
            val pointCount = plotWidth.toInt().coerceAtLeast(1)
            for (px in 0..pointCount) {
                val frac = px / pointCount.toDouble()
                val freq = MIN_FREQ_HZ * (maxFreq / MIN_FREQ_HZ).pow(frac)
                val binIndex = (freq / binHz).toInt().coerceIn(0, spectrumDb.size - 1)
                val db = spectrumDb[binIndex].coerceIn(MIN_DB, MAX_DB)
                val x = plotLeft + px.toFloat()
                val y = yForDb(db)
                if (px == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color = traceColor, style = Stroke(width = 2.dp.toPx()))
        }
    }
}
