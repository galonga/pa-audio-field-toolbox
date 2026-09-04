package de.minmon.app.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Mp
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.minmon.app.CompactWindowSizeClass
import de.minmon.app.ui.components.EnhancedCard
import de.minmon.app.ui.components.EnhancedCardModel
import de.minmon.app.ui.components.StorybookLayoutType
import de.minmon.design.token.RbSizeTokens
import de.minmon.design.token.RbSpacing
import de.minmon.app.util.Router
import de.minmon.app.ui.nav.Screens
import de.minmon.data.model.WordPressPost
import de.minmon.design.theme.MinMonTheme
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material3.Text as MaterialText
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.minmon.app.ui.components.IconAnimation

@Composable
fun HomeScreen(windowSizeClass: WindowSizeClass, viewModel: HomeViewModel = koinViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    Box {
        HomeScreenHolder(
            uiState = uiState,
            onHomeAction = viewModel::onHomeAction,
            windowSizeClass = windowSizeClass,
        )
    }
}

@Composable
fun HomeScreenHolder(
    uiState: HomeScreenUiState,
    onHomeAction: (HomeAction) -> Unit,
    windowSizeClass: WindowSizeClass,
) {

    Column() {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {

            HighlightsCarouselMultiBrowse(onHomeAction)

            // Recent Posts
            EnhancedCard(
                modifier = Modifier.padding(RbSpacing.outerSpacing),
                model = EnhancedCardModel(
                    title = "Recent Posts",
                    layoutType = StorybookLayoutType.TONAL_SURFACE,
                    showAccentBar = false,
                    icon = Icons.Default.NoteAlt
                )
            ) {
                Column(modifier = Modifier.padding(RbSpacing.space16)) {
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(RbSpacing.space16)
                            ) {
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
                                    onClick = {
                                        onHomeAction(HomeAction.OnPostClick(post.id))
                                    }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightsCarouselMultiBrowse(onHomeAction: (HomeAction) -> Unit) {
    data class CarouselItem(
        val id: Int,
        val content: @Composable () -> Unit,
    )

    val items = remember {
        listOf(
            CarouselItem(0, {
                EnhancedCard(
                    modifier = Modifier.fillMaxHeight(),
                    model = EnhancedCardModel(
                        title = "We Are?",
                        layoutType = StorybookLayoutType.TONAL_SURFACE,
                        showAccentBar = false,
                        icon = Icons.Default.Mp,
                        accentColor = MaterialTheme.colorScheme.primary,
                        iconAnimation = IconAnimation.ROTATE
                    )
                ) {
                    Column() {
                        Text(
                            modifier = Modifier.weight(1f),

                            style = MaterialTheme.typography.bodyMedium,
                            text = "Wir sind das MinMon Kollektiv, eine Gemeinschaft, die sich den vielen Facetten der elektronischen Musik verschrieben hat. Uns verbindet die Lust zur Kreativität und die Liebe für kleine handgemachte Veranstaltungen an besonderen Orten.",
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier.padding(RbSpacing.space8),
                                onClick = {}) {
                                Text(text = "Dates")
                            }
                            Button(
                                modifier = Modifier.padding(RbSpacing.space8),
                                onClick = {}) {
                                Text(text = "Podcast")
                            }
                        }
                    }
                }
            }),
            CarouselItem(1, {
                EnhancedCard(
                    modifier = Modifier.fillMaxHeight(),
                    model = EnhancedCardModel(
                        title = "Podcast!",
                        layoutType = StorybookLayoutType.TONAL_SURFACE,
                        showAccentBar = false,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        icon = Icons.Default.Podcasts,
                        iconAnimation = IconAnimation.ROTATE
                    )
                ) {
                    Column() {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = "Der MinMon Podcast ist ein sporadisch erscheinender DJ oder Live Mix, welcher Projekten und Künstlern eine Plattform bietet, um ihre Audiokreativität einem breiteren Publikum zu präsentieren.\n" +
                                    "\n" +
                                    "In jeder Folge des Podcasts führen wir außerdem ein Interview mit dem jeweiligen Projekt, welche ihr hier auf der Webseite nachlesen könnt.\n" +
                                    "\n" +
                                    "Den aktuellen Podcast findest du  weiter unten verlinkt.\n" +
                                    "\n" +
                                    "Wenn auch Du einmal einen Podcast bei uns veröffentlichen möchtest kannst du gern mit uns Kontakt aufnehmen und uns eine Demo deines Kreativität hinterlassen.",
                        )
                        Row() {
                            Button(onClick = {}) {
                                Text(text = "Dates")
                            }
                            Button(onClick = {}) {
                                Text(text = "Podcast")
                            }
                        }
                    }
                }
            }),
            CarouselItem(2, {
                EnhancedCard(
                    modifier = Modifier.fillMaxHeight(),
                    model = EnhancedCardModel(
                        title = "Artists!",
                        layoutType = StorybookLayoutType.TONAL_SURFACE,
                        showAccentBar = false,
                        accentColor = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.Groups,
                        iconAnimation = IconAnimation.ROTATE
                    )
                ) {
                    Column() { Text(text = "Artists!") }
                }
            }

            ),
            CarouselItem(3, {
                EnhancedCard(
                    modifier = Modifier.fillMaxHeight(),
                    model = EnhancedCardModel(
                        title = "What We Also Do.",
                        layoutType = StorybookLayoutType.TONAL_SURFACE,
                        showAccentBar = false,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        icon = Icons.Default.MoreHoriz,
                        iconAnimation = IconAnimation.ROTATE
                    )
                ) {
                    Column() {
                        Text(text = "What We Also Do.")
                    }
                }
            }),
        )
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { items.count() },
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        preferredItemWidth = 320.dp,
        itemSpacing = RbSpacing.outerSpacing,
        contentPadding = PaddingValues(horizontal = RbSpacing.outerSpacing)
    ) { i ->
        val item = items[i]
        item.content()

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
private fun HomeScreenPreview() {
    MinMonTheme {
        HomeScreenHolder(
            uiState = HomeScreenUiState(),
            onHomeAction = {},
            windowSizeClass = CompactWindowSizeClass,
        )
    }
}
