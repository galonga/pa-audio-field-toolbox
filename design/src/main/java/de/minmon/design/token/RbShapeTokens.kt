package de.minmon.design.token

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Material Expressive Shape System
 * Provides expressive, personality-driven corner radius values
 * for creating visually distinctive and engaging interfaces.
 */
object RbShapeTokens {
    // Base corner radius values
    val rbCornerRadius0 = 0.dp
    val rbCornerRadius4 = 4.dp
    val rbCornerRadius8 = 8.dp
    val rbCornerRadius12 = 12.dp
    val rbCornerRadius16 = 16.dp
    val rbCornerRadius20 = 20.dp
    val rbCornerRadius24 = 24.dp
    val rbCornerRadius28 = 28.dp
    val rbCornerRadius32 = 32.dp
    val rbCornerRadius36 = 36.dp
    val rbCornerRadius40 = 40.dp
    val rbCornerRadius48 = 48.dp

    // Material Expressive Shape Families
    // Small components: buttons, chips, small cards
    val shapeSmall = RoundedCornerShape(rbCornerRadius8)
    val shapeSmallTop = RoundedCornerShape(
        topStart = rbCornerRadius8,
        topEnd = rbCornerRadius8,
        bottomStart = rbCornerRadius0,
        bottomEnd = rbCornerRadius0
    )

    // Medium components: standard cards, dialogs
    val shapeMedium = RoundedCornerShape(rbCornerRadius16)
    val shapeMediumTop = RoundedCornerShape(
        topStart = rbCornerRadius16,
        topEnd = rbCornerRadius16,
        bottomStart = rbCornerRadius0,
        bottomEnd = rbCornerRadius0
    )

    // Large components: hero cards, bottom sheets
    val shapeLarge = RoundedCornerShape(rbCornerRadius24)
    val shapeLargeTop = RoundedCornerShape(
        topStart = rbCornerRadius24,
        topEnd = rbCornerRadius24,
        bottomStart = rbCornerRadius0,
        bottomEnd = rbCornerRadius0
    )
    val shapeLargeEnd = RoundedCornerShape(
        topStart = rbCornerRadius0,
        topEnd = rbCornerRadius24,
        bottomStart = rbCornerRadius0,
        bottomEnd = rbCornerRadius24
    )

    // Extra Large components: immersive surfaces, hero moments
    val shapeExtraLarge = RoundedCornerShape(rbCornerRadius32)
    val shapeExtraLargeTop = RoundedCornerShape(
        topStart = rbCornerRadius32,
        topEnd = rbCornerRadius32,
        bottomStart = rbCornerRadius0,
        bottomEnd = rbCornerRadius0
    )

    // Expressive asymmetric shapes for personality
    val shapeAsymmetricSmall = RoundedCornerShape(
        topStart = rbCornerRadius4,
        topEnd = rbCornerRadius16,
        bottomStart = rbCornerRadius16,
        bottomEnd = rbCornerRadius4
    )

    val shapeAsymmetricMedium = RoundedCornerShape(
        topStart = rbCornerRadius8,
        topEnd = rbCornerRadius24,
        bottomStart = rbCornerRadius24,
        bottomEnd = rbCornerRadius8
    )

    val shapeAsymmetricLarge = RoundedCornerShape(
        topStart = rbCornerRadius16,
        topEnd = rbCornerRadius40,
        bottomStart = rbCornerRadius40,
        bottomEnd = rbCornerRadius16
    )

    // Full rounded for pills and circular elements
    val shapeFull = RoundedCornerShape(percent = 50)
}
