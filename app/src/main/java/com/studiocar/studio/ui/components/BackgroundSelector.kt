package com.studiocar.studio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.CarBackground
import com.studiocar.studio.data.models.CarFloor

@Composable
fun <T> GenericStudioSelector(
    label: String,
    items: Array<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    getItemLabel: (T) -> String,
    icon: ImageVector
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label.uppercase(),
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                StudioCard(
                    label = getItemLabel(item),
                    isSelected = item == selectedItem,
                    onClick = { onItemSelected(item) },
                    icon = icon
                )
            }
        }
    }
}

@Composable
fun StudioCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00F2FE) else Color.Transparent,
        label = "BorderColor"
    )

    Column(
        modifier = Modifier
            .width(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f))
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = if (isSelected) 
                            listOf(Color(0xFF4FACFE), Color(0xFF00F2FE)) 
                        else 
                            listOf(Color(0xFF232526), Color(0xFF414345))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White.copy(alpha = 0.4f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}



