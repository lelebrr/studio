package com.studiocar.studio.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.utils.CarFramingGuide

@Composable
fun LightbulbButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
    ) {
        Icon(
            Icons.Default.Lightbulb, 
            contentDescription = "Dicas",
            tint = Color.Yellow,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun HorizonLine() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val y = size.height * 0.6f
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun FramingFeedbackIndicator(result: CarFramingGuide.AnalysisResult) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(result.status) {
        if (result.status != CarFramingGuide.FramingStatus.NOT_FOUND) {
            visible = true
            if (result.status == CarFramingGuide.FramingStatus.PERFECT) {
                kotlinx.coroutines.delay(2000)
                visible = false
            }
        } else {
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(24.dp)
            ) {
                if (result.status == CarFramingGuide.FramingStatus.PERFECT) {
                    Icon(
                        Icons.Default.ThumbUp, 
                        null, 
                        tint = Color.Green, 
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Text(
                        result.arrow ?: "", 
                        fontSize = 72.sp, 
                        color = Color.Cyan, 
                        fontWeight = FontWeight.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    result.message,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTipsPanel(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF151515),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "DICAS DE ÂNGULOS PROFISSIONAIS",
                color = Color.Cyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            AngleTipItem("1. Front ¾ (Hero Shot)", "O ângulo mais importante. 45° em relação à frente. Camera na altura do farol.", true)
            AngleTipItem("2. Rear ¾", "Dá profundidade ao design traseiro. Mesma lógica do Front ¾.", false)
            AngleTipItem("3. Perfil Lateral", "Mostra o comprimento total. Camera perfeitamente paralela ao carro.", false)
            AngleTipItem("4. Frontal Direto", "Imponência. Alinhe o logo da marca no centro.", false)
            AngleTipItem("5. Traseira Reta", "Simetria é tudo aqui. Mostre as lanternas e escape.", false)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ENTENDI", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AngleTipItem(title: String, desc: String, isHero: Boolean) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (isHero) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = Color.Cyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("GOLD", color = Color.Cyan, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
        Text(desc, color = Color.Gray, fontSize = 12.sp)
    }
}
