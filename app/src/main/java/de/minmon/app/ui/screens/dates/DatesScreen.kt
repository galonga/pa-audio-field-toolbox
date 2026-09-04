package de.minmon.app.ui.screens.dates

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.minmon.app.ui.components.EnhancedCard
import de.minmon.app.ui.components.EnhancedCardModel
import de.minmon.app.ui.components.StorybookLayoutType
import de.minmon.app.ui.components.about.ScreenContentContainer
import de.minmon.app.util.Router
import de.minmon.app.ui.nav.Screens
import de.minmon.data.model.WordPressEvent
import de.minmon.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatesScreen(windowSizeClass: WindowSizeClass, viewModel: DatesViewModel = koinViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        DatesScreenHolder(
            uiState = uiState,
            onDatesAction = viewModel::onDatesAction,
            windowSizeClass = windowSizeClass,
            onPostClick = { postId ->
                Router.dispatch(Router.NavigationType.NavigateTo(Screens.Post(postId, isEvent = true)))
            }
        )
    }
}

@Composable
fun DatesScreenHolder(
    uiState: DatesScreenUiState,
    onDatesAction: (DatesAction) -> Unit,
    windowSizeClass: WindowSizeClass,
    onPostClick: (Int) -> Unit
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {
        EnhancedCard(
            model = EnhancedCardModel(
                title = "Termine & Events",
                layoutType = StorybookLayoutType.ELEVATED_CARD,
                showAccentBar = false,
                icon = Icons.Default.CalendarMonth
            )
        ) {
            Column(modifier = Modifier.padding(RbSpacing.space16)) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(RbSpacing.space16)) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.errorMessage != null -> {
                        Text(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    uiState.events.isEmpty() -> {
                        Text(
                            text = "No events available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        uiState.events.forEach { event ->
                            EventListItem(
                                event = event,
                                onClick = { onPostClick(event.id) }
                            )
                            Spacer(modifier = Modifier.height(RbSpacing.space8))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventListItem(
    event: WordPressEvent,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = RbSpacing.space8)
    ) {
        Text(
            text = event.title.rendered.replace(Regex("<[^>]*>"), ""),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(RbSpacing.space4))
        Text(
            text = event.date.substringBefore("T"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(RbSpacing.space4))
        Text(
            text = event.excerpt.rendered.replace(Regex("<[^>]*>"), "").take(150) + "...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
