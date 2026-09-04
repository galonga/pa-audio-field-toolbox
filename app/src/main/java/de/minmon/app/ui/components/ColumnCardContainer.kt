package de.minmon.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.minmon.design.theme.MinMonTheme
import de.minmon.design.token.RbSizeTokens
import de.minmon.design.token.RbSpacing

data class RbColumnCardContainerModel(val paddingValues: PaddingValues)

@Composable
fun ColumnCardContainer(modifier: Modifier = Modifier, model: RbColumnCardContainerModel, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .padding(model.paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(vertical = RbSpacing.space8, horizontal = RbSpacing.space16)
            .fillMaxWidth()
    ) {
        content()
    }
}

@Preview
@Composable
private fun ColumnCardContainerPreview() {
    MinMonTheme() {
        ColumnCardContainer(
            model = RbColumnCardContainerModel(paddingValues = PaddingValues())
        ) {
            Box(modifier = Modifier.size(RbSizeTokens.rbSize16))
        }
    }
}
