package com.studiocar.studio.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.studiocar.studio.data.models.GridType

@Composable
fun CameraGridOverlay(
    type: GridType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val color = Color.White.copy(alpha = 0.3f)
        val strokeWidth = 1.dp.toPx()

        when (type) {
            GridType.RULE_OF_THIRDS -> {
                // Vertical lines
                drawLine(color, start = androidx.compose.ui.geometry.Offset(width / 3f, 0f), end = androidx.compose.ui.geometry.Offset(width / 3f, height), strokeWidth = strokeWidth)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(2 * width / 3f, 0f), end = androidx.compose.ui.geometry.Offset(2 * width / 3f, height), strokeWidth = strokeWidth)
                // Horizontal lines
                drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, height / 3f), end = androidx.compose.ui.geometry.Offset(width, height / 3f), strokeWidth = strokeWidth)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, 2 * height / 3f), end = androidx.compose.ui.geometry.Offset(width, 2 * height / 3f), strokeWidth = strokeWidth)
            }
            GridType.GOLDEN_RATIO -> {
                val ratio = 0.618f
                drawLine(color, start = androidx.compose.ui.geometry.Offset(width * (1-ratio), 0f), end = androidx.compose.ui.geometry.Offset(width * (1-ratio), height), strokeWidth = strokeWidth)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(width * ratio, 0f), end = androidx.compose.ui.geometry.Offset(width * ratio, height), strokeWidth = strokeWidth)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, height * (1-ratio)), end = androidx.compose.ui.geometry.Offset(width, height * (1-ratio)), strokeWidth = strokeWidth)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, height * ratio), end = androidx.compose.ui.geometry.Offset(width, height * ratio), strokeWidth = strokeWidth)
            }
            GridType.CENTER -> {
                drawLine(color, start = androidx.compose.ui.geometry.Offset(width / 2f, 0f), end = androidx.compose.ui.geometry.Offset(width / 2f, height), strokeWidth = strokeWidth)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, height / 2f), end = androidx.compose.ui.geometry.Offset(width, height / 2f), strokeWidth = strokeWidth)
            }
            GridType.NONE -> {}
        }
    }
}
