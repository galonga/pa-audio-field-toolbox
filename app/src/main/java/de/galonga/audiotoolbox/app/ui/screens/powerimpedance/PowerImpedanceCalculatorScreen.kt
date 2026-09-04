package de.galonga.audiotoolbox.app.ui.screens.powerimpedance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.calculators.ElectricalQuantity
import de.galonga.audiotoolbox.app.calculators.PowerImpedanceMath
import de.galonga.audiotoolbox.app.calculators.formatOrDash
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.token.RbSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerImpedanceCalculatorScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tool_power_calculator_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        ScreenContentContainer(modifier = Modifier.padding(padding)) {
            OhmsLawCard()
        }
    }
}

@Composable
private fun OhmsLawCard() {
    var voltageText by rememberSaveable { mutableStateOf("") }
    var currentText by rememberSaveable { mutableStateOf("") }
    var resistanceText by rememberSaveable { mutableStateOf("") }
    var powerText by rememberSaveable { mutableStateOf("") }
    // Most-recently-edited valid fields, oldest first, capped at 2 — these are treated as the "known" inputs.
    var editOrder by remember { mutableStateOf(listOf<ElectricalQuantity>()) }

    fun textFor(q: ElectricalQuantity) = when (q) {
        ElectricalQuantity.VOLTAGE -> voltageText
        ElectricalQuantity.CURRENT -> currentText
        ElectricalQuantity.RESISTANCE -> resistanceText
        ElectricalQuantity.POWER -> powerText
    }

    fun setText(q: ElectricalQuantity, text: String) {
        when (q) {
            ElectricalQuantity.VOLTAGE -> voltageText = text
            ElectricalQuantity.CURRENT -> currentText = text
            ElectricalQuantity.RESISTANCE -> resistanceText = text
            ElectricalQuantity.POWER -> powerText = text
        }
    }

    fun recompute(order: List<ElectricalQuantity>) {
        val others = ElectricalQuantity.entries.filterNot { it in order }
        if (order.size < 2) {
            others.forEach { setText(it, "") }
            return
        }
        val (a, b) = order.takeLast(2)
        val va = textFor(a).toDoubleOrNull()?.takeIf { it > 0.0 }
        val vb = textFor(b).toDoubleOrNull()?.takeIf { it > 0.0 }
        if (va == null || vb == null) {
            others.forEach { setText(it, "") }
            return
        }
        val result = PowerImpedanceMath.solve(a, va, b, vb)
        others.forEach { q -> setText(q, result?.getValue(q)?.formatOrDash(3) ?: "—") }
    }

    fun onFieldChange(q: ElectricalQuantity, text: String) {
        setText(q, text)
        val isValid = text.toDoubleOrNull()?.let { it > 0.0 } == true
        editOrder = if (isValid) (editOrder.filterNot { it == q } + q).takeLast(2) else editOrder.filterNot { it == q }
        recompute(editOrder)
    }

    fun onClear() {
        editOrder = emptyList()
        voltageText = ""
        currentText = ""
        resistanceText = ""
        powerText = ""
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
            Text(text = stringResource(R.string.power_card_title), style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.power_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = voltageText,
                onValueChange = { onFieldChange(ElectricalQuantity.VOLTAGE, it) },
                label = { Text(stringResource(R.string.power_voltage_label)) },
                suffix = { Text("V") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = currentText,
                onValueChange = { onFieldChange(ElectricalQuantity.CURRENT, it) },
                label = { Text(stringResource(R.string.power_current_label)) },
                suffix = { Text("A") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = resistanceText,
                onValueChange = { onFieldChange(ElectricalQuantity.RESISTANCE, it) },
                label = { Text(stringResource(R.string.power_resistance_label)) },
                suffix = { Text("Ω") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = powerText,
                onValueChange = { onFieldChange(ElectricalQuantity.POWER, it) },
                label = { Text(stringResource(R.string.power_power_label)) },
                suffix = { Text("W") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = ::onClear) {
                Text(stringResource(R.string.action_clear))
            }
        }
    }
}
