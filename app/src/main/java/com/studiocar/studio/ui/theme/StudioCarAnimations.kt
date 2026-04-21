package com.studiocar.studio.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

object StudioCarAnimations {
    
    // Spring specs para sensações diferentes
    val LightSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val NaturalSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )
    
    val ResponsiveSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessHigh
    )

    // Tween specs para fades e transições suaves
    val PremiumTween = tween<Float>(
        durationMillis = 400,
        easing = FastOutSlowInEasing
    )
    
    val SlowFade = tween<Float>(
        durationMillis = 600,
        easing = LinearOutSlowInEasing
    )

    // Shimmer effect para loading e processamento
    @Composable
    fun rememberShimmerBrush(): Brush {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_translate"
        )

        return Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.1f),
                Color.Transparent,
            ),
            start = androidx.compose.ui.geometry.Offset(translateAnim - 500f, translateAnim - 500f),
            end = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim)
        )
    }
}

/**
 * Modifier para adicionar um efeito de pulso/glow quando selecionado
 */
fun Modifier.pulseSelection(
    selected: Boolean,
    pulseColor: Color = Color.Cyan
): Modifier = composed {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = StudioCarAnimations.NaturalSpring,
        label = "pulse_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (selected) 0.6f else 0f,
        animationSpec = StudioCarAnimations.PremiumTween,
        label = "pulse_alpha"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.drawWithContent {
        drawContent()
        if (selected) {
            drawCircle(
                color = pulseColor,
                radius = size.maxDimension / 2 * 1.2f,
                alpha = alpha,
                blendMode = BlendMode.Screen
            )
        }
    }
}

/**
 * Modifier para animação de entrada elegante
 */
fun Modifier.premiumEntrance(
    delay: Int = 0,
    duration: Int = 500
): Modifier = composed {
    val animationState = remember { Animatable(0f) }
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        animationState.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = duration, easing = FastOutSlowInEasing)
        )
    }

    this.graphicsLayer {
        alpha = animationState.value
        translationY = (1f - animationState.value) * 20.dp.toPx()
        scaleX = 0.95f + (animationState.value * 0.05f)
        scaleY = 0.95f + (animationState.value * 0.05f)
    }
}
