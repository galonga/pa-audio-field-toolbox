package de.galonga.audiotoolbox.design.utils

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

/**
 * Material 3 Surface Utilities
 * Helper functions for creating tonal surfaces, calculating elevation overlays, and managing state layers
 */
object SurfaceUtils {

    /**
     * Creates a tonal surface color by blending the base color with the surface
     *
     * @param baseColor The color to blend (typically a primary, secondary, or brand color)
     * @param intensity The blend intensity (0.0 = no tint, 1.0 = full color)
     * @return The resulting tonal surface color
     */
    @Composable
    fun tonalSurfaceColor(baseColor: Color, intensity: Float = 0.08f): Color {
        val surface = MaterialTheme.colorScheme.surface
        return lerp(surface, baseColor, intensity.coerceIn(0f, 1f))
    }

    /**
     * Calculates the surface color at a given elevation using Material 3's surface tint overlay
     * This follows the M3 spec for elevation overlays in both light and dark themes
     *
     * @param elevation The elevation level
     * @param surfaceTint The tint color to overlay (defaults to primary)
     * @return The resulting surface color with elevation tint applied
     */
    @Composable
    fun surfaceColorAtElevation(elevation: Dp, surfaceTint: Color = MaterialTheme.colorScheme.primary): Color {
        if (elevation == 0.dp) return MaterialTheme.colorScheme.surface

        // Material 3 uses a logarithmic curve for elevation overlays
        // The formula: alpha = ((4.5 * ln(elevation + 1)) + 2) / 100
        val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f

        return surfaceTint
            .copy(alpha = alpha.coerceIn(0f, 1f))
            .compositeOver(MaterialTheme.colorScheme.surface)
    }

    /**
     * Gets the appropriate state layer opacity based on the interaction state
     * Follows Material 3 interaction state specifications
     *
     * @param interactionState The current interaction state
     * @return The opacity value for the state layer (0.0 - 1.0)
     */
    @Composable
    fun stateLayerOpacity(interactionState: InteractionSource): Float {
        val isHovered by interactionState.collectIsHoveredAsState()
        val isFocused by interactionState.collectIsFocusedAsState()
        val isPressed by interactionState.collectIsPressedAsState()
        val isDragged by interactionState.collectIsDraggedAsState()

        return when {
            isDragged -> StateLayerOpacity.DRAGGED
            isPressed -> StateLayerOpacity.PRESSED
            isFocused -> StateLayerOpacity.FOCUS
            isHovered -> StateLayerOpacity.HOVER
            else -> 0f
        }
    }

    /**
     * Determines if a state layer should be visible
     */
    @Composable
    fun hasStateLayer(interactionState: InteractionSource): Boolean = stateLayerOpacity(interactionState) > 0f

    /**
     * Gets the state layer color with appropriate opacity
     *
     * @param interactionState The current interaction state
     * @param stateLayerColor The base color for the state layer (defaults to onSurface)
     * @return The state layer color with opacity applied
     */
    @Composable
    fun stateLayerColor(interactionState: InteractionSource, stateLayerColor: Color = MaterialTheme.colorScheme.onSurface): Color {
        val opacity = stateLayerOpacity(interactionState)
        return stateLayerColor.copy(alpha = opacity)
    }

    /**
     * Material 3 State Layer Opacity Constants
     */
    object StateLayerOpacity {
        /** Hover state opacity - 8% */
        const val HOVER = 0.08f

        /** Focus state opacity - 12% */
        const val FOCUS = 0.12f

        /** Pressed state opacity - 12% */
        const val PRESSED = 0.12f

        /** Dragged state opacity - 16% */
        const val DRAGGED = 0.16f
    }

    /**
     * Material 3 Surface Container Levels
     * Defines the elevation tint intensity for different surface container levels
     */
    object SurfaceContainerLevel {
        /** Lowest surface container - subtle tint */
        const val LOWEST = 0.03f

        /** Low surface container */
        const val LOW = 0.05f

        /** Default surface container */
        const val DEFAULT = 0.08f

        /** High surface container */
        const val HIGH = 0.11f

        /** Highest surface container - maximum tint */
        const val HIGHEST = 0.14f
    }

    /**
     * Creates a surface container color at the specified level
     *
     * @param level The container level intensity
     * @return The resulting surface container color
     */
    @Composable
    fun surfaceContainerColor(level: Float = SurfaceContainerLevel.DEFAULT): Color = tonalSurfaceColor(
        baseColor = MaterialTheme.colorScheme.primary,
        intensity = level
    )

    /**
     * Calculates appropriate text color for a given surface based on contrast
     *
     * @param surface The surface color
     * @param lightColor The color to use on dark surfaces (default: onSurface for light theme)
     * @param darkColor The color to use on light surfaces (default: onSurface for dark theme)
     * @return The appropriate text color
     */
    @Composable
    fun onSurfaceColor(surface: Color, lightColor: Color = MaterialTheme.colorScheme.onSurface, darkColor: Color = MaterialTheme.colorScheme.onSurface): Color {
        // Calculate relative luminance
        val luminance = surface.luminance()
        // Use light color on dark surfaces (luminance < 0.5), dark color on light surfaces
        return if (luminance < 0.5f) lightColor else darkColor
    }

    /**
     * Calculates the relative luminance of a color
     */
    private fun Color.luminance(): Float {
        // Convert to linear RGB
        fun linearize(component: Float): Float = if (component <= 0.03928f) {
            component / 12.92f
        } else {
            Math.pow(((component + 0.055) / 1.055).toDouble(), 2.4).toFloat()
        }

        val r = linearize(red)
        val g = linearize(green)
        val b = linearize(blue)

        // Calculate relative luminance using ITU-R BT.709 coefficients
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }
}
