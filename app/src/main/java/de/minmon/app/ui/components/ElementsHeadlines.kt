package de.minmon.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import de.minmon.design.token.RbSizeTokens
import de.minmon.design.token.RbSpacing

@Composable
fun ItemSettingsHeadlineText(
    modifier: Modifier = Modifier,
    text: String,
    imageVector: ImageVector? = null,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        imageVector?.let {
            Icon(
                modifier = Modifier
                    .size(RbSizeTokens.rbSize24)
                    .fillMaxHeight(),
                imageVector = imageVector,
                contentDescription = text
            )

            Spacer(
                modifier = Modifier.width(15.dp)
            )
        } ?: run {
            Spacer(
                modifier = Modifier.width(39.dp)
            )
        }
        Column {
            Text(
                modifier = modifier.padding(bottom = RbSpacing.space8, top = RbSpacing.space16),
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
            content.invoke()
        }
    }
}
