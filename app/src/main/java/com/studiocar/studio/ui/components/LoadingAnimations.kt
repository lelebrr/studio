package com.studiocar.studio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.ui.theme.StudioCyan

@Composable
fun StudioPulseLoading(
    modifier: Modifier = Modifier,
    message: String = "IA PROCESSANDO..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Círculos concêntricos pulsantes
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(StudioCyan.copy(alpha = alpha * 0.2f))
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(scale * 0.8f)
                    .clip(CircleShape)
                    .background(StudioCyan.copy(alpha = alpha * 0.5f))
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(StudioCyan)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = message.uppercase(),
            color = StudioCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            modifier = Modifier.scale(0.9f + (scale * 0.1f))
        )
    }
}

@Composable
fun PremiumShimmerBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "xOffset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.05f),
            Color.White.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.05f),
            Color.Transparent
        ),
        start = androidx.compose.ui.geometry.Offset(xOffset, 0f),
        end = androidx.compose.ui.geometry.Offset(xOffset + 300f, 300f)
    )

    Box(modifier = modifier) {
        content()
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush)
        )
    }
}
