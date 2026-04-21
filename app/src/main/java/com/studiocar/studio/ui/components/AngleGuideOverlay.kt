package com.studiocar.studio.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.PhotoAngle

/**
 * Overlay inteligente de guia de enquadramento com silhuetas por ângulo
 * e dicas de posicionamento em tempo real.
 */
@Composable
fun AngleGuideOverlay(
    currentAngle: PhotoAngle,
    onAngleSelected: (PhotoAngle) -> Unit,
    guidanceText: String? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Silhueta do carro baseada no ângulo
        CarAngleSilhouette(angle = currentAngle)

        // Dica de posicionamento animada
        guidanceText?.let { text ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = text,
                        color = Color.Cyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Selector de ângulo na parte inferior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PhotoAngle.entries.forEach { angle ->
                AngleChip(
                    angle = angle,
                    isSelected = angle == currentAngle,
                    onClick = { onAngleSelected(angle) }
                )
            }
        }
    }
}

@Composable
private fun AngleChip(
    angle: PhotoAngle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.15f),
        label = "chipBg"
    )
    val textColor = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = angle.label,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
        )
    }
}

@Composable
private fun CarAngleSilhouette(angle: PhotoAngle) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "silhouetteAlpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val path = when (angle) {
            PhotoAngle.FRONT_THREE_QUARTER -> Path().apply {
                // ¾ frontal — perspectiva levemente diagonal
                moveTo(w * 0.12f, h * 0.68f)
                lineTo(w * 0.18f, h * 0.58f)
                lineTo(w * 0.35f, h * 0.54f)
                lineTo(w * 0.45f, h * 0.42f)
                lineTo(w * 0.65f, h * 0.42f)
                lineTo(w * 0.78f, h * 0.54f)
                lineTo(w * 0.92f, h * 0.58f)
                lineTo(w * 0.92f, h * 0.72f)
                lineTo(w * 0.12f, h * 0.72f)
                close()
            }
            PhotoAngle.LEFT_SIDE -> Path().apply {
                moveTo(w * 0.05f, h * 0.65f)
                lineTo(w * 0.10f, h * 0.58f)
                lineTo(w * 0.30f, h * 0.55f)
                lineTo(w * 0.42f, h * 0.43f)
                lineTo(w * 0.62f, h * 0.43f)
                lineTo(w * 0.75f, h * 0.55f)
                lineTo(w * 0.95f, h * 0.58f)
                lineTo(w * 0.95f, h * 0.75f)
                lineTo(w * 0.05f, h * 0.75f)
                close()
            }
            PhotoAngle.RIGHT_SIDE -> Path().apply {
                moveTo(w * 0.95f, h * 0.65f)
                lineTo(w * 0.90f, h * 0.58f)
                lineTo(w * 0.70f, h * 0.55f)
                lineTo(w * 0.58f, h * 0.43f)
                lineTo(w * 0.38f, h * 0.43f)
                lineTo(w * 0.25f, h * 0.55f)
                lineTo(w * 0.05f, h * 0.58f)
                lineTo(w * 0.05f, h * 0.75f)
                lineTo(w * 0.95f, h * 0.75f)
                close()
            }
            PhotoAngle.REAR -> Path().apply {
                moveTo(w * 0.20f, h * 0.68f)
                lineTo(w * 0.25f, h * 0.55f)
                lineTo(w * 0.35f, h * 0.48f)
                lineTo(w * 0.65f, h * 0.48f)
                lineTo(w * 0.75f, h * 0.55f)
                lineTo(w * 0.80f, h * 0.68f)
                lineTo(w * 0.80f, h * 0.75f)
                lineTo(w * 0.20f, h * 0.75f)
                close()
            }
            PhotoAngle.ANGLE_45 -> Path().apply {
                moveTo(w * 0.10f, h * 0.66f)
                lineTo(w * 0.16f, h * 0.56f)
                lineTo(w * 0.38f, h * 0.52f)
                lineTo(w * 0.48f, h * 0.40f)
                lineTo(w * 0.68f, h * 0.40f)
                lineTo(w * 0.82f, h * 0.52f)
                lineTo(w * 0.93f, h * 0.56f)
                lineTo(w * 0.93f, h * 0.74f)
                lineTo(w * 0.10f, h * 0.74f)
                close()
            }
            PhotoAngle.FRONT -> Path().apply {
                moveTo(w * 0.22f, h * 0.68f)
                lineTo(w * 0.26f, h * 0.56f)
                lineTo(w * 0.36f, h * 0.50f)
                lineTo(w * 0.64f, h * 0.50f)
                lineTo(w * 0.74f, h * 0.56f)
                lineTo(w * 0.78f, h * 0.68f)
                lineTo(w * 0.78f, h * 0.75f)
                lineTo(w * 0.22f, h * 0.75f)
                close()
            }
        }
        drawPath(
            path = path,
            color = Color.Cyan.copy(alpha = alpha),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
