package de.minmon.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun EditFloatingActionButton(isScrolling: Boolean = false, text: String = "Edit", onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        expanded = !isScrolling,
        text = { Text(text) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = text,
                tint = Color.White
            )
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}
