package de.galonga.audiotoolbox.app.ui.components.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.galonga.audiotoolbox.app.ui.components.ColumnCardContainer
import de.galonga.audiotoolbox.app.ui.components.RbColumnCardContainerModel
import de.galonga.audiotoolbox.design.token.RbSpacing

@Composable
fun ScreenSettingsContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(RbSpacing.space16)
    ) {
        content.invoke()
    }
}

@Composable
fun ScreenContentContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ColumnCardContainer(
        modifier = modifier,
        model = RbColumnCardContainerModel(
            paddingValues = PaddingValues()
        )
    ) {
        content.invoke()
    }
}
