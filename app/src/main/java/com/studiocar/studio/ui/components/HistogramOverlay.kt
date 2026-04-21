package com.studiocar.studio.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

/**
 * Histograma em tempo real para auxílio na exposição.
 */
@Composable
fun HistogramOverlay(
    data: FloatArray?,
    modifier: Modifier = Modifier
) {
    if (data == null) return

    Box(
        modifier = modifier
            .size(width = 120.dp, height = 60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val maxVal = data.maxOrNull() ?: 1f
            
            val path = Path()
            path.moveTo(0f, height)
            
            data.forEachIndexed { index, value ->
                val x = (index.toFloat() / data.size) * width
                val y = height - (value / maxVal) * height
                path.lineTo(x, y)
            }
            
            path.lineTo(width, height)
            path.close()
            
            drawPath(
                path = path,
                color = Color.White.copy(alpha = 0.7f),
                style = Fill
            )
        }
    }
}
