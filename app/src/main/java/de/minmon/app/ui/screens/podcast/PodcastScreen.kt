package de.minmon.app.ui.screens.podcast

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Podcasts
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
import de.minmon.data.model.WordPressPost
import de.minmon.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PodcastScreen(windowSizeClass: WindowSizeClass, viewModel: PodcastViewModel = koinViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        PodcastScreenHolder(
            uiState = uiState,
            onPodcastAction = viewModel::onPodcastAction,
            windowSizeClass = windowSizeClass,
            onPostClick = { postId ->
                Router.dispatch(Router.NavigationType.NavigateTo(Screens.Post(postId)))
            }
        )
    }
}

@Composable
fun PodcastScreenHolder(
    uiState: PodcastScreenUiState,
    onPodcastAction: (PodcastAction) -> Unit,
    windowSizeClass: WindowSizeClass,
    onPostClick: (Int) -> Unit
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {
        EnhancedCard(
            model = EnhancedCardModel(
                title = "Podcast Episodes",
                layoutType = StorybookLayoutType.ELEVATED_CARD,
                showAccentBar = false,
                icon = Icons.Default.Podcasts
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
                    uiState.posts.isEmpty() -> {
                        Text(
                            text = "No podcast episodes available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        uiState.posts.forEach { post ->
                            PodcastListItem(
                                post = post,
                                onClick = { onPostClick(post.id) }
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
private fun PodcastListItem(
    post: WordPressPost,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = RbSpacing.space8)
    ) {
        Text(
            text = post.title.rendered.replace(Regex("<[^>]*>"), ""),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(RbSpacing.space4))
        Text(
            text = post.date.substringBefore("T"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(RbSpacing.space4))
        Text(
            text = post.excerpt.rendered.replace(Regex("<[^>]*>"), "").take(150) + "...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
