package de.galonga.audiotoolbox.app.ui.screens.dbcalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.annotation.StringRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.galonga.audiotoolbox.app.R
import de.galonga.audiotoolbox.app.calculators.DbMath
import de.galonga.audiotoolbox.app.calculators.formatOrDash
import de.galonga.audiotoolbox.app.ui.components.about.ScreenContentContainer
import de.galonga.audiotoolbox.design.token.RbSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbCalculatorScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tool_db_calculator_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        ScreenContentContainer(modifier = Modifier.padding(padding)) {
            VoltageCard()
            RatioCard()
        }
    }
}

@Composable
private fun VoltageCard() {
    var voltsText by rememberSaveable { mutableStateOf("") }
    var dbuText by rememberSaveable { mutableStateOf("") }
    var dbvText by rememberSaveable { mutableStateOf("") }

    fun onVolts(text: String) {
        voltsText = text
        val v = text.toDoubleOrNull()
        when {
            text.isBlank() -> { dbuText = ""; dbvText = "" }
            v == null || v <= 0.0 -> { dbuText = "—"; dbvText = "—" }
            else -> {
                dbuText = DbMath.voltsToDbu(v).formatOrDash(2)
                dbvText = DbMath.voltsToDbv(v).formatOrDash(2)
            }
        }
    }

    fun onDbu(text: String) {
        dbuText = text
        val dbu = text.toDoubleOrNull()
        when {
            text.isBlank() -> { voltsText = ""; dbvText = "" }
            dbu == null -> { voltsText = "—"; dbvText = "—" }
            else -> {
                val v = DbMath.dbuToVolts(dbu)
                voltsText = v.formatOrDash(3)
                dbvText = DbMath.voltsToDbv(v).formatOrDash(2)
            }
        }
    }

    fun onDbv(text: String) {
        dbvText = text
        val dbv = text.toDoubleOrNull()
        when {
            text.isBlank() -> { voltsText = ""; dbuText = "" }
            dbv == null -> { voltsText = "—"; dbuText = "—" }
            else -> {
                val v = DbMath.dbvToVolts(dbv)
                voltsText = v.formatOrDash(3)
                dbuText = DbMath.voltsToDbu(v).formatOrDash(2)
            }
        }
    }

    CalculatorCard(titleRes = R.string.db_voltage_card_title) {
        LabeledNumberField(label = stringResource(R.string.db_volts_label), suffix = "V", value = voltsText, onValueChange = ::onVolts)
        LabeledNumberField(label = "dBu", suffix = "dBu", value = dbuText, onValueChange = ::onDbu)
        LabeledNumberField(label = "dBV", suffix = "dBV", value = dbvText, onValueChange = ::onDbv)
    }
}

@Composable
private fun RatioCard() {
    var voltageMode by rememberSaveable { mutableStateOf(true) } // true = voltage ratio (20·log), false = power ratio (10·log)
    var dbText by rememberSaveable { mutableStateOf("") }
    var ratioText by rememberSaveable { mutableStateOf("") }

    fun dbToRatio(db: Double) = if (voltageMode) DbMath.dbToVoltageRatio(db) else DbMath.dbToPowerRatio(db)
    fun ratioToDb(ratio: Double) = if (voltageMode) DbMath.voltageRatioToDb(ratio) else DbMath.powerRatioToDb(ratio)

    fun onDb(text: String) {
        dbText = text
        val db = text.toDoubleOrNull()
        ratioText = when {
            text.isBlank() -> ""
            db == null -> "—"
            else -> dbToRatio(db).formatOrDash(3)
        }
    }

    fun onRatio(text: String) {
        ratioText = text
        val ratio = text.toDoubleOrNull()
        dbText = when {
            text.isBlank() -> ""
            ratio == null || ratio <= 0.0 -> "—"
            else -> ratioToDb(ratio).formatOrDash(2)
        }
    }

    fun onModeChange(newVoltageMode: Boolean) {
        if (newVoltageMode == voltageMode) return
        voltageMode = newVoltageMode
        val db = dbText.toDoubleOrNull()
        if (db != null) {
            ratioText = dbToRatio(db).formatOrDash(3)
            return
        }
        val ratio = ratioText.toDoubleOrNull()
        if (ratio != null && ratio > 0.0) dbText = ratioToDb(ratio).formatOrDash(2)
    }

    CalculatorCard(titleRes = R.string.db_ratio_card_title) {
        Row(horizontalArrangement = Arrangement.spacedBy(RbSpacing.space8)) {
            FilterChip(selected = voltageMode, onClick = { onModeChange(true) }, label = { Text(stringResource(R.string.db_ratio_mode_voltage)) })
            FilterChip(selected = !voltageMode, onClick = { onModeChange(false) }, label = { Text(stringResource(R.string.db_ratio_mode_power)) })
        }
        LabeledNumberField(label = "dB", suffix = "dB", value = dbText, onValueChange = ::onDb)
        LabeledNumberField(label = stringResource(R.string.db_ratio_label), suffix = "x", value = ratioText, onValueChange = ::onRatio)
    }
}

@Composable
private fun CalculatorCard(@StringRes titleRes: Int, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = RbSpacing.space16),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RbSpacing.space16),
            verticalArrangement = Arrangement.spacedBy(RbSpacing.space12)
        ) {
            Text(text = stringResource(titleRes), style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun LabeledNumberField(
    label: String,
    suffix: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        suffix = { Text(suffix) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}
