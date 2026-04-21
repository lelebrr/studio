package com.studio.tline.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TopAppBarDefaults
import com.studio.tline.ui.viewmodels.EditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    editorViewModel: EditorViewModel,
    onNavigateToEditor: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("T-LINE CAR STUDIO", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                        Text("VERSÃO FINAL B2B", color = Color.Cyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações", tint = Color.Gray)
                    }
                },
                // Corrigido: usando topAppBarColors em vez de centerAlignedTopAppBarColors depreciado
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F0F0F), titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F0F))
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // BOTÃO PRINCIPAL GIGANTE ÚNICO
            Button(
                onClick = onNavigateToEditor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddAPhoto, 
                        contentDescription = null, 
                        tint = Color.Black, 
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "NOVA FOTO DE CARRO", 
                        color = Color.Black, 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "CLIQUE PARA INICIAR ESTÚDIO IA", 
                        color = Color.DarkGray, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))
            
            Text(
                "T-Line Studio Pro Gold Master v1.0.0",
                color = Color.DarkGray.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}