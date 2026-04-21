package com.studio.tline.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.studio.tline.ui.viewmodels.EditorViewModel
import com.studio.tline.utils.DebugDataManager
import com.studio.tline.utils.ImageSaveHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    onBack: () -> Unit,
    viewModel: EditorViewModel
) {
    val stats by DebugDataManager.lastExecutionStats.collectAsState()
    val original by viewModel.originalBitmap.collectAsState()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = ImageSaveHelper.getBitmapFromUri(context, it)
            bitmap?.let { b -> viewModel.setOriginalImage(b) }
        }
    }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        topBar = {
            TopAppBar(
                title = { Text("DIAGNOSTIC LAB V9", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                        Icon(Icons.Default.PhotoLibrary, null, tint = Color.Cyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seção 1: Comparador 4-Grid
            item {
                Text("PIPELINE COMPARATOR (GRID 4)", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TestStageBox("1. ORIGINAL", original, Modifier.weight(1f))
                        TestStageBox("2. MASK", stats?.stageMask, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TestStageBox("3. GEMINI STAGE", stats?.stageGemini, Modifier.weight(1f))
                        TestStageBox("4. FINAL (FLUX)", stats?.stageFinal, Modifier.weight(1f), isFinal = true)
                    }
                }
            }

            // Seção 2: Métricas de Qualidade Reais
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF151515)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("QUALITY ANALYTICS", color = Color.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        stats?.let { s ->
                            QualityMetricRow("Score de Nitidez", "${s.qualityScore}%", Icons.Default.PrecisionManufacturing, Color.Green)
                            QualityMetricRow("Presença de Rebarbas", "${s.burrPresenceScore}%", Icons.Default.ContentCut, if(s.burrPresenceScore > 20) Color.Red else Color.Cyan)
                            QualityMetricRow("Resolução Final", s.finalResolution, Icons.Default.SdStorage, Color.White)
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                            
                            QualityMetricRow("MediaPipe", "${s.mediaPipeTime}ms", Icons.Default.Layers, Color.Gray)
                            QualityMetricRow("Gemini Pass", "${s.geminiPass1Time}ms", Icons.Default.AutoAwesome, Color.Gray)
                            QualityMetricRow("Stable Diffusion", "${s.stableDiffusionTime}ms", Icons.Default.Blender, Color.Gray)
                            QualityMetricRow("LATÊNCIA TOTAL", "${s.totalTime}ms", Icons.Default.Timer, Color.Cyan)
                        } ?: Text("Inicie um teste para ver métricas", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            // Seção 3: Categorias de Stress Test
            item {
                Text("STRESS TEST CATEGORIES", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StressCategoryChip("Carro Preto", Icons.Default.Nightlight)
                    StressCategoryChip("Fundo Complexo", Icons.Default.Nature)
                    StressCategoryChip("Vidros/Transp.", Icons.Default.Window)
                    StressCategoryChip("Bordas/Branco", Icons.Default.BorderOuter)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TestStageBox(label: String, bitmap: Bitmap?, modifier: Modifier, isFinal: Boolean = false) {
    Box(modifier = modifier.fillMaxHeight().clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A1A))) {
        if (bitmap != null) {
            AsyncImage(model = bitmap, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Icon(Icons.Default.Image, null, modifier = Modifier.align(Alignment.Center).size(24.dp), tint = Color.DarkGray)
        }
        Text(
            label, 
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(Color.Black.copy(0.6f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
            color = if(isFinal) Color.Cyan else Color.White, 
            fontSize = 8.sp, 
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QualityMetricRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.LightGray, fontSize = 12.sp)
        }
        Text(value, color = valueColor, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun StressCategoryChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        onClick = { /* Trigger auto-test */ },
        color = Color(0xFF222222),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.1f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.Cyan, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
