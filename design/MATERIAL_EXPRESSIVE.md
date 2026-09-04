# Material Expressive Design System

This document outlines the Material Expressive design implementation for the MinMon Android app. Material Expressive builds on Material Design 3 with enhanced personality, expressiveness, and delight.

## Overview

Material Expressive enhances the standard Material Design 3 system with:

- ✨ **Enhanced Typography**: More personality with expressive display styles and optimized readability
- 🎨 **Expressive Shapes**: Larger, bolder corner radii for visual distinction (up to 48dp)
- 🎭 **Expressive Motion**: Spring animations, overshoots, and emphasized easing for delightful interactions
- 🏗️ **Enhanced Components**: Buttons, cards, and surfaces with more visual prominence

## Typography System

### Display Styles (Antic Didone)

The display font family creates dramatic, attention-grabbing headlines with personality:

```kotlin
// Hero moments and marketing content
MaterialTheme.typography.displayLarge     // 64sp, line height 72sp
MaterialTheme.typography.displayMedium    // 52sp, line height 60sp
MaterialTheme.typography.displaySmall     // 44sp, line height 52sp
```

### Headlines (Antic Didone)

For section headers and content hierarchy:

```kotlin
MaterialTheme.typography.headlineLarge    // 36sp, line height 44sp
MaterialTheme.typography.headlineMedium   // 32sp, line height 40sp
MaterialTheme.typography.headlineSmall    // 28sp, line height 36sp
```

### Titles (Mixed)

Titles combine display and body fonts for hierarchy:

```kotlin
MaterialTheme.typography.titleLarge       // 24sp, Antic Didone
MaterialTheme.typography.titleMedium      // 18sp, Alef Bold
MaterialTheme.typography.titleSmall       // 16sp, Alef Bold
```

### Body Text (Alef)

Optimized for readability with appropriate letter spacing:

```kotlin
MaterialTheme.typography.bodyLarge        // 16sp, line height 24sp
MaterialTheme.typography.bodyMedium       // 14sp, line height 20sp
MaterialTheme.typography.bodySmall        // 12sp, line height 16sp
```

### Usage Example

```kotlin
@Composable
fun MyScreen() {
    Column {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "Discover amazing content",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "This is body text with enhanced readability.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
```

## Shape System

### Material Theme Shapes

The theme provides built-in shape scales:

```kotlin
MaterialTheme.shapes.extraSmall    // 4dp - Chips, small buttons
MaterialTheme.shapes.small         // 8dp - Small cards
MaterialTheme.shapes.medium        // 16dp - Standard cards, text fields
MaterialTheme.shapes.large         // 24dp - Dialogs, prominent cards
MaterialTheme.shapes.extraLarge    // 32dp - Hero surfaces
```

### Expressive Shape Families

For more personality and distinction:

```kotlin
import de.minmon.design.theme.ExpressiveShapes

// Hero cards and feature highlights
ExpressiveShapes.heroCard              // 28dp all corners

// Bottom sheets
ExpressiveShapes.bottomSheet           // 28dp top corners only

// Modal surfaces
ExpressiveShapes.modal                 // 24dp all corners

// Asymmetric shapes for personality
ExpressiveShapes.asymmetricSmall       // 8-20dp varied corners
ExpressiveShapes.asymmetricLarge       // 16-40dp varied corners

// Pills
ExpressiveShapes.pill                  // 50% rounded
```

### RbShapeTokens (Low-level)

Direct access to corner radius values:

```kotlin
import de.minmon.design.token.RbShapeTokens

RbShapeTokens.rbCornerRadius8     // 8.dp
RbShapeTokens.rbCornerRadius16    // 16.dp
RbShapeTokens.rbCornerRadius24    // 24.dp
RbShapeTokens.rbCornerRadius32    // 32.dp
RbShapeTokens.rbCornerRadius48    // 48.dp

// Pre-built shapes
RbShapeTokens.shapeMedium         // RoundedCornerShape(16.dp)
RbShapeTokens.shapeLarge          // RoundedCornerShape(24.dp)
RbShapeTokens.shapeExtraLarge     // RoundedCornerShape(32.dp)
```

### Usage Example

```kotlin
Card(
    shape = MaterialTheme.shapes.large,  // 24dp rounded
    modifier = Modifier.padding(16.dp)
) {
    // Card content
}

// Or use expressive shapes
Card(
    shape = ExpressiveShapes.heroCard,  // 28dp rounded
    modifier = Modifier.padding(16.dp)
) {
    // Hero content
}
```

## Motion System

### Duration Tokens

```kotlin
import de.minmon.design.token.MotionTokens

// Short animations (50-200ms)
MotionTokens.DURATION_SHORT_1 to DURATION_SHORT_4

// Medium animations (250-400ms)
MotionTokens.DURATION_MEDIUM_1 to DURATION_MEDIUM_4

// Long animations (450-600ms)
MotionTokens.DURATION_LONG_1 to DURATION_LONG_4

// Extra long for expressive moments (700-1000ms)
MotionTokens.DURATION_EXTRA_LONG_1 to DURATION_EXTRA_LONG_4
```

