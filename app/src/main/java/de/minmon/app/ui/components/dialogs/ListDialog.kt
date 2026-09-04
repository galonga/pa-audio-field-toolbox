package de.minmon.app.ui.components.dialogs

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.minmon.app.ui.components.DialogButton
import de.minmon.app.ui.components.SelectableItem

@Composable
fun ListDialog(items: List<String>, onDismissRequest: () -> Unit, onClick: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DialogButton(
                text = stringResource(R.string.cancel),
                onClick = {
                    onDismissRequest()
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                items.forEachIndexed { index, title ->
                    SelectableItem(
                        text = title
                    ) {
                        onClick.invoke(index)
                    }
                }
            }
        }
    )
}
