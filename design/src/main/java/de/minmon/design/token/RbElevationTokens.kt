package de.minmon.design.token

import androidx.compose.ui.unit.dp

/**
 * Material 3 Expressive Elevation Tokens
 * Defines elevation levels for creating visual hierarchy and depth
 */
object RbElevationTokens {
    /** Surface level - no elevation */
    val level0 = 0.dp

    /** Subtle lift - cards at rest, raised containers */
    val level1 = 1.dp

    /** Moderate lift - app bars, raised cards, emphasized containers */
    val level2 = 3.dp

    /** Strong lift - floating action buttons, navigation drawer */
    val level3 = 6.dp

    /** Modal surfaces - dialogs, bottom sheets, modal overlays */
    val level4 = 8.dp

    /** Maximum lift - dropdowns, menus, tooltips */
    val level5 = 12.dp
}
