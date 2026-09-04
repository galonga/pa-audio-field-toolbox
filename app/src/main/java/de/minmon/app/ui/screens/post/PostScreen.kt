package de.minmon.app.ui.screens.post

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import de.minmon.app.util.Router
import de.minmon.design.components.ExpressivePadding
import de.minmon.design.token.RbSpacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    windowSizeClass: WindowSizeClass,
    postId: Int,
    isEvent: Boolean = false,
    viewModel: PostViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(postId, isEvent) {
        if (isEvent) {
            viewModel.onPostAction(PostAction.LoadEvent(postId))
        } else {
            viewModel.onPostAction(PostAction.LoadPost(postId))
        }
    }

    // Get the link for the "Open in browser" action
    val link = when (val content = uiState.content) {
        is ContentType.Post -> content.post.link
        is ContentType.Event -> content.event.link
        null -> null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main content
        PostScreenHolder(uiState = uiState)

        // Floating top bar with transparent background
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            TopAppBar(
                title = { /* Empty - we'll show title in the content */ },
                navigationIcon = {
                    IconButton(
                        onClick = { Router.dispatch(Router.NavigationType.PopBack) },
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (link != null) {
                        IconButton(
                            onClick = {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, link.toUri())
                                )
                            },
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Open in browser",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun PostScreenHolder(
    uiState: PostScreenUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(RbSpacing.space16)
                ) {
                    Text(
                        text = "Error loading post",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(RbSpacing.space8))
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            uiState.content != null -> {
                when (val content = uiState.content) {
                    is ContentType.Post -> PostContent(post = content.post)
                    is ContentType.Event -> EventContent(event = content.event)
                    else -> {} // Should not happen
                }
            }
        }
    }
}

@Composable
private fun PostContent(post: de.minmon.data.model.WordPressPost) {
    val scrollState = rememberScrollState()
    val headerHeight = 400.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

    // Calculate parallax offset (image scrolls at 0.5x speed)
    val parallaxOffset by remember {
        derivedStateOf {
            (scrollState.value * 0.5f).roundToInt()
        }
    }

    // Calculate header alpha for fade effect
    val headerAlpha by remember {
        derivedStateOf {
            1f - (scrollState.value / headerHeightPx).coerceIn(0f, 1f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header image with parallax
            val imageUrl = post.embedded?.featuredMedia?.firstOrNull()?.sourceUrl

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = HtmlCompat.fromHtml(
                            post.title.rendered,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .offset { IntOffset(0, parallaxOffset) }
                            .alpha(headerAlpha),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    ),
                                    startY = headerHeightPx * 0.5f
                                )
                            )
                    )
                } else {
                    // Fallback background for posts without featured image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }
            }

            // Content card with rounded top corners
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ExpressivePadding.large)
                ) {
                    // Title
                    Text(
                        text = HtmlCompat.fromHtml(
                            post.title.rendered,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(ExpressivePadding.medium))

                    // Date and Author
                    Text(
                        text = post.date.substringBefore("T"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    post.embedded?.author?.firstOrNull()?.let { author ->
                        Spacer(modifier = Modifier.height(ExpressivePadding.small))
                        Text(
                            text = "by ${author.name}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(ExpressivePadding.large))

                    // Content
                    Text(
                        text = HtmlCompat.fromHtml(
                            post.content.rendered,
                            HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                        ).toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Bottom padding
                    Spacer(modifier = Modifier.height(ExpressivePadding.huge))
                }
            }
        }
    }
}

@Composable
private fun EventContent(event: de.minmon.data.model.WordPressEvent) {
    val scrollState = rememberScrollState()
    val headerHeight = 400.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

    // Calculate parallax offset (image scrolls at 0.5x speed)
    val parallaxOffset by remember {
        derivedStateOf {
            (scrollState.value * 0.5f).roundToInt()
        }
    }

    // Calculate header alpha for fade effect
    val headerAlpha by remember {
        derivedStateOf {
            1f - (scrollState.value / headerHeightPx).coerceIn(0f, 1f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header image with parallax
            val imageUrl = event.embedded?.featuredMedia?.firstOrNull()?.sourceUrl

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = HtmlCompat.fromHtml(
                            event.title.rendered,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(headerHeight)
                            .offset { IntOffset(0, parallaxOffset) }
                            .alpha(headerAlpha),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    ),
                                    startY = headerHeightPx * 0.5f
                                )
                            )
                    )
                } else {
                    // Fallback background for events without featured image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }
            }

            // Content card with rounded top corners
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ExpressivePadding.large)
                ) {
                    // Title
                    Text(
                        text = HtmlCompat.fromHtml(
                            event.title.rendered,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(ExpressivePadding.medium))

                    // Date
                    Text(
                        text = event.date.substringBefore("T"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(ExpressivePadding.large))

                    // Content
                    Text(
                        text = HtmlCompat.fromHtml(
                            event.content.rendered,
                            HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV
                        ).toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Bottom padding
                    Spacer(modifier = Modifier.height(ExpressivePadding.huge))
                }
            }
        }
    }
}
