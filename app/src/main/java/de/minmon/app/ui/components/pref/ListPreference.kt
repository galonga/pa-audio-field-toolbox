package de.minmon.app.ui.components.pref

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.minmon.app.ui.components.dialogs.ListDialog
import de.rebuy.android.uistorybook.ui.components.pref.PreferenceItem

@Composable
fun ListPreference(
    title: String,
    entries: List<String>,
    values: List<String>,
    defaultValue: String,
    onChange: (String) -> Unit = {},
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val indexOfCurrent = values.indexOf(defaultValue)
    var summary by remember {
        mutableStateOf(
            if (indexOfCurrent != -1) entries[indexOfCurrent] else null
        )
    }

    PreferenceItem(
        title = title,
        summary = summary,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                showDialog = true
            }
            .padding(10.dp)
    )

    if (showDialog) {
        ListDialog(
            items = entries,
            onDismissRequest = {
                showDialog = false
            },
            onClick = {
                summary = entries[it]
                onChange.invoke(values[it])
                showDialog = false
            }
        )
    }
}
