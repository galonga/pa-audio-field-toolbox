package de.galonga.audiotoolbox.design.token

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/**
 * Material Expressive Motion System
 * Provides expressive, personality-driven animation tokens for creating
 * engaging and delightful user experiences with enhanced motion.
 */
object MotionTokens {

    // Duration tokens
    const val DURATION_SHORT_1 = 50
    const val DURATION_SHORT_2 = 100
    const val DURATION_SHORT_3 = 150
    const val DURATION_SHORT_4 = 200

    const val DURATION_MEDIUM_1 = 250
    const val DURATION_MEDIUM_2 = 300
    const val DURATION_MEDIUM_3 = 350
    const val DURATION_MEDIUM_4 = 400

    const val DURATION_LONG_1 = 450
    const val DURATION_LONG_2 = 500
    const val DURATION_LONG_3 = 550
    const val DURATION_LONG_4 = 600

    // Extended durations for expressive moments
    const val DURATION_EXTRA_LONG_1 = 700
    const val DURATION_EXTRA_LONG_2 = 800
    const val DURATION_EXTRA_LONG_3 = 900
    const val DURATION_EXTRA_LONG_4 = 1000

    // Material Expressive Easing Curves
    // Enhanced emphasized easing for more personality
    val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val emphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    // Standard easing for everyday interactions
    val standard: Easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val standardDecelerate: Easing = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
    val standardAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)

    // Expressive overshoot easing for playful moments
    val expressiveOvershoot: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f)
    val expressiveAnticipate: Easing = CubicBezierEasing(0.36f, 0.0f, 0.66f, -0.56f)

    // Linear for fade effects
    val linear: Easing = CubicBezierEasing(0.0f, 0.0f, 1.0f, 1.0f)

    // Material Expressive Spring Animations
    // High energy springs for expressive, bouncy animations
    fun <T> expressiveSpring() = spring<T>(
        dampingRatio = 0.5f, // More bounce
        stiffness = 300f // Medium stiffness
    )

    fun <T> expressiveEnterSpring() = spring<T>(
        dampingRatio = 0.6f,
        stiffness = 400f
    )

    fun <T> expressiveExitSpring() = spring<T>(
        dampingRatio = 0.8f,
        stiffness = 500f
    )

    // Smooth springs for refined animations
    fun <T> smoothSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Legacy spring configurations (maintained for compatibility)
    fun <T> highBouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun <T> mediumBouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> lowBouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> noBouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> veryHighStiffnessSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // Material Expressive Predefined Motion Patterns
    // Enter animations - expressive deceleration
    const val ENTER_DURATION = DURATION_MEDIUM_3
    val ENTER_EASING = emphasizedDecelerate
    const val ENTER_DURATION_EXPRESSIVE = DURATION_LONG_2
    val ENTER_EASING_EXPRESSIVE = expressiveOvershoot

    // Exit animations - quick acceleration
    const val EXIT_DURATION = DURATION_MEDIUM_2
    val EXIT_EASING = emphasizedAccelerate
    const val EXIT_DURATION_EXPRESSIVE = DURATION_MEDIUM_4
    val EXIT_EASING_EXPRESSIVE = expressiveAnticipate

    // Fade animations
    const val FADE_DURATION = DURATION_SHORT_3
    val FADE_EASING = linear
    const val FADE_DURATION_LONG = DURATION_MEDIUM_1
    val FADE_EASING_EMPHASIZED = emphasized

    // Scale animations - expressive growth
    const val SCALE_DURATION = DURATION_MEDIUM_2
    val SCALE_EASING = standard
    const val SCALE_DURATION_EXPRESSIVE = DURATION_MEDIUM_4
    val SCALE_EASING_EXPRESSIVE = emphasized

    // Slide animations for navigation
    const val SLIDE_DURATION = DURATION_MEDIUM_3
    val SLIDE_EASING = emphasizedDecelerate

    // Shared element transitions
    const val SHARED_ELEMENT_DURATION = DURATION_LONG_1
    val SHARED_ELEMENT_EASING = emphasized
}
