package com.studiocar.studio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.PhotoMode

/**
 * Selector horizontal de modo de fotografia (Exterior, Interior, Motor, Rodas, Detalhe).
 */
@Composable
fun PhotoModeSelector(
    currentMode: PhotoMode,
    onModeSelected: (PhotoMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PhotoMode.entries.forEach { mode ->
            PhotoModeChip(
                mode = mode,
                isSelected = mode == currentMode,
                onClick = { onModeSelected(mode) }
            )
        }
    }
}

@Composable
private fun PhotoModeChip(
    mode: PhotoMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.1f),
        label = "modeChipBg"
    )
    val contentColor = if (isSelected) Color.Black else Color.White.copy(alpha = 0.6f)
    val icon = when (mode) {
        PhotoMode.EXTERIOR -> Icons.Default.DirectionsCar
        PhotoMode.INTERIOR -> Icons.Default.Chair
        PhotoMode.ENGINE -> Icons.Default.Settings
        PhotoMode.WHEELS -> Icons.Default.TripOrigin
        PhotoMode.DETAIL -> Icons.Default.Search
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = mode.label,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = mode.label,
            color = contentColor,
            fontSize = 8.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
        )
    }
}
