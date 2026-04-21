package com.studiocar.studio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.CarBackground
import com.studiocar.studio.data.models.CarFloor

@Composable
fun SelectionList(
    title: String,
    items: List<Any>,
    selected: Any?,
    onSelect: (Any) -> Unit,
    customItems: List<String> = emptyList(),
    onAddCustom: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            if (onAddCustom != null) {
                IconButton(onClick = onAddCustom, modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.Cyan.copy(alpha = 0.1f))) {
                    Icon(Icons.Default.Add, null, tint = Color.Cyan, modifier = Modifier.size(16.dp))
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items(items) { item ->
                SelectionItem(
                    label = when(item) {
                        is CarBackground -> item.description
                        is CarFloor -> item.description
                        else -> item.toString()
                    },
                    isSelected = item == selected,
                    onClick = { onSelect(item) }
                )
            }
            
            if (customItems.isNotEmpty()) {
                item {
                    Text("PERSONALIZADOS", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(customItems) { path ->
                    SelectionItem(
                        label = path.split("/").last(),
                        isSelected = path == selected,
                        onClick = { onSelect(path) }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) Color.Cyan.copy(alpha = 0.1f) else Color(0xFF222222),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = if (isSelected) Color.Cyan else Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(Icons.Default.Check, null, tint = Color.Cyan, modifier = Modifier.size(20.dp))
            }
        }
    }
}
