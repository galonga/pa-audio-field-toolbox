package de.galonga.audiotoolbox.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Material Expressive Shapes
 * Enhanced shape system with more personality and expressive corner radii
 */
val AudioToolboxShapes = Shapes(
    // Small components: chips, small buttons
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),

    // Medium components: cards, text fields
    medium = RoundedCornerShape(16.dp),

    // Large components: dialogs, bottom sheets
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Expressive shape variations for special use cases
 */
object ExpressiveShapes {
    // Hero cards and feature highlights
    val heroCard = RoundedCornerShape(28.dp)

    // Bottom sheets with prominent top corners
    val bottomSheet = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // Modal surfaces
    val modal = RoundedCornerShape(24.dp)

    // Asymmetric for personality
    val asymmetricSmall = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 20.dp,
        bottomStart = 20.dp,
        bottomEnd = 8.dp
    )

    val asymmetricMedium = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 24.dp,
        bottomStart = 24.dp,
        bottomEnd = 12.dp
    )

    val asymmetricLarge = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 40.dp,
        bottomStart = 40.dp,
        bottomEnd = 16.dp
    )

    // Pills and fully rounded elements
    val pill = RoundedCornerShape(percent = 50)
}
