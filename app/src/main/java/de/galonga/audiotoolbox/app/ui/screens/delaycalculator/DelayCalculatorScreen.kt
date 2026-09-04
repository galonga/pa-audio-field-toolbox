package de.galonga.audiotoolbox.app.ui.screens.delaycalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.calculators.DelayMath
import de.galonga.audiotoolbox.app.calculators.formatOrDash
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.token.RbSpacing

private enum class DistanceUnit(val label: String) { METERS("m"), FEET("ft") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayCalculatorScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tool_delay_calculator_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        ScreenContentContainer(modifier = Modifier.padding(padding)) {
            DelayCard()
        }
    }
}

@Composable
private fun DelayCard() {
    var unit by rememberSaveable { mutableStateOf(DistanceUnit.METERS) }
    var distanceText by rememberSaveable { mutableStateOf("") }
    var delayText by rememberSaveable { mutableStateOf("") }

    fun distanceToMs(distance: Double) =
        if (unit == DistanceUnit.METERS) DelayMath.metersToMs(distance) else DelayMath.feetToMs(distance)

    fun msToDistance(ms: Double) =
        if (unit == DistanceUnit.METERS) DelayMath.msToMeters(ms) else DelayMath.msToFeet(ms)

    fun onDistance(text: String) {
        distanceText = text
        val d = text.toDoubleOrNull()
        delayText = when {
            text.isBlank() -> ""
            d == null -> "—"
            else -> distanceToMs(d).formatOrDash(2)
        }
    }

    fun onDelay(text: String) {
        delayText = text
        val ms = text.toDoubleOrNull()
        distanceText = when {
            text.isBlank() -> ""
            ms == null -> "—"
            else -> msToDistance(ms).formatOrDash(2)
        }
    }

    fun onUnitChange(newUnit: DistanceUnit) {
        if (newUnit == unit) return
        unit = newUnit
        val ms = delayText.toDoubleOrNull()
        if (ms != null) distanceText = msToDistance(ms).formatOrDash(2)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RbSpacing.space16),
            verticalArrangement = Arrangement.spacedBy(RbSpacing.space12)
        ) {
            Text(text = stringResource(R.string.delay_card_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.delay_speed_of_sound_label, DelayMath.SPEED_OF_SOUND_M_PER_S.formatOrDash(0)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(RbSpacing.space8)) {
                FilterChip(
                    selected = unit == DistanceUnit.METERS,
                    onClick = { onUnitChange(DistanceUnit.METERS) },
                    label = { Text(stringResource(R.string.delay_unit_meters)) }
                )
                FilterChip(
                    selected = unit == DistanceUnit.FEET,
                    onClick = { onUnitChange(DistanceUnit.FEET) },
                    label = { Text(stringResource(R.string.delay_unit_feet)) }
                )
            }
            OutlinedTextField(
                value = distanceText,
                onValueChange = ::onDistance,
                label = { Text(stringResource(R.string.delay_distance_label)) },
                suffix = { Text(unit.label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = delayText,
                onValueChange = ::onDelay,
                label = { Text(stringResource(R.string.delay_time_label)) },
                suffix = { Text("ms") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
