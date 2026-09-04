package de.minmon.app.ui.screens.news

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.minmon.app.CompactWindowSizeClass
import de.minmon.app.ui.components.EnhancedCard
import de.minmon.app.ui.components.EnhancedCardModel
import de.minmon.app.ui.components.StorybookLayoutType
import de.minmon.app.ui.components.about.ScreenContentContainer
import de.minmon.data.model.WordPressPost
import de.minmon.design.theme.MinMonTheme
import de.minmon.design.token.RbSizeTokens
import de.minmon.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsScreen(windowSizeClass: WindowSizeClass, viewModel: NewsViewModel = koinViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        NewsScreenHolder(
            uiState = uiState,
            onNewsAction = viewModel::onNewsAction,
            windowSizeClass = windowSizeClass,
            openLinkFromHref = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, it.toUri())
                )
            }
        )
    }
}

@Composable
fun NewsScreenHolder(
    uiState: NewsScreenUiState,
    onNewsAction: (NewsAction) -> Unit,
    windowSizeClass: WindowSizeClass,
    openLinkFromHref: (String) -> Unit,
) {
    ScreenContentContainer(modifier = Modifier.fillMaxWidth()) {

        // Recent Posts
        EnhancedCard(
            model = EnhancedCardModel(
                title = "Recent Posts",
                layoutType = StorybookLayoutType.ELEVATED_CARD,
                showAccentBar = false,
                icon = Icons.Default.NoteAlt
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
                        MaterialText(
                            text = "Error: ${uiState.errorMessage}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    uiState.posts.isEmpty() -> {
                        MaterialText(
                            text = "No posts available",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        uiState.posts.forEach { post ->
                            PostListItem(
                                post = post,
                                onClick = { openLinkFromHref(post.link) }
                            )
                            Spacer(modifier = Modifier.height(RbSpacing.space8))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(RbSizeTokens.rbSize24))
    }
}

@Composable
private fun PostListItem(
    post: WordPressPost,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = RbSpacing.space8)
    ) {
        MaterialText(
            text = post.title.rendered.replace(Regex("<[^>]*>"), ""),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(RbSpacing.space4))
        MaterialText(
            text = post.excerpt.rendered.replace(Regex("<[^>]*>"), "").take(150) + "...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun NewsScreenPreview() {
    MinMonTheme {
        NewsScreenHolder(
            uiState = NewsScreenUiState(),
            onNewsAction = {},
            windowSizeClass = CompactWindowSizeClass,
            openLinkFromHref = {}
        )
    }
}
