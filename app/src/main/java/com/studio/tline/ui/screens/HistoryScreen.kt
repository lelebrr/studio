package com.studio.tline.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.platform.LocalContext
import com.studio.tline.data.models.EditedCar
import com.studio.tline.ui.viewmodels.EditorViewModel
import java.io.File

@Composable
fun HistoryScreen(
    viewModel: EditorViewModel,
    onItemClick: (EditedCar) -> Unit
) {
    val context = LocalContext.current
    val history: List<EditedCar> by viewModel.historyList(context).collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        if (history.isEmpty()) {
            EmptyHistory()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = history.size,
                    key = { index -> history[index].id }
                ) { index ->
                    val car = history[index]
                    HistoryItem(car = car, onClick = { onItemClick(car) })
                }
            }
        }
    }
}

@Composable
fun HistoryItem(car: EditedCar, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(File(car.resultPhotoPath)),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(car.backgroundName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(car.timestamp),
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun EmptyHistory() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.DirectionsCar,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("NENHUM CARRO EDITADO AINDA", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
