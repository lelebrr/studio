package com.studio.tline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        topBar = {
            TopAppBar(
                title = { Text("Sobre o sistema", fontSize = 16.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.Info, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "T-Line Car Studio Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text("Versão 1.7.0 — Gold Master Elite (2026)", color = Color.Gray, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp)) {
                    AboutRow("Segmentation", "MediaPipe Vision 2026")
                    AboutRow("Refraction IA", "Gemini 3 Pro")
                    AboutRow("DSLR Engine", "FLUX.1.1 Pro Ultra")
                    AboutRow("Output", "4K Ultra HD Premium")
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                "Desenvolvido com tecnologia de ponta para [Nome da Concessionária].",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Concordar e voltar", color = Color.Black, fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("© 2026 T-Line Studio. Todos os direitos reservados.", color = Color.DarkGray, fontSize = 10.sp)
        }
    }
}

@Composable
fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
