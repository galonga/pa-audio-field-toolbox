package de.galonga.audiotoolbox.design.components.snackbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.R
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import de.galonga.audiotoolbox.design.token.RbSpacing

@Composable
fun MMSnackbar(
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    snackbarData: SnackbarData,
    actionContentColor: Color = SnackbarDefaults.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaults.dismissActionContentColor,
) {
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? = if (actionLabel != null) {
        @Composable {
            TextButton(
                onClick = { snackbarData.performAction() },
                content = { Text(text = actionLabel, fontWeight = FontWeight.SemiBold) }
            )
        }
    } else {
        null
    }
    val dismissActionComposable: (@Composable () -> Unit)? =
        if (snackbarData.visuals.withDismissAction) {
            @Composable {
                IconButton(
                    onClick = { snackbarData.dismiss() },
                    content = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.m3c_snackbar_dismiss)
                        )
                    }
                )
            }
        } else {
            null
        }

    Snackbar(
        modifier = modifier.padding(RbSpacing.space16),
        action = actionComposable,
        dismissAction = dismissActionComposable,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor
    ) {
        Row {
//            Image(
//                modifier = Modifier
//                    .align(CenterVertically)
//                    .padding(end = RbSpacing.space8),
//                painter = painterResource(id = snackBarStyle.icon),
//                contentDescription = null
//            )
            Text(
                modifier = Modifier.align(CenterVertically),
                text = snackbarData.visuals.message
            )
        }
    }
}
