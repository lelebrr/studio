package com.studiocar.studio.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Componente de Visualização Ultra Quality.
 * Suporta inspeção de detalhes em 12x com filtragem de alta qualidade.
 */
@Composable
fun ZoomableImage(
    bitmap: Bitmap,
    modifier: Modifier = Modifier,
    minScale: Float = 0.8f,
    maxScale: Float = 12f, // Aumentado para inspeção 4K+
    onZoomChanged: (Float) -> Unit = {}
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val scope = rememberCoroutineScope()
    
    var showIndicator by remember { mutableStateOf(false) }
    var indicatorJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    fun triggerIndicator() {
        showIndicator = true
        indicatorJob?.cancel()
        indicatorJob = scope.launch {
            delay(1500)
            showIndicator = false
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(minScale, maxScale)
                    if (newScale != scale) {
                        val scaleFactor = newScale / scale
                        offset = (offset * scaleFactor) + (centroid * (1f - scaleFactor))
                        scale = newScale
                        triggerIndicator()
                        onZoomChanged(scale)
                    } else {
                        offset += pan
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        scope.launch {
                            if (scale > 1f) {
                                launch { animate(scale, 1f) { v, _ -> scale = v } }
                                launch { animate(offset.x, 0f) { v, _ -> offset = offset.copy(x = v) } }
                                launch { animate(offset.y, 0f) { v, _ -> offset = offset.copy(y = v) } }
                            } else {
                                val targetScale = 3f
                                val scaleFactor = targetScale / scale
                                val targetOffset = (offset * scaleFactor) + (tapOffset * (1f - scaleFactor))
                                launch { animate(scale, targetScale) { v, _ -> scale = v } }
                                launch { animate(offset.x, targetOffset.x) { v, _ -> offset = offset.copy(x = v) } }
                                launch { animate(offset.y, targetOffset.y) { v, _ -> offset = offset.copy(y = v) } }
                            }
                            triggerIndicator()
                        }
                    }
                )
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit,
            // PRIORIDADE: Qualidade de filtragem alta para zoom 12x
            filterQuality = FilterQuality.High
        )

        // Zoom HUD
        AnimatedVisibility(
            visible = showIndicator,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "${"%.1f".format(scale)}x",
                    color = Color.Cyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private suspend fun animate(
    initialValue: Float,
    targetValue: Float,
    onUpdate: (Float, Float) -> Unit
) {
    Animatable(initialValue).animateTo(
        targetValue = targetValue,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow)
    ) { onUpdate(value, velocity) }
}