### Easing Curves

```kotlin
// Emphasized easing (Material Expressive)
MotionTokens.emphasizedDecelerate    // Entering elements
MotionTokens.emphasizedAccelerate    // Exiting elements
MotionTokens.emphasized              // General emphasis

// Expressive easing with overshoot
MotionTokens.expressiveOvershoot     // Playful entrances
MotionTokens.expressiveAnticipate    // Playful exits

// Standard easing
MotionTokens.standard                // Everyday interactions
MotionTokens.standardDecelerate      // Deceleration
MotionTokens.standardAccelerate      // Acceleration
```

### Spring Animations

```kotlin
// Expressive springs with personality
MotionTokens.expressiveSpring<Float>()        // High energy, bouncy
MotionTokens.expressiveEnterSpring<Float>()   // Entering elements
MotionTokens.expressiveExitSpring<Float>()    // Exiting elements

// Smooth springs
MotionTokens.smoothSpring<Float>()            // No bounce, smooth
```

### Predefined Motion Patterns

```kotlin
// Enter animations
MotionTokens.ENTER_DURATION              // 350ms
MotionTokens.ENTER_EASING                // emphasizedDecelerate
MotionTokens.ENTER_DURATION_EXPRESSIVE   // 500ms
MotionTokens.ENTER_EASING_EXPRESSIVE     // expressiveOvershoot

// Exit animations
MotionTokens.EXIT_DURATION               // 300ms
MotionTokens.EXIT_EASING                 // emphasizedAccelerate

// Fade animations
MotionTokens.FADE_DURATION               // 150ms
MotionTokens.FADE_DURATION_LONG          // 250ms

// Scale animations
MotionTokens.SCALE_DURATION              // 300ms
MotionTokens.SCALE_DURATION_EXPRESSIVE   // 400ms
MotionTokens.SCALE_EASING_EXPRESSIVE     // emphasized

// Navigation
MotionTokens.SLIDE_DURATION              // 350ms
MotionTokens.SLIDE_EASING                // emphasizedDecelerate

// Shared element transitions
MotionTokens.SHARED_ELEMENT_DURATION     // 450ms
MotionTokens.SHARED_ELEMENT_EASING       // emphasized
```

### Usage Example

```kotlin
// Animate scale with spring
val scale by animateFloatAsState(
    targetValue = if (expanded) 1f else 0.8f,
    animationSpec = MotionTokens.expressiveSpring()
)

// Animate with tween and easing
val alpha by animateFloatAsState(
    targetValue = if (visible) 1f else 0f,
    animationSpec = tween(
        durationMillis = MotionTokens.FADE_DURATION_LONG,
        easing = MotionTokens.emphasized
    )
)
```

## Expressive Components

### ExpressiveCard

Enhanced card with spring animations and interactive elevation:

```kotlin
import de.minmon.design.components.ExpressiveCard

ExpressiveCard(
    modifier = Modifier.padding(16.dp),
    onClick = { /* handle click */ }
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Card Title", style = MaterialTheme.typography.titleLarge)
        Text("Card content...", style = MaterialTheme.typography.bodyMedium)
    }
}
```

Features:
- ✨ 28dp corner radius by default
- 🎭 Spring animation on press
- 📈 Animated elevation
- 🎨 Scale feedback on interaction

### ExpressiveHeroCard

For prominent, hero content:

```kotlin
ExpressiveHeroCard(
    modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    onClick = { /* handle click */ }
) {
    // Hero content
}
```

### Expressive Buttons

Three button variants with spring animations:

```kotlin
import de.minmon.design.components.*

// Primary button - filled
ExpressivePrimaryButton(
    onClick = { /* action */ }
) {
    Text("Primary Action")
}

// Secondary button - tonal fill
ExpressiveSecondaryButton(
    onClick = { /* action */ }
) {
    Text("Secondary Action")
}

// Outlined button
ExpressiveOutlinedButton(
    onClick = { /* action */ }
) {
    Text("Outlined Action")
}
```

All expressive buttons feature:
- 🎯 24dp corner radius
- 🎭 Spring scale animation on press
- 🎨 Material 3 color roles
- ♿ Full accessibility support

## Expressive Animations

### Pre-built Animation Patterns

```kotlin
import de.minmon.design.animations.*

// Fade in with scale
ExpressiveFadeInScale(visible = isVisible) {
    // Content
}

// Slide in from bottom (dialogs, sheets)
ExpressiveSlideInFromBottom(visible = isVisible) {
    // Content
}

// Slide in from end (navigation)
ExpressiveSlideInFromEnd(visible = isVisible) {
    // Content
}

// Expand vertically (expandable lists)
ExpressiveExpandVertically(visible = isExpanded) {
    // Expanded content
}
```

### Bouncy Entrance Modifier

Apply a bouncy scale animation on first appearance:

```kotlin
Text(
    text = "Hello!",
    modifier = Modifier.bouncyEntrance()
)
```

### Staggered List Animations

Animate list items with sequential delay:

