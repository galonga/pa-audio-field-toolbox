package de.minmon.app.ui.components.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import de.minmon.design.token.RbShapeTokens

@Composable
fun FlexibleRow(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    summary: String? = null,
    imageVector: ImageVector? = null,
    painterResource: Painter? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(0.dp, 3.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(RbShapeTokens.rbCornerRadius16))
            .clickable {
                onClick.invoke()
            }
            .padding(10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageVector != null) {
            Icon(imageVector, null)
        } else if (painterResource != null) {
            Icon(painterResource, null)
        }
        Spacer(
            modifier = Modifier.width(15.dp)
        )
        Column {
            Text(
                text = title,
                style = titleStyle
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
