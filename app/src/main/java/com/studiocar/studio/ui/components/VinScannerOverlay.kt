package com.studiocar.studio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Overlay de scan VIN com guia animado e resultado.
 */
@Composable
fun VinScannerOverlay(
    isScanning: Boolean,
    scannedVin: String?,
    decodedInfo: VinDecodedInfo?,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Overlay escurecido
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = Color.Black.copy(alpha = 0.6f))

            // Janela de scan transparente
            val scanWidth = size.width * 0.85f
            val scanHeight = size.height * 0.08f
            val scanX = (size.width - scanWidth) / 2
            val scanY = size.height * 0.38f

            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(scanX, scanY),
                size = Size(scanWidth, scanHeight),
                cornerRadius = CornerRadius(12.dp.toPx()),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )
        }

        // Borda animada do scanner
        if (isScanning) {
            ScannerFrame()
        }

        // Texto de instrução
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = Color.Cyan,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "SCANNER VIN",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Posicione o código VIN dentro do quadro",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        // Botão fechar
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.White)
        }

        // Resultado do scan
        if (scannedVin != null) {
            VinResultCard(
                vin = scannedVin,
                info = decodedInfo,
                onConfirm = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .padding(bottom = 48.dp)
            )
        }
    }
}

@Composable
private fun ScannerFrame() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanLine")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLineAnim"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val scanWidth = size.width * 0.85f
        val scanHeight = size.height * 0.08f
        val scanX = (size.width - scanWidth) / 2
        val scanY = size.height * 0.38f

        // Borda do scanner
        drawRoundRect(
            color = Color.Cyan,
            topLeft = Offset(scanX, scanY),
            size = Size(scanWidth, scanHeight),
            cornerRadius = CornerRadius(12.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // Linha de scan animada
        val lineY = scanY + (scanHeight * scanLineY)
        drawLine(
            color = Color.Cyan.copy(alpha = 0.8f),
            start = Offset(scanX + 8.dp.toPx(), lineY),
            end = Offset(scanX + scanWidth - 8.dp.toPx(), lineY),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
private fun VinResultCard(
    vin: String,
    info: VinDecodedInfo?,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("VIN DETECTADO", color = Color.Cyan, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                vin,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            if (info != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    VinInfoItem("Marca", info.make)
                    VinInfoItem("Modelo", info.model)
                    VinInfoItem("Ano", info.year)
                }
                if (info.bodyClass.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${info.bodyClass} • ${info.driveType}", color = Color.Gray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONFIRMAR E USAR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun VinInfoItem(label: String, value: String) {
    Column {
        Text(label.uppercase(), color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Dados decodificados do VIN via NHTSA API.
 */
data class VinDecodedInfo(
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val bodyClass: String = "",
    val driveType: String = "",
    val fuelType: String = "",
    val plantCountry: String = ""
)
