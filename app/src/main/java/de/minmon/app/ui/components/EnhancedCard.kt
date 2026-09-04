package de.minmon.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.minmon.design.token.MotionTokens
import de.minmon.design.token.RbElevationTokens
import de.minmon.design.token.RbSpacing
import de.minmon.design.utils.SurfaceUtils

@Composable
fun EnhancedCard(modifier: Modifier = Modifier, model: EnhancedCardModel, content: @Composable () -> Unit) {
    val accentBarHeight = remember { Animatable(0f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(20f) }
    val iconRotation = remember { Animatable(0f) }
    val iconScale = remember { Animatable(0f) }

    LaunchedEffect(model.title) {
        accentBarHeight.snapTo(0f)
        titleAlpha.snapTo(0f)
        titleOffset.snapTo(20f)
        iconRotation.snapTo(0f)
        iconScale.snapTo(0f)
        accentBarHeight.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = MotionTokens.DURATION_MEDIUM_3,
                easing = MotionTokens.emphasizedDecelerate
            )
        )

        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = MotionTokens.DURATION_MEDIUM_2,
                easing = MotionTokens.emphasizedDecelerate
            )
        )
        titleOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = MotionTokens.DURATION_MEDIUM_2,
                easing = MotionTokens.emphasizedDecelerate
            )
        )

        if (model.icon != null) {
            when (model.iconAnimation) {
                IconAnimation.BOUNCE -> {
                    iconScale.animateTo(
                        targetValue = 1f,
                        animationSpec = MotionTokens.mediumBouncySpring()
                    )
                }

                IconAnimation.ROTATE -> {
                    iconScale.snapTo(1f)
                    iconRotation.animateTo(
                        targetValue = 360f,
                        animationSpec = tween(
                            durationMillis = MotionTokens.DURATION_LONG_2,
                            easing = MotionTokens.standard
                        )
                    )
                }

                IconAnimation.PULSE -> {
                    iconScale.animateTo(
                        targetValue = 1f,
                        animationSpec = MotionTokens.highBouncySpring()
                    )
                }

                IconAnimation.NONE -> {
                    iconScale.snapTo(1f)
                }
            }
        }
    }

    val surfaceColor = when (model.layoutType) {
        StorybookLayoutType.ELEVATED_CARD -> {
            SurfaceUtils.surfaceColorAtElevation(model.elevation)
        }

        StorybookLayoutType.TONAL_SURFACE -> {
            SurfaceUtils.tonalSurfaceColor(
                baseColor = model.accentColor ?: MaterialTheme.colorScheme.primary,
                intensity = 0.12f
            )
        }

        StorybookLayoutType.OUTLINED_EXPRESSIVE -> {
            MaterialTheme.colorScheme.surface
        }
    }

    val containerModifier = when (model.layoutType) {
        StorybookLayoutType.ELEVATED_CARD -> {
            modifier
        }

        StorybookLayoutType.TONAL_SURFACE -> {
            modifier
        }

        StorybookLayoutType.OUTLINED_EXPRESSIVE -> {
            val gradientColors = listOf(
                model.accentColor ?: MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary,
                model.accentColor?.copy(alpha = 0.7f) ?: MaterialTheme.colorScheme.tertiary
            )
            modifier.border(
                width = 1.dp,
                brush = Brush.linearGradient(gradientColors),
                shape = RoundedCornerShape(RbSpacing.space16)
            )
        }
    }

    Box(modifier = Modifier.padding(top = RbSpacing.space16)) {
        Surface(
            modifier = containerModifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = MotionTokens.DURATION_MEDIUM_2,
                        easing = MotionTokens.emphasizedDecelerate
                    )
                ),
            shape = RoundedCornerShape(RbSpacing.space16),
            color = surfaceColor,
            tonalElevation = if (model.layoutType == StorybookLayoutType.ELEVATED_CARD) model.elevation else 0.dp,
            shadowElevation = if (model.layoutType == StorybookLayoutType.ELEVATED_CARD) model.elevation else 0.dp
        ) {
            Row(
                modifier = Modifier.padding(RbSpacing.space16)
            ) {
                // Animated accent bar
                if (model.showAccentBar) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(accentBarHeight.value.dp * 100) // Animated height
                            .clip(RoundedCornerShape(2.dp))
                            .background(model.accentColor ?: MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(RbSpacing.space16))
                }

                // Content column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = MotionTokens.DURATION_MEDIUM_2,
                                easing = MotionTokens.emphasizedDecelerate
                            )
                        )
                ) {
                    // Title row with optional icon
                    if (model.title != null || model.icon != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = if (model.subtitle != null) RbSpacing.space8 else RbSpacing.space16)
                        ) {
                            // Animated icon
                            if (model.icon != null) {
                                Icon(
                                    imageVector = model.icon,
                                    contentDescription = null,
                                    tint = model.accentColor ?: MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .scale(iconScale.value)
                                        .rotate(iconRotation.value)
                                )
                                Spacer(modifier = Modifier.width(RbSpacing.space12))
                            }

                            // Animated title
                            if (model.title != null) {
                                Text(
                                    text = model.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = titleAlpha.value),
                                    modifier = Modifier.padding(top = titleOffset.value.dp)
                                )
                            }
                        }

                        // Subtitle
                        if (model.subtitle != null) {
                            Text(
                                text = model.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = titleAlpha.value),
                                modifier = Modifier.padding(bottom = RbSpacing.space16)
                            )
                        }
                    }
                    content()
                }
            }
        }
    }
}

data class EnhancedCardModel(
    val title: String? = null,
    val subtitle: String? = null,
    val accentColor: Color? = null,
    val elevation: Dp = RbElevationTokens.level1,
    val showAccentBar: Boolean = true,
    val icon: ImageVector? = null,
    val iconAnimation: IconAnimation = IconAnimation.NONE,
    val layoutType: StorybookLayoutType = StorybookLayoutType.ELEVATED_CARD,
)

enum class StorybookLayoutType {
    ELEVATED_CARD,
    TONAL_SURFACE,
    OUTLINED_EXPRESSIVE,
}

enum class IconAnimation {
    NONE,
    BOUNCE,
    ROTATE,
    PULSE,
}
