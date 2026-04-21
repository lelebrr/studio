package com.studiocar.studio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History

@Composable
fun TutorialScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        TutorialPageData(
            title = "Capture Profissional",
            description = "Use o grid para alinhar o carro perfeitamente. O sistema Elite 2026 cuida do resto.",
            icon = Icons.Default.CameraAlt,
            color = Color(0xFF4FACFE)
        ),
        TutorialPageData(
            title = "Inteligência Híbrida",
            description = "Nossa IA remove o fundo e cria um showroom 4K em segundos usando Gemini e Flux.",
            icon = Icons.Default.AutoAwesome,
            color = Color(0xFF00F2FE)
        ),
        TutorialPageData(
            title = "Venda Mais Rápido",
            description = "Compartilhe fotos de alta qualidade diretamente no WhatsApp e encante seus clientes.",
            icon = Icons.Default.History,
            color = Color(0xFF8E2DE2)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            TutorialPage(data = pages[page])
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val color = if (pagerState.currentPage == index) Color.Cyan else Color.Gray.copy(alpha = 0.5f)
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinished()
                    }
                },
                modifier = Modifier.align(Alignment.CenterEnd).height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "COMEÇAR" else "PRÓXIMO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TutorialPage(data: TutorialPageData) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(data.color.copy(alpha = 0.3f), Color.Transparent))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = data.color
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = data.title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = data.description,
            color = Color.Gray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

data class TutorialPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)



