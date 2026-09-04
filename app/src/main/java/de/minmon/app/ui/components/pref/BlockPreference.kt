package de.minmon.app.ui.components.pref

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.minmon.app.ui.components.BlockButton
import de.minmon.design.utils.Preferences

@Composable
fun BlockPreference(
    preferenceKey: String,
    entries: List<String>,
    values: List<String>,
    onSelectionChange: (Int) -> Unit = {},
) {
    var selected by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(Unit) {
        val pref = Preferences.getString(preferenceKey, "")
        if (pref != "") selected = values.indexOf(pref)
    }

    LazyRow(
        modifier = Modifier.padding(horizontal = 5.dp)
    ) {
        items(entries) {
            val index = entries.indexOf(it)
            BlockButton(
                modifier = Modifier.padding(2.dp, 0.dp),
                text = it,
                selected = index != selected
            ) {
                Preferences.edit { putString(preferenceKey, values[index]) }
                selected = index
                onSelectionChange.invoke(selected)
            }
        }
    }
}
