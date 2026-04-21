package com.studiocar.studio.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.studiocar.studio.ui.components.StudioAppBar
import com.studiocar.studio.ui.theme.*
import com.studiocar.studio.ui.viewmodels.EditorViewModel
import com.studiocar.studio.data.models.EditedCar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: EditorViewModel = viewModel(),
    onNavigateToCamera: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val recentCaptures by viewModel.historyList(context).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            StudioAppBar(
                title = "StudioCar",
                subtitle = "Professional Studio AI",
                actions = {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Configurações", tint = Color.White)
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Main Capture Card (Capture Hub Hero)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .clickable(onClick = onNavigateToCamera),
                color = StudioSurfaceVariant,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Visual interest: Gradient and background hints
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        StudioMetallicBlue.copy(alpha = 0.15f)
                                    )
                                )
                            )
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(100.dp).premiumEntrance(),
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 20.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.AddAPhoto, 
                                    contentDescription = null, 
                                    tint = StudioBlack, 
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Text(
                            "NOVA CAPTURA", 
                            style = MaterialTheme.typography.displayLarge,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            "TECNOLOGIA STUDIO AI ATIVA", 
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioCyan,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Recent Captures Section
            if (recentCaptures.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "CAPTURES RECENTES",
                            color = Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "VER TUDO",
                            color = StudioCyan,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onNavigateToHistory)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(end = 24.dp)
                    ) {
                        items(recentCaptures.take(5)) { car ->
                            RecentCaptureCard(car = car) {
                                // Navigate to Editor with this car
                            }
                        }
                    }
                }
            }

            // Secondary Options Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardSmallCard(
                    modifier = Modifier.weight(1f),
                    title = "BIBLIOTECA",
                    icon = Icons.Default.Collections,
                    onClick = onNavigateToHistory
                )
                DashboardSmallCard(
                    modifier = Modifier.weight(1f),
                    title = "AJUSTES PRO",
                    icon = Icons.Default.Tune,
                    onClick = onNavigateToSettings
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Branding Footer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "STUDIOCAR ELITE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.2f),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    "PROFESSIONAL AUTOMOTIVE SUITE",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.1f),
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun RecentCaptureCard(car: EditedCar, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = StudioSurfaceVariant,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            AsyncImage(
                model = car.resultPhotoPath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = car.carModel ?: "Veículo",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = car.carBrand ?: "Unknown",
            color = Color.Gray,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun DashboardSmallCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        color = StudioSurfaceVariant,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title, 
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
