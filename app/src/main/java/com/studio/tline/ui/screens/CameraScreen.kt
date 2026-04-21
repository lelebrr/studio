package com.studio.tline.ui.screens

import android.graphics.Bitmap
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.studio.tline.ui.viewmodels.EditorViewModel
import timber.log.Timber
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    viewModel: EditorViewModel,
    onPhotoCaptured: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    val options by viewModel.options.collectAsState()
    
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var showGuidelines by remember { mutableStateOf(true) }
    var isHdrEnabled by remember { mutableStateOf(false) }

    // Removido setTargetResolution depreciado - usa resolução máxima do dispositivo
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(flashMode)
            .build()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply { scaleType = PreviewView.ScaleType.FILL_CENTER }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val extensionsManagerFuture = ExtensionsManager.getInstanceAsync(context, cameraProvider)
                    
                    extensionsManagerFuture.addListener({
                        val extensionsManager = extensionsManagerFuture.get()
                        var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        
                        if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.HDR)) {
                            cameraSelector = extensionsManager.getExtensionEnabledCameraSelector(cameraSelector, ExtensionMode.HDR)
                            isHdrEnabled = true
                        }

                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                        } catch (e: Exception) { Timber.e(e, "Erro na câmera") }
                    }, ContextCompat.getMainExecutor(context))
                }, ContextCompat.getMainExecutor(context))
            }
        )

        if (showGuidelines) CarSilhouetteOverlay()

        // Overlay do Modo Concessionária
        AnimatedVisibility(
            visible = options.isDealershipMode,
            enter = fadeIn() + slideInVertically(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
        ) {
            Surface(
                color = Color.Cyan.copy(alpha = 0.9f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MODO CONCESSIONÁRIA ATIVO", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Top Controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(top = 24.dp).align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { 
                flashMode = if(flashMode == ImageCapture.FLASH_MODE_OFF) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                imageCapture.flashMode = flashMode
            }) {
                Icon(if(flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff, null, tint = Color.White)
            }

            // Toggle Profissional
            Surface(
                onClick = { viewModel.toggleDealershipMode(!options.isDealershipMode) },
                color = if (options.isDealershipMode) Color.Cyan else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Icon(Icons.Default.Storefront, null, tint = if(options.isDealershipMode) Color.Black else Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if(options.isDealershipMode) "PROFESSIONAL" else "STANDARD", color = if(options.isDealershipMode) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            IconButton(onClick = { showGuidelines = !showGuidelines }) {
                Icon(if (showGuidelines) Icons.Default.Grid4x4 else Icons.Default.GridOff, null, tint = if (showGuidelines) Color.Cyan else Color.White)
            }
        }

        // Capture Button
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp).align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FOCO NO VEÍCULO PARA RESULTADO 4K", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                modifier = Modifier.size(84.dp).clip(CircleShape).border(4.dp, Color.White, CircleShape).clickable {
                    imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = image.toBitmap()
                            viewModel.setOriginalImage(bitmap)
                            image.close()
                            onPhotoCaptured()
                        }
                        override fun onError(ex: ImageCaptureException) { Timber.e(ex, "Erro na captura") }
                    })
                },
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(if(options.isDealershipMode) Color.Cyan else Color.White))
                }
            }
        }
    }

    DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }
}

@Composable
fun CarSilhouetteOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.65f)
            lineTo(w * 0.20f, h * 0.58f)
            lineTo(w * 0.40f, h * 0.55f)
            lineTo(w * 0.50f, h * 0.45f)
            lineTo(w * 0.70f, h * 0.45f)
            lineTo(w * 0.80f, h * 0.58f)
            lineTo(w * 0.90f, h * 0.60f)
            lineTo(w * 0.90f, h * 0.75f)
            lineTo(w * 0.15f, h * 0.75f)
            close()
        }
        drawPath(path = path, color = Color.White.copy(alpha = 0.2f), style = Stroke(width = 1.dp.toPx()))
    }
}