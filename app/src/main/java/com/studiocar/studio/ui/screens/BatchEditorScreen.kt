package com.studiocar.studio.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.ui.viewmodels.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchEditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val batchImages by viewModel.batchImages.collectAsState()
    val processingStage by viewModel.processingStage.collectAsState()
    val batchProgress by viewModel.batchProgress.collectAsState() // (current, total)
    val options by viewModel.options.collectAsState()
    
    val isProcessing = processingStage == EditorViewModel.ProcessingStage.BATCH_PROCESSING

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("LOTE STUDIO PRO", fontWeight = FontWeight.Black, fontSize = 14.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isProcessing) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) 
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            BatchBottomBar(
                isProcessing = isProcessing,
                onProcess = { viewModel.processBatch(context) },
                onCancel = { viewModel.clearBatch() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Batch Info
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                color = Color(0xFF151515),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoLibrary, null, tint = Color.Cyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("${batchImages.size} FOTOS CAPTURADAS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Modo: ${if(options.isDealershipMode) "Elite Studio" else "Standard"}", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }

            // Photo Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(batchImages) { index, bitmap ->
                    BatchImageItem(
                        bitmap = bitmap,
                        isProcessing = isProcessing && index == batchProgress.first - 1,
                        isDone = isProcessing && index < batchProgress.first - 1
                    )
                }
            }
            
            // Progress Overlay
            if (isProcessing) {
                LinearProgressIndicator(
                    progress = { batchProgress.first.toFloat() / batchProgress.second.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Cyan,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text(
                    "PROCESSANDO ${batchProgress.first} DE ${batchProgress.second}",
                    color = Color.Cyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun BatchImageItem(bitmap: Bitmap, isProcessing: Boolean, isDone: Boolean) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF222222))
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        if (isDone) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CheckCircle, null, tint = Color.Cyan, modifier = Modifier.size(32.dp))
            }
        }
        
        if (isProcessing) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Cyan, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun BatchBottomBar(isProcessing: Boolean, onProcess: () -> Unit, onCancel: () -> Unit) {
    Surface(
        color = Color(0xFF121212),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                enabled = !isProcessing,
                modifier = Modifier.height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text("EXCLUIR", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = onProcess,
                enabled = !isProcessing,
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isProcessing) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                else Text("REMOVER FUNDOS EM LOTE", fontWeight = FontWeight.Black)
            }
        }
    }
}
