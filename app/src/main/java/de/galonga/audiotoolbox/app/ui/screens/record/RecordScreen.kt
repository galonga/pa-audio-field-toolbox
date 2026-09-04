package de.galonga.audiotoolbox.app.ui.screens.record

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10

@Composable
fun RecordScreen(windowSizeClass: WindowSizeClass, viewModel: RecordViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onRecordAction(RecordAction.StartRecording)
    }

    RecordScreenHolder(
        uiState = uiState,
        onRecordClick = {
            val permission = Manifest.permission.RECORD_AUDIO
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                viewModel.onRecordAction(RecordAction.StartRecording)
            } else {
                permissionLauncher.launch(permission)
            }
        },
        onStopClick = { viewModel.onRecordAction(RecordAction.StopRecording) },
        onGainChange = { viewModel.onRecordAction(RecordAction.SetGain(it)) },
        onPlayClick = { id -> viewModel.onRecordAction(RecordAction.PlayRecording(id)) },
        onStopPlayback = { viewModel.onRecordAction(RecordAction.StopPlayback) },
        onDeleteClick = { id -> viewModel.onRecordAction(RecordAction.DeleteRecording(id)) }
    )
}

@Composable
fun RecordScreenHolder(
    uiState: RecordScreenUiState,
    onRecordClick: () -> Unit,
    onStopClick: () -> Unit,
    onGainChange: (Float) -> Unit,
    onPlayClick: (String) -> Unit,
    onStopPlayback: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.record_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = RbSpacing.space16)
        )
        UsbDeviceStatusCard(usbDevice = uiState.usbDevice, midiState = uiState.midiState)

        RecordControlPanel(
            isRecording = uiState.isRecording,
            timer = uiState.formattedTimer,
            recordingSource = uiState.recordingSource,
            usbDevice = uiState.usbDevice,
            onRecordClick = onRecordClick,
            onStopClick = onStopClick
        )

        LevelAndGainPanel(
            level = uiState.inputLevel,
            gain = uiState.inputGain,
            onGainChange = onGainChange
        )

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = RbSpacing.space16)
            )
        }

        if (uiState.recordings.isNotEmpty()) {
            RecordingsList(
                recordings = uiState.recordings,
                playingId = uiState.playingId,
                onPlayClick = onPlayClick,
                onStopPlayback = onStopPlayback,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// USB device status card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UsbDeviceStatusCard(usbDevice: UsbAudioDevice?, midiState: MidiConnectionState) {
    val connected = usbDevice != null
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RbSpacing.space16),
        colors = CardDefaults.cardColors(
            containerColor = if (connected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = RbSpacing.space16, vertical = RbSpacing.space12),
            verticalArrangement = Arrangement.spacedBy(RbSpacing.space8)
        ) {
            // ── USB audio row ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RbSpacing.space12)
            ) {
                Icon(
                    imageVector = Icons.Default.Usb,
                    contentDescription = null,
                    tint = if (connected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (connected) usbDevice!!.displayName else stringResource(R.string.record_no_usb_title),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (connected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (connected) usbDevice!!.formatSummary
                        else stringResource(R.string.record_no_usb_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (connected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (connected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                )
            }

            // ── MIDI status row (only when USB audio is connected) ──
            if (connected) {
                val midiColor = when (midiState) {
                    MidiConnectionState.CONNECTED -> MaterialTheme.colorScheme.primary
                    MidiConnectionState.DISCONNECTED -> MaterialTheme.colorScheme.error
                    MidiConnectionState.UNAVAILABLE -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val midiText = stringResource(
                    when (midiState) {
                        MidiConnectionState.CONNECTED -> R.string.record_midi_connected
                        MidiConnectionState.DISCONNECTED -> R.string.record_midi_disconnected
                        MidiConnectionState.UNAVAILABLE -> R.string.record_midi_unavailable
                    }
                )
                Text(
                    text = midiText,
                    style = MaterialTheme.typography.bodySmall,
                    color = midiColor.copy(alpha = 0.85f)
                )
            }

            // ── Physical switch reminder ──
            if (!connected) {
                Text(
                    text = stringResource(R.string.record_usb_switch_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Record button + timer
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecordControlPanel(
    isRecording: Boolean,
    timer: String,
    recordingSource: RecordingSource,
    usbDevice: UsbAudioDevice?,
    onRecordClick: () -> Unit,
    onStopClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = RbSpacing.space24),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timer,
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light,
                fontSize = 56.sp
            ),
            color = if (isRecording) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(RbSpacing.space24))

        Box(contentAlignment = Alignment.Center) {
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(pulseScale)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                )
            }
            FilledIconButton(
                onClick = if (isRecording) onStopClick else onRecordClick,
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = stringResource(
                        if (isRecording) R.string.record_stop_action else R.string.record_start_action
                    ),
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(RbSpacing.space12))

        val statusTextRes = when {
            isRecording && recordingSource == RecordingSource.USB_MIXER -> R.string.record_recording_usb
            isRecording -> R.string.record_recording_mic
            usbDevice != null -> R.string.record_tap_usb
            else -> R.string.record_tap_mic
        }
        Text(
            text = stringResource(statusTextRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (isRecording) {
            Spacer(modifier = Modifier.height(RbSpacing.space8))
            Surface(
                shape = RoundedCornerShape(50),
                color = if (recordingSource == RecordingSource.USB_MIXER)
                    MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = RbSpacing.space12, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (recordingSource == RecordingSource.USB_MIXER)
                            Icons.Default.Usb else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = if (recordingSource == RecordingSource.USB_MIXER) "USB" else "MIC",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Level meter + gain slider card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LevelAndGainPanel(
    level: Float,
    gain: Float,
    onGainChange: (Float) -> Unit
) {
    // dB label: 20·log10(gain), or "-∞" for gain≈0
    val gainDb = if (gain < 0.01f) null else 20f * log10(gain)
    val gainLabel = gainDb?.let { db ->
        if (db >= 0f) "+%.1f dB".format(db) else "%.1f dB".format(db)
    } ?: "-∞ dB"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RbSpacing.space16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = RbSpacing.space16, vertical = RbSpacing.space12),
            verticalArrangement = Arrangement.spacedBy(RbSpacing.space12)
        ) {
            // ── Level meter ──
            Text(
                text = stringResource(R.string.record_level_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LevelMeter(
                level = level,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )

            // ── Gain slider ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.record_gain_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(36.dp)
                )
                Slider(
                    value = gain,
                    onValueChange = onGainChange,
                    valueRange = 0f..2f,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = gainLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(56.dp),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Segmented VU meter drawn on a Canvas.
 *
 * Segments:
 *  0–11  (  0–60%) green
 *  12–15 ( 60–80%) amber
 *  16–19 (80–100%) red
 *
 * A peak-hold marker stays at the highest reached position and decays after 1.5s.
 */
@Composable
private fun LevelMeter(level: Float, modifier: Modifier = Modifier) {
    val animatedLevel by animateFloatAsState(
        targetValue = level,
        animationSpec = tween(durationMillis = 80, easing = LinearEasing),
        label = "level"
    )

    var peakLevel by remember { mutableFloatStateOf(0f) }
    var peakTimestamp by remember { mutableLongStateOf(0L) }

    LaunchedEffect(animatedLevel) {
        val now = System.currentTimeMillis()
        if (animatedLevel >= peakLevel) {
            peakLevel = animatedLevel
            peakTimestamp = now
        } else if (now - peakTimestamp > 1500L) {
            // Decay peak slowly after hold period
            peakLevel = (peakLevel - 0.005f).coerceAtLeast(0f)
        }
    }

    val segmentCount = 20
    val greenEnd = 12   // segments 0–11
    val amberEnd = 16   // segments 12–15

    val colorGreen = Color(0xFF00C853)
    val colorAmber = Color(0xFFFFAB00)
    val colorRed   = Color(0xFFFF1744)

    Canvas(modifier = modifier) {
        val gapPx = 2.dp.toPx()
        val totalGaps = (segmentCount - 1) * gapPx
        val segWidth = (size.width - totalGaps) / segmentCount
        val corner = CornerRadius(2.dp.toPx())

        val litCount = (animatedLevel * segmentCount).toInt().coerceIn(0, segmentCount)
        val peakSeg = (peakLevel * segmentCount).toInt().coerceIn(0, segmentCount - 1)

        for (i in 0 until segmentCount) {
            val x = i * (segWidth + gapPx)
            val baseColor = when {
                i < greenEnd -> colorGreen
                i < amberEnd -> colorAmber
                else -> colorRed
            }
            val isPeak = i == peakSeg && peakLevel > 0.02f
            val isLit  = i < litCount
            val color = when {
                isPeak -> baseColor
                isLit  -> baseColor
                else   -> baseColor.copy(alpha = 0.15f)
            }
            drawRoundRect(
                color = color,
                topLeft = Offset(x, 0f),
                size = Size(segWidth, size.height),
                cornerRadius = corner
            )
        }

        // dB tick marks at -40, -20, -10, -6, 0 dB
        // Map dB to linear: linear = 10^(dB/20), then normalise 0→1 over 0..1 linear range
        val ticks = listOf(-40f, -20f, -10f, -6f, 0f)
        for (db in ticks) {
            val linear = Math.pow(10.0, db / 20.0).toFloat().coerceIn(0f, 1f)
            val tickX = linear * size.width
            drawLine(
                color = Color.White.copy(alpha = 0.25f),
                start = Offset(tickX, size.height * 0.6f),
                end = Offset(tickX, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Recordings list
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecordingsList(
    recordings: List<Recording>,
    playingId: String?,
    onPlayClick: (String) -> Unit,
    onStopPlayback: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.record_recordings_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = RbSpacing.space16, vertical = RbSpacing.space8)
        )
        recordings.forEach { recording ->
            RecordingItem(
                recording = recording,
                isPlaying = recording.id == playingId,
                onPlayClick = { onPlayClick(recording.id) },
                onStopPlayback = onStopPlayback,
                onDeleteClick = { onDeleteClick(recording.id) }
            )
            Spacer(modifier = Modifier.height(RbSpacing.space8))
        }
    }
}

@Composable
private fun RecordingItem(
    recording: Recording,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onStopPlayback: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RbSpacing.space16),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = RbSpacing.space12, vertical = RbSpacing.space8),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recording.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = recording.formattedDuration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(RbSpacing.space8))
                    Text(
                        text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                            .format(Date(recording.createdAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = if (isPlaying) onStopPlayback else onPlayClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = stringResource(
                        if (isPlaying) R.string.record_stop_playback_action else R.string.generator_play_action
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.record_delete_action),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
