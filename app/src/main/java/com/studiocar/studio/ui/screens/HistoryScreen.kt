package com.studiocar.studio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.studiocar.studio.data.models.EditedCar
import com.studiocar.studio.ui.viewmodels.EditorViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * HistoryScreen V2.0 - StudioCar Elite Sales Records.
 * Displays processed cars with metadata and 360 tour access.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onNavigateToTour: () -> Unit,
    viewModel: EditorViewModel
) {
    val context = LocalContext.current
    val history by viewModel.historyList(context).collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    val filteredHistory = history.filter { car ->
        car.carModel?.contains(searchQuery, ignoreCase = true) == true || 
        car.carBrand?.contains(searchQuery, ignoreCase = true) == true ||
        car.vinCode?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0F0F0F))) {
                CenterAlignedTopAppBar(
                    title = { Text("HISTÓRICO ELITE", fontSize = 14.sp, fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToTour) { Icon(Icons.Default.ScreenRotation, null, tint = Color.Cyan) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
                )
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por modelo, marca ou VIN", fontSize = 12.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        focusedBorderColor = Color.Cyan,
                        focusedContainerColor = Color(0xFF151515),
                        unfocusedContainerColor = Color(0xFF151515)
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (filteredHistory.isEmpty()) {
                EmptyHistory()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredHistory, key = { car -> car.id }) { car ->
                        HistoryEliteItem(car = car, onClick = { /* Detalhes */ })
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryEliteItem(car: EditedCar, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(File(car.resultPhotoPath)),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentScale = ContentScale.Crop
                )
                
                // Badge de IA
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.BottomStart),
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "STUDIO PRO",
                        color = Color.Cyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "${car.carBrand ?: ""} ${car.carModel ?: "Veículo"}".trim(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(car.vendorName ?: "Sistema", color = Color.Gray, fontSize = 9.sp)
                }
                
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()).format(car.timestamp),
                    color = Color.DarkGray,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
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
        Icon(Icons.Default.History, null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("NENHUM REGISTRO ENCONTRADO", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
