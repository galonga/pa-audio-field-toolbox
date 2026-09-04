package de.minmon.design.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.minmon.design.animations.ExpressiveFadeInScale
import de.minmon.design.animations.ExpressiveStaggeredListItem
import de.minmon.design.components.ExpressiveCard
import de.minmon.design.components.ExpressiveHeroCard
import de.minmon.design.components.ExpressiveOutlinedButton
import de.minmon.design.components.ExpressivePadding
import de.minmon.design.components.ExpressivePrimaryButton
import de.minmon.design.components.ExpressiveSecondaryButton
import de.minmon.design.theme.ExpressiveShapes
import de.minmon.design.theme.MinMonTheme

/**
 * Material Expressive Examples Screen
 *
 * Demonstrates all the Material Expressive components and patterns
 * available in the MinMon design system.
 */
@Composable
fun ExpressiveExamplesScreen() {
    var showContent by remember { mutableStateOf(true) }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(ExpressivePadding.medium),
            verticalArrangement = Arrangement.spacedBy(ExpressivePadding.large)
        ) {
            // Hero Section
            item {
                ExpressiveFadeInScale(visible = showContent) {
                    Column {
                        Text(
                            text = "Material Expressive",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(ExpressivePadding.small))
                        Text(
                            text = "Enhanced design with personality",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Section: Hero Card
            item {
                SectionTitle("Hero Card")
            }

            item {
                ExpressiveFadeInScale(visible = showContent) {
                    ExpressiveHeroCard(
                        onClick = { /* Handle click */ }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExpressivePadding.large)
                        ) {
                            Text(
                                text = "Featured Content",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(ExpressivePadding.small))
                            Text(
                                text = "This hero card features a 28dp corner radius, " +
                                        "interactive elevation, and spring animations on press.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(ExpressivePadding.medium))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(ExpressivePadding.small)
                            ) {
                                ExpressivePrimaryButton(
                                    onClick = { }
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    Text("Play")
                                }
                                ExpressiveSecondaryButton(
                                    onClick = { }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    Text("Share")
                                }
                            }
                        }
                    }
                }
            }

            // Section: Buttons
            item {
                SectionTitle("Expressive Buttons")
            }

            item {
                ExpressiveFadeInScale(visible = showContent) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(ExpressivePadding.medium)
                    ) {
                        ExpressivePrimaryButton(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Primary Button - 24dp Rounded")
                        }

                        ExpressiveSecondaryButton(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Secondary Button - Tonal Fill")
                        }

                        ExpressiveOutlinedButton(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Outlined Button - Stroke Style")
                        }
                    }
                }
            }

            // Section: Cards with Different Shapes
            item {
                SectionTitle("Shape Variations")
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(ExpressivePadding.medium)
                ) {
                    ExpressiveCard(
                        shape = MaterialTheme.shapes.medium // 16dp
                    ) {
                        CardContent(
                            title = "Medium Shape",
                            description = "16dp corner radius - standard card"
                        )
                    }

                    ExpressiveCard(
                        shape = MaterialTheme.shapes.large // 24dp
                    ) {
                        CardContent(
                            title = "Large Shape",
                            description = "24dp corner radius - prominent card"
                        )
                    }

                    ExpressiveCard(
                        shape = MaterialTheme.shapes.extraLarge // 32dp
                    ) {
                        CardContent(
                            title = "Extra Large Shape",
                            description = "32dp corner radius - hero surface"
                        )
                    }

                    ExpressiveCard(
                        shape = ExpressiveShapes.asymmetricMedium
                    ) {
                        CardContent(
                            title = "Asymmetric Shape",
                            description = "Varied corners for personality"
                        )
                    }
                }
            }

            // Section: Staggered List Animation
            item {
                SectionTitle("Staggered List Animation")
            }

            items(5) { index ->
                ExpressiveStaggeredListItem(
                    index = index,
                    visible = showContent,
                    delayPerItem = 50
                ) {
                    ExpressiveCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ExpressivePadding.medium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "List Item ${index + 1}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Animates in with 50ms delay",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Section: Typography Scale
            item {
                SectionTitle("Expressive Typography")
            }

            item {
                ExpressiveCard {
                    Column(
                        modifier = Modifier.padding(ExpressivePadding.large),
                        verticalArrangement = Arrangement.spacedBy(ExpressivePadding.medium)
                    ) {
                        TypeExample("Display Large", MaterialTheme.typography.displayLarge)
                        HorizontalDivider()
                        TypeExample("Headline Large", MaterialTheme.typography.headlineLarge)
                        HorizontalDivider()
                        TypeExample("Title Large", MaterialTheme.typography.titleLarge)
                        HorizontalDivider()
                        TypeExample("Body Large", MaterialTheme.typography.bodyLarge)
                        HorizontalDivider()
                        TypeExample("Label Large", MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = ExpressivePadding.small)
    )
}

@Composable
private fun CardContent(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(ExpressivePadding.medium)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(ExpressivePadding.small))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TypeExample(
    label: String,
    textStyle: androidx.compose.ui.text.TextStyle
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "The quick brown fox",
            style = textStyle
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpressiveExamplesScreenPreview() {
    MinMonTheme {
        ExpressiveExamplesScreen()
    }
}

@Preview(
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ExpressiveExamplesScreenDarkPreview() {
    MinMonTheme(darkTheme = true) {
        ExpressiveExamplesScreen()
    }
}
