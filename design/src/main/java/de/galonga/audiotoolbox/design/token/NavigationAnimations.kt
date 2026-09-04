package de.galonga.audiotoolbox.design.token

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * Material 3 Expressive Navigation Animations
 * Enhanced with scale animations and emphasized easing curves for more personality
 */
object NavigationAnimations {

    // Material 3 Expressive timing tokens
    private const val DURATION_ENTER_SHORT = 250
    private const val DURATION_EXIT_SHORT = 200
    private const val DURATION_ENTER_MEDIUM = 350
    private const val DURATION_EXIT_MEDIUM = 300

    // Material 3 Emphasized easing curves for expressive motion
    private val EmphasizedDecelerateEasing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    private val EmphasizedAccelerateEasing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)

    /**
     * Screen enter transition: slide + fade + scale for dynamic entrance with depth
     * Duration: 350ms with emphasized decelerate easing
     */
    fun screenEnterTransition(): EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth / 4 },
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MEDIUM,
            easing = EmphasizedDecelerateEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MEDIUM,
            easing = EmphasizedDecelerateEasing
        )
    ) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MEDIUM,
            easing = EmphasizedDecelerateEasing
        )
    )

    /**
     * Screen exit transition: slide + fade + scale for depth perception
     * Duration: 300ms with emphasized accelerate easing
     */
    fun screenExitTransition(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MEDIUM,
            easing = EmphasizedAccelerateEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MEDIUM,
            easing = EmphasizedAccelerateEasing
        )
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MEDIUM,
            easing = EmphasizedAccelerateEasing
        )
    )

    /**
     * Pop enter transition: slide in from left (going back)
     */
    fun screenPopEnterTransition(): EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 4 },
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MEDIUM,
            easing = EmphasizedDecelerateEasing
        )
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MEDIUM,
            easing = EmphasizedDecelerateEasing
        )
    ) + scaleIn(
        initialScale = 0.95f,
        animationSpec = tween(
            durationMillis = DURATION_ENTER_MEDIUM,
            easing = EmphasizedDecelerateEasing
        )
    )

    /**
     * Pop exit transition: slide out to right (going back)
     */
    fun screenPopExitTransition(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth / 4 },
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MEDIUM,
            easing = EmphasizedAccelerateEasing
        )
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MEDIUM,
            easing = EmphasizedAccelerateEasing
        )
    ) + scaleOut(
        targetScale = 0.95f,
        animationSpec = tween(
            durationMillis = DURATION_EXIT_MEDIUM,
            easing = EmphasizedAccelerateEasing
        )
    )

    /**
     * Bottom sheet slide-up entrance with spring physics for expressive feel
     */
    fun slideUpEnterTransition(): EnterTransition = slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(
        animationSpec = tween(DURATION_ENTER_SHORT)
    )

    /**
     * Bottom sheet slide-down exit
     */
    fun slideDownExitTransition(): ExitTransition = slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(
            durationMillis = DURATION_EXIT_SHORT,
            easing = EmphasizedAccelerateEasing
        )
    ) + fadeOut(
        animationSpec = tween(DURATION_EXIT_SHORT)
    )

    /**
     * Bottom navigation selection animation spec
     * Uses spring physics with medium bouncy damping for expressive feel
     */
    val bottomNavSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Color transition animation spec for bottom nav and other UI elements
     * Duration: 150ms for snappy color changes
     */
    val colorTransitionSpec = tween<Color>(
        durationMillis = 150,
        easing = FastOutSlowInEasing
    )

    /**
     * Elevation animation spec for bottom nav scroll effects
     * Duration: 150ms for smooth elevation changes
     */
    val elevationTransitionSpec = tween<Dp>(
        durationMillis = 150,
        easing = FastOutSlowInEasing
    )
}
