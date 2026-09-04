package de.galonga.audiotoolbox.design.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import de.galonga.audiotoolbox.design.token.MotionTokens

/**
 * Material Expressive Animations
 *
 * Provides reusable animation patterns with expressive motion characteristics
 * including overshoots, bounces, and emphasized easing for delightful interactions.
 */

/**
 * Expressive fade-in with scale animation
 * Perfect for content appearing with personality
 */
@Composable
fun ExpressiveFadeInScale(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.ENTER_DURATION_EXPRESSIVE,
                easing = MotionTokens.ENTER_EASING_EXPRESSIVE
            )
        ) + scaleIn(
            initialScale = 0.8f,
            transformOrigin = TransformOrigin.Center,
            animationSpec = tween(
                durationMillis = MotionTokens.SCALE_DURATION_EXPRESSIVE,
                easing = MotionTokens.expressiveOvershoot
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.EXIT_DURATION,
                easing = MotionTokens.EXIT_EASING
            )
        ) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(
                durationMillis = MotionTokens.EXIT_DURATION,
                easing = MotionTokens.EXIT_EASING
            )
        ),
        content = content
    )
}

/**
 * Expressive slide-in from bottom with fade
 * Great for bottom sheets, dialogs, and cards
 */
@Composable
fun ExpressiveSlideInFromBottom(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = MotionTokens.ENTER_DURATION_EXPRESSIVE,
                easing = MotionTokens.emphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION_LONG,
                easing = MotionTokens.linear
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                durationMillis = MotionTokens.EXIT_DURATION,
                easing = MotionTokens.emphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION,
                easing = MotionTokens.linear
            )
        ),
        content = content
    )
}

/**
 * Expressive slide-in from end (right in LTR) with fade
 * Perfect for navigation transitions
 */
@Composable
fun ExpressiveSlideInFromEnd(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(
                durationMillis = MotionTokens.SLIDE_DURATION,
                easing = MotionTokens.emphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION_LONG,
                easing = MotionTokens.linear
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it / 3 }, // Parallax effect
            animationSpec = tween(
                durationMillis = MotionTokens.EXIT_DURATION,
                easing = MotionTokens.emphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION,
                easing = MotionTokens.linear
            )
        ),
        content = content
    )
}

/**
 * Expressive expand vertically with fade
 * Excellent for expandable cards and lists
 */
@Composable
fun ExpressiveExpandVertically(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(
                durationMillis = MotionTokens.ENTER_DURATION_EXPRESSIVE,
                easing = MotionTokens.emphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION_LONG,
                easing = MotionTokens.linear
            )
        ),
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(
                durationMillis = MotionTokens.EXIT_DURATION,
                easing = MotionTokens.emphasizedAccelerate
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION,
                easing = MotionTokens.linear
            )
        ),
        content = content
    )
}

/**
 * Bouncy entrance animation modifier
 * Applies a bouncy scale animation when content first appears
 *
 * Usage:
 * ```
 * Text(
 *     text = "Hello",
 *     modifier = Modifier.bouncyEntrance()
 * )
 * ```
 */
@Composable
fun Modifier.bouncyEntrance(
    initialScale: Float = 0.3f,
    durationMillis: Int = MotionTokens.ENTER_DURATION_EXPRESSIVE
): Modifier {
    val scale = remember { Animatable(initialScale) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = MotionTokens.expressiveEnterSpring()
        )
    }

    return this.scale(scale.value)
}

/**
 * Staggered list entrance animation
 * Animates list items with a delay between each
 *
 * @param index The index of the item in the list
 * @param delayPerItem Delay in milliseconds between each item (default 50ms)
 */
@Composable
fun ExpressiveStaggeredListItem(
    index: Int,
    visible: Boolean,
    modifier: Modifier = Modifier,
    delayPerItem: Int = 50,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(
                durationMillis = MotionTokens.ENTER_DURATION,
                delayMillis = index * delayPerItem,
                easing = MotionTokens.emphasizedDecelerate
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION_LONG,
                delayMillis = index * delayPerItem,
                easing = MotionTokens.linear
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = MotionTokens.FADE_DURATION,
                easing = MotionTokens.linear
            )
        ),
        content = content
    )
}