```kotlin
items.forEachIndexed { index, item ->
    ExpressiveStaggeredListItem(
        index = index,
        visible = isVisible
    ) {
        ListItemContent(item)
    }
}
```

## Spacing and Elevation

### ExpressivePadding

Consistent spacing values:

```kotlin
import de.minmon.design.components.ExpressivePadding

ExpressivePadding.extraSmall  // 4.dp
ExpressivePadding.small       // 8.dp
ExpressivePadding.medium      // 16.dp
ExpressivePadding.large       // 24.dp
ExpressivePadding.extraLarge  // 32.dp
ExpressivePadding.huge        // 48.dp
```

### ExpressiveElevation

Surface elevation hierarchy:

```kotlin
import de.minmon.design.components.ExpressiveElevation

ExpressiveElevation.level0    // 0.dp - Flat
ExpressiveElevation.level1    // 2.dp - Subtle
ExpressiveElevation.level2    // 4.dp - Standard cards
ExpressiveElevation.level3    // 8.dp - Elevated cards
ExpressiveElevation.level4    // 12.dp - Dialogs
ExpressiveElevation.level5    // 16.dp - Navigation drawer
```

## Navigation Animations

Pre-configured expressive navigation transitions:

```kotlin
import de.minmon.design.token.NavigationAnimations

// Screen transitions
composable(
    route = "details",
    enterTransition = { NavigationAnimations.screenEnterTransition() },
    exitTransition = { NavigationAnimations.screenExitTransition() },
    popEnterTransition = { NavigationAnimations.screenPopEnterTransition() },
    popExitTransition = { NavigationAnimations.screenPopExitTransition() }
) {
    DetailsScreen()
}
```

Features:
- 📱 Slide + fade + scale for depth
- ⚡ 350ms enter / 300ms exit
- 🎭 Emphasized easing curves
- ↔️ Parallax effect on pop transitions

## Best Practices

### When to Use Expressive Elements

✅ **Use expressive design for:**
- Hero content and featured items
- Primary call-to-action buttons
- Onboarding flows
- Celebration moments
- App launches and splash screens
- Empty states with personality

❌ **Use standard Material 3 for:**
- Dense information displays
- Data tables
- Settings screens
- Form inputs
- Utility screens

### Typography Hierarchy

1. **Display Large**: Marketing hero sections only
2. **Display Medium/Small**: Feature introductions
3. **Headline Large/Medium**: Screen titles
4. **Headline Small**: Section headers
5. **Title Large**: Card headers
6. **Title Medium/Small**: List item titles
7. **Body Large**: Primary body text
8. **Body Medium**: Secondary text
9. **Body Small**: Captions, timestamps

### Shape Guidelines

- **Small components** (4-8dp): Chips, small buttons, badges
- **Standard components** (12-16dp): Cards, buttons, text fields
- **Large components** (24-28dp): Dialogs, prominent cards, hero sections
- **Extra large** (32-48dp): Bottom sheets, immersive surfaces, splash screens

### Motion Guidelines

- **Fast (50-150ms)**: Simple state changes, color transitions
- **Medium (200-400ms)**: Component entrances, button presses
- **Long (450-600ms)**: Screen transitions, complex animations
- **Extra long (700-1000ms)**: Hero moments, celebration animations

Use springs for:
- Interactive elements (buttons, cards)
- Playful moments
- Bouncy, personality-driven animations

Use easing curves for:
- Screen transitions
- Predictable, controlled motion
- Accessibility-friendly animations

## Migration Guide

### Updating Existing Components

**Before:**
```kotlin
Button(onClick = { }) {
    Text("Click me")
}
```

**After:**
```kotlin
ExpressivePrimaryButton(onClick = { }) {
    Text("Click me")
}
```

**Before:**
```kotlin
Card(
    shape = RoundedCornerShape(16.dp)
) {
    // Content
}
```

**After:**
```kotlin
ExpressiveCard {
    // Content
}
// Or use Material theme shapes:
Card(
    shape = MaterialTheme.shapes.large  // 24dp
) {
    // Content
}
```

### Typography Updates

Typography is automatically applied through `MaterialTheme.typography`. No code changes needed - the enhanced type scale is now in effect!

## Accessibility

All expressive components maintain full accessibility:

- ✅ Sufficient touch targets (48dp minimum)
- ✅ Color contrast ratios (WCAG AA compliant)
- ✅ Screen reader support
- ✅ Reduced motion support (respect system preferences)
- ✅ Keyboard navigation

To respect reduced motion preferences:

```kotlin
val windowInsetsController = LocalWindowInsetsController.current
val isReducedMotion = /* check system preference */

val animationSpec = if (isReducedMotion) {
    tween(durationMillis = 0)  // Instant
} else {
    MotionTokens.expressiveSpring()
}
```

## Resources

- [Material Design 3](https://m3.material.io/)
- [Material Expressive Theme](https://m3.material.io/styles/motion/overview)
- [Compose Animation](https://developer.android.com/jetpack/compose/animation)
- [Material Motion System](https://m3.material.io/styles/motion/easing-and-duration)

---

**Last Updated**: February 2026
**MinMon Android Design System v2.0**
