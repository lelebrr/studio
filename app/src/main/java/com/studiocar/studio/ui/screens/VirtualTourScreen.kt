package com.studiocar.studio.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled._360
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.ui.viewmodels.EditorViewModel

/**
 * VirtualTourScreen (#16): Visualizador Interativo 360°.
 * Usa o lote de fotos para simular o giro do carro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualTourScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val batchImages by viewModel.batchImages.collectAsState()
    var currentIndex by remember { mutableIntStateOf(0) }
    var dragAmount by remember { mutableFloatStateOf(0f) }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TOUR VIRTUAL 360°", fontSize = 14.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (batchImages.isEmpty()) {
                EmptyTourView()
            } else {
                TourViewer(
                    images = batchImages,
                    currentIndex = currentIndex,
                    onIndexChange = { currentIndex = it },
                    dragAmount = dragAmount,
                    onDragAmountChange = { dragAmount = it }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    "DESLIZE PARA GIRAR O VEÍCULO",
                    color = Color.Cyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                
                Spacer(modifier = Modifier.height(64.dp))
                
                Button(
                    onClick = { /* Exportar como GIF/Video */ },
                    modifier = Modifier.padding(16.dp).fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Share, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("GERAR VÍDEO 360°", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun TourViewer(
    images: List<Bitmap>,
    currentIndex: Int,
    onIndexChange: (Int) -> Unit,
    dragAmount: Float,
    onDragAmountChange: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF151515))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmountX ->
                    change.consume()
                    val sensitivity = 50f
                    val newDragAmount = dragAmount + dragAmountX
                    onDragAmountChange(newDragAmount)
                    
                    val step = (newDragAmount / sensitivity).toInt()
                    if (step != 0) {
                        var nextIndex = currentIndex + step
                        while (nextIndex < 0) nextIndex += images.size
                        while (nextIndex >= images.size) nextIndex -= images.size
                        onIndexChange(nextIndex)
                        onDragAmountChange(newDragAmount % sensitivity)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = images[currentIndex].asImageBitmap(),
            contentDescription = "360 View",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            color = Color.Black.copy(alpha = 0.6f),
            shape = CircleShape
        ) {
            Icon(
                Icons.AutoMirrored.Filled._360,
                null,
                tint = Color.Cyan,
                modifier = Modifier.padding(8.dp).size(24.dp)
            )
        }
    }
}

@Composable
fun EmptyTourView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.AutoMirrored.Filled._360, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("CAPTURE UM LOTE DE FOTOS PARA INICIAR O TOUR", color = Color.Gray, fontSize = 12.sp)
    }
}
