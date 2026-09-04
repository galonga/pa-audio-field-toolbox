package de.galonga.audiotoolbox.design.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.galonga.audiotoolbox.design.token.MotionTokens
import de.galonga.audiotoolbox.design.theme.ExpressiveShapes

/**
 * Material Expressive Components
 *
 * Enhanced components with expressive animations, larger corner radii,
 * and more personality for a delightful user experience.
 */

/**
 * Expressive Card with enhanced elevation and rounded corners
 *
 * Features:
 * - Larger corner radii (24dp default)
 * - Enhanced elevation with press interaction
 * - Smooth spring animations
 *
 * @param modifier The modifier to apply to the card
 * @param shape The shape of the card (default: large rounded corners)
 * @param onClick Optional click handler with interactive elevation
 * @param content The card content
 */
@Composable
fun ExpressiveCard(
    modifier: Modifier = Modifier,
    shape: Shape = ExpressiveShapes.heroCard,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animate elevation on press
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 4.dp,
        animationSpec = MotionTokens.expressiveSpring(),
        label = "card_elevation"
    )

    // Animate scale on press for feedback
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = MotionTokens.expressiveSpring(),
        label = "card_scale"
    )

    Card(
        modifier = modifier.scale(scale),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        onClick = onClick ?: {}
    ) {
        content()
    }
}

/**
 * Expressive Hero Card for prominent content
 *
 * Features larger corners and enhanced visual prominence
 */
@Composable
fun ExpressiveHeroCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    ExpressiveCard(
        modifier = modifier,
        shape = ExpressiveShapes.heroCard,
        onClick = onClick,
        content = content
    )
}

/**
 * Expressive Primary Button with enhanced corners and spring animation
 *
 * Features:
 * - Larger corner radius (16dp)
 * - Spring animation on press
 * - Prominent filled appearance
 *
 * @param onClick Click handler
 * @param modifier The modifier to apply
 * @param enabled Whether the button is enabled
 * @param contentPadding The padding around button content
 * @param content The button content (typically Text)
 */
@Composable
fun ExpressivePrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = MotionTokens.expressiveSpring(),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        shape = MaterialTheme.shapes.large, // 24dp rounded
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        content()
    }
}

/**
 * Expressive Secondary Button with tonal fill and spring animation
 */
@Composable
fun ExpressiveSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = MotionTokens.expressiveSpring(),
        label = "button_scale"
    )

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        content()
    }
}

/**
 * Expressive Outlined Button with enhanced corners
 */
@Composable
fun ExpressiveOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = MotionTokens.expressiveSpring(),
        label = "button_scale"
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.scale(scale),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        contentPadding = contentPadding,
        interactionSource = interactionSource
    ) {
        content()
    }
}

/**
 * Expressive padding values for consistent spacing
 */
object ExpressivePadding {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
}

/**
 * Expressive elevation values for surface hierarchy
 */
object ExpressiveElevation {
    val level0 = 0.dp
    val level1 = 2.dp
    val level2 = 4.dp
    val level3 = 8.dp
    val level4 = 12.dp
    val level5 = 16.dp
}
