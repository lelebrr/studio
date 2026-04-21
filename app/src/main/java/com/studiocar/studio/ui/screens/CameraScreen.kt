package com.studiocar.studio.ui.screens

import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import com.studiocar.studio.ui.theme.StudioCarAnimations
import com.studiocar.studio.ui.theme.premiumEntrance
import com.studiocar.studio.ui.theme.StudioCyan
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.CaptureRequestOptions
import android.hardware.camera2.CaptureRequest
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.studiocar.studio.data.models.PhotoAngle
import com.studiocar.studio.ui.components.*
import com.studiocar.studio.data.models.*
import com.studiocar.studio.ui.viewmodels.EditorViewModel
import com.studiocar.studio.utils.SettingsManager
import timber.log.Timber
import java.util.concurrent.Executors
import com.studiocar.studio.utils.CarFramingGuide
import com.studiocar.studio.ui.components.HorizonLine
import com.studiocar.studio.ui.components.FramingFeedbackIndicator
import com.studiocar.studio.ui.components.LightbulbButton
import com.studiocar.studio.ui.components.CarTipsPanel
import com.studiocar.studio.ui.components.ProCameraSettingsPanel
import android.content.Context
import android.graphics.Color as AndroidColor
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.compose.ui.draw.blur
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.res.Configuration
import com.studiocar.studio.camera.CameraOrientationManager
import com.studiocar.studio.ui.components.OrientationWarningOverlay
import com.studiocar.studio.ui.components.OrientationSuccessToast
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.studiocar.studio.ai.VehicleTypeDetector
import com.studiocar.studio.utils.VehicleSilhouetteManager
import kotlin.math.abs

@OptIn(
    ExperimentalMaterial3Api::class,
    androidx.camera.camera2.interop.ExperimentalCamera2Interop::class,
    androidx.camera.core.ExperimentalGetImage::class
)
@Composable
fun CameraScreen(
    viewModel: EditorViewModel = viewModel(),
    onNavigateToEditor: () -> Unit,
    onNavigateToBatchEditor: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val haptic = LocalHapticFeedback.current
    
    val options by viewModel.options.collectAsState()
    val isScanningVin by viewModel.isScanningVin.collectAsState()
    val scannedVin by viewModel.scannedVin.collectAsState()
    val vinInfo by viewModel.vinInfo.collectAsState()
    val batchImages by viewModel.batchImages.collectAsState()
    
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var isNightModeActive by remember { mutableStateOf(false) }
    var camera: Camera? by remember { mutableStateOf(null) }
    var showTips by remember { mutableStateOf(false) }
    
    val settingsManager = remember { SettingsManager(context) }
    val smartFramingEnabled by settingsManager.smartFramingEnabled.collectAsState(initial = true)
    val advancedStabilization by settingsManager.advancedStabilization.collectAsState(initial = true)
    val carTypeStr by settingsManager.preferredCarType.collectAsState(initial = "SEDAN")
    val carType = if (carTypeStr == "SUV") CarFramingGuide.CarType.SUV else CarFramingGuide.CarType.SEDAN

    val detectedVehicleType by viewModel.detectedVehicleType.collectAsState()
    val isCameraStable by viewModel.isCameraStable.collectAsState()
    val vehicleDetector = remember { VehicleTypeDetector() }

    val proSettings by viewModel.cameraSettings.collectAsState()
    val histogramData by viewModel.histogramData.collectAsState()
    var showProSettings by remember { mutableStateOf(false) }
    var timerCountdown by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    
    var framingResult by remember { mutableStateOf(CarFramingGuide.AnalysisResult(CarFramingGuide.FramingStatus.NOT_FOUND, "")) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var focusVisibility by remember { mutableStateOf(false) }

    // Orientation Management
    val orientationManager = remember { CameraOrientationManager(context) }
    val currentOrientation = orientationManager.rememberOrientation()
    var userIgnoredOrientationWarning by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf(false) }
    
    val isLandscapeRecommended = orientationManager.isLandscapeRecommended(options)
    val shouldShowWarning = orientationManager.shouldShowRotationWarning(options, currentOrientation) && !userIgnoredOrientationWarning

    // Reset ignore flag when angle changes
    LaunchedEffect(options.currentAngle, options.photoMode) {
        userIgnoredOrientationWarning = false
    }

    // Success Toast Logic
    LaunchedEffect(currentOrientation) {
        if (isLandscapeRecommended && currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            showSuccessToast = true
            delay(3000)
            showSuccessToast = false
        }
    }

    // Sensor de Estabilidade
    DisposableEffect(advancedStabilization) {
        if (advancedStabilization) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            
            val listener = object : SensorEventListener {
                private var lastX = 0f
                private var lastY = 0f
                private var lastZ = 0f
                private var lastUpdate: Long = 0

                override fun onSensorChanged(event: SensorEvent) {
                    val curTime = System.currentTimeMillis()
                    if ((curTime - lastUpdate) > 100) {
                        val diffTime = (curTime - lastUpdate)
                        lastUpdate = curTime

                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
                        viewModel.updateCameraStability(speed < 150) // Threshold for stability
                        
                        lastX = x
                        lastY = y
                        lastZ = z
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            onDispose { sensorManager.unregisterListener(listener) }
        } else {
            viewModel.updateCameraStability(false)
            onDispose {}
        }
    }

    val density = LocalDensity.current
    fun Float.toComposeDp() = with(density) { this@toComposeDp.toDp() }

    // CameraX Initialization
    @Suppress("DEPRECATION")
    val imageCapture = remember<ImageCapture>(proSettings.resolution, proSettings.quality) {
        val builder = ImageCapture.Builder()
            .setCaptureMode(if(proSettings.quality == CameraQuality.MAXIMUM) ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY else ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .setTargetResolution(Size(proSettings.resolution.width, proSettings.resolution.height))
        
        // Aplicar Configurações Pro via Camera2Interop
        val extender = Camera2Interop.Extender(builder)
        if (proSettings.iso != 100 || proSettings.shutterSpeedNanos != 16_666_666L) {
            extender.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)
            extender.setCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY, proSettings.iso)
            extender.setCaptureRequestOption(CaptureRequest.SENSOR_EXPOSURE_TIME, proSettings.shutterSpeedNanos)
        }
        
        // White Balance (#PRO)
        if (proSettings.whiteBalanceTemp != 5500) {
            extender.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_OFF)
            extender.setCaptureRequestOption(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX)
            val gains = calculateWbGains(proSettings.whiteBalanceTemp)
            extender.setCaptureRequestOption(CaptureRequest.COLOR_CORRECTION_GAINS, android.hardware.camera2.params.RggbChannelVector(gains.first, 1.0f, 1.0f, gains.second))
        }

        // Estabilização Avançada
        if (advancedStabilization) {
            extender.setCaptureRequestOption(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON)
            extender.setCaptureRequestOption(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON)
        }

        builder.build()
    }

    val barcodeScanner = remember { BarcodeScanning.getClient() }

    // Analysis for Night Mode (#22) and VIN Decoding (#2)
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        // 1. Detecção de Luminosidade (Night Mode #22)
                        val plane = mediaImage.planes[0]
                        val buffer = plane.buffer
                        val data = ByteArray(buffer.remaining())
                        buffer.get(data)
                        var sum = 0L
                        for (i in data.indices step 64) sum += data[i].toInt() and 0xFF
                        val avgLuma = sum / (data.size / 64)
                        isNightModeActive = avgLuma < 50
                        
                        // 2. Scan de VIN se ativo (#2)
                        if (isScanningVin && scannedVin == null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        val value = barcode.rawValue
                                        if (value != null && (value.length == 17)) {
                                            viewModel.onVinDetected(value)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }
                                    }
                                }
                        }

                        // 3. Guia de Enquadramento Inteligente (#24)
                        if (smartFramingEnabled && options.photoMode == PhotoMode.EXTERIOR) {
                            val carBox = if (avgLuma > 20) {
                                android.graphics.RectF(0.2f, 0.4f, 0.8f, 0.8f)
                            } else null
                            
                            framingResult = CarFramingGuide.analyze(carBox, carType)
                        }

                        // 4. Histograma (#PRO)
                        if (proSettings.showHistogram) {
                            val plane = mediaImage.planes[0]
                            val buffer = plane.buffer
                            val data = ByteArray(buffer.remaining())
                            buffer.get(data)
                            val hist = FloatArray(256)
                            for (i in data.indices step 8) {
                                val luma = data[i].toInt() and 0xFF
                                hist[luma]++
                            }
                            viewModel.updateHistogram(hist)
                        } else {
                            viewModel.updateHistogram(null)
                        }

                        // 5. Detecção de Tipo de Veículo
                        if (!isScanningVin && options.photoMode == PhotoMode.EXTERIOR) {
                            vehicleDetector.processImage(imageProxy) { type ->
                                viewModel.updateDetectedVehicleType(type)
                            }
                        }
                    }
                    imageProxy.close()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        focusPoint = offset
                        focusVisibility = true
                        val factory = SurfaceOrientedMeteringPointFactory(size.width.toFloat(), size.height.toFloat())
                        val point = factory.createPoint(offset.x, offset.y)
                        val action = FocusMeteringAction.Builder(point).build()
                        camera?.cameraControl?.startFocusAndMetering(action)
                        
                        // Esconder indicador de foco após 2s
                        scope.launch {
                            delay(2000)
                            focusVisibility = false
                        }
                    }
                },
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val extensionsManagerFuture = ExtensionsManager.getInstanceAsync(context, cameraProvider)
                    
                    extensionsManagerFuture.addListener({
                        val extensionsManager = extensionsManagerFuture.get()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        
                        // HDR Auto-Selection (#22)
                        val finalSelector = if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.HDR)) {
                            extensionsManager.getExtensionEnabledCameraSelector(cameraSelector, ExtensionMode.HDR)
                        } else cameraSelector

                        val previewBuilder = Preview.Builder()
                        
                        // Aplicar Configurações Pro ao Preview também (#PRO)
                        val previewExtender = Camera2Interop.Extender(previewBuilder)
                        if (proSettings.iso != 100 || proSettings.shutterSpeedNanos != 16_666_666L) {
                            previewExtender.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)
                            previewExtender.setCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY, proSettings.iso)
                            previewExtender.setCaptureRequestOption(CaptureRequest.SENSOR_EXPOSURE_TIME, proSettings.shutterSpeedNanos)
                        }
                        
                        if (proSettings.whiteBalanceTemp != 5500) {
                            previewExtender.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)
                            previewExtender.setCaptureRequestOption(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX)
                            val gains = calculateWbGains(proSettings.whiteBalanceTemp)
                            previewExtender.setCaptureRequestOption(CaptureRequest.COLOR_CORRECTION_GAINS, android.hardware.camera2.params.RggbChannelVector(gains.first, 1.0f, 1.0f, gains.second))
                        }

                        // Estabilização Avançada no Preview
                        if (advancedStabilization) {
                            previewExtender.setCaptureRequestOption(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON)
                            previewExtender.setCaptureRequestOption(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON)
                        }

                        // Aplicar EV via CameraControl
                        camera?.cameraControl?.setExposureCompensationIndex(
                            (proSettings.exposureCompensation * (camera?.cameraInfo?.exposureState?.exposureCompensationRange?.upper ?: 12)).toInt()
                        )

                        val preview = previewBuilder.build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                        try {
                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner, 
                                finalSelector, 
                                preview, 
                                imageCapture, 
                                imageAnalysis
                            )
                        } catch (e: Exception) { Timber.e(e, "Erro na câmera") }
                    }, ContextCompat.getMainExecutor(context))
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Grade de Enquadramento (#PRO)
        if (proSettings.gridType != GridType.NONE) {
            CameraGridOverlay(type = proSettings.gridType)
        }

        // Guia de Enquadramento AI (#1)
        if (!isScanningVin && options.photoMode == PhotoMode.EXTERIOR) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                AngleGuideOverlay(
                    currentAngle = options.currentAngle,
                    onAngleSelected = { viewModel.updateOptions(options.copy(currentAngle = it)) },
                    guidanceText = if (isNightModeActive) "MODO NOTURNO ATIVO: Mantenha firme" else "Alinhe o carro com a silhueta"
                )
            }

            // Overlays Inteligentes (#24)
            if (smartFramingEnabled) {
                HorizonLine()
                
                // Silhueta do Veículo
                VehicleSilhouetteManager.SilhouetteOverlay(
                    type = detectedVehicleType,
                    angle = options.currentAngle
                )
                
                // Feedback de enquadramento animado
                AnimatedVisibility(
                    visible = framingResult.status != CarFramingGuide.FramingStatus.NOT_FOUND,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.Center).padding(top = 200.dp)
                ) {
                    FramingFeedbackIndicator(result = framingResult)
                }
            }
        }

        // Seletor de Modo (#12)
        if (!isScanningVin) {
            PhotoModeSelector(
                currentMode = options.photoMode,
                onModeSelected = { viewModel.updateOptions(options.copy(photoMode = it)) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp)
            )
        }

        // Overlay de VIN (#2)
        if (isScanningVin) {
            VinScannerOverlay(
                isScanning = scannedVin == null,
                scannedVin = scannedVin,
                decodedInfo = vinInfo.let { 
                    com.studiocar.studio.ui.components.VinDecodedInfo(it.carBrand ?: "", it.carModel ?: "", it.carYear ?: "") 
                },
                onClose = { viewModel.setScanningVin(false) },
                onConfirm = { viewModel.confirmVin() }
            )
        }

        // Top Controls
        CameraTopBar(
            options = options,
            flashMode = flashMode,
            isNightMode = isNightModeActive,
            isProActive = proSettings.iso != 100 || proSettings.shutterSpeedNanos != 16_666_666L,
            onSettings = { showProSettings = true },
            onAiSettings = { viewModel.setShowAiProvidersSheet(true) },
            onFlashToggle = { 
                flashMode = if(flashMode == ImageCapture.FLASH_MODE_OFF) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                imageCapture.flashMode = flashMode
            },
            onVinToggle = { viewModel.setScanningVin(true) },
            onDealershipToggle = { viewModel.toggleDealershipMode(!options.isDealershipMode) },
            onShowTips = { showTips = true }
        )

        // Histograma Overlay (#PRO)
        if (proSettings.showHistogram) {
            HistogramOverlay(
                data = histogramData,
                modifier = Modifier.align(Alignment.TopStart).padding(top = 100.dp, start = 16.dp)
            )
        }

        // Indicador de Estabilidade
        if (advancedStabilization) {
            AnimatedVisibility(
                visible = isCameraStable,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp)
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Estável", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Timer Countdown
        if (timerCountdown > 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    timerCountdown.toString(),
                    color = Color.White,
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.blur(if(timerCountdown == 0) 10.dp else 0.dp)
                )
            }
        }

        // Painel Pro (#PRO)
        if (showProSettings) {
            ProCameraSettingsPanel(
                settings = proSettings,
                onSettingsChanged = { viewModel.updateCameraSettings(context, it) },
                onDismiss = { showProSettings = false }
            )
        }

        // Indicador de Foco (#PRO)
        if (focusVisibility && focusPoint != null) {
            Box(
                modifier = Modifier
                    .offset(x = focusPoint!!.x.toComposeDp() - 30.dp, y = focusPoint!!.y.toComposeDp() - 30.dp)
                    .size(60.dp)
                    .border(1.dp, Color.Cyan, CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = focusPoint!!.x.toComposeDp() - 2.dp, y = focusPoint!!.y.toComposeDp() - 2.dp)
                    .size(4.dp)
                    .background(Color.Cyan, CircleShape)
            )
        }

        // Modal de Dicas (#24)
        if (showTips) {
            CarTipsPanel(onDismiss = { showTips = false })
        }

        // AI Providers Sheet (#2026)
        val showAiSheet by viewModel.showAiProvidersSheet.collectAsState()
        if (showAiSheet) {
            AIProvidersBottomSheet(
                onDismiss = { viewModel.setShowAiProvidersSheet(false) },
                settingsManager = settingsManager,
                aiProviderManager = viewModel.getAiProviderManager(context)
            )
        }

        // Capture Controls
        if (!isScanningVin) {
            CameraBottomBar(
                options = options,
                batchCount = batchImages.size,
                onCapture = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    
                    scope.launch {
                        if (proSettings.timerSeconds > 0) {
                            timerCountdown = proSettings.timerSeconds
                            while (timerCountdown > 0) {
                                delay(1000)
                                timerCountdown--
                            }
                        }
                        
                        imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = image.toBitmap()
                                if (options.batchMode) {
                                    viewModel.addToBatch(bitmap)
                                } else {
                                    viewModel.setOriginalImage(bitmap)
                                    onNavigateToEditor()
                                }
                                image.close()
                            }
                            override fun onError(ex: ImageCaptureException) { Timber.e(ex, "Erro na captura") }
                        })
                    }
                },
                onFinishBatch = onNavigateToBatchEditor,
                captureBlocked = isLandscapeRecommended && currentOrientation == Configuration.ORIENTATION_PORTRAIT && !userIgnoredOrientationWarning
            )
        }

        // Overlays de Orientação
        if (shouldShowWarning) {
            OrientationWarningOverlay(
                onDismiss = { userIgnoredOrientationWarning = true }
            )
        }

        if (showSuccessToast) {
            OrientationSuccessToast()
        }
    }

    DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }
}

@Composable
private fun CameraTopBar(
    options: com.studiocar.studio.data.models.EditOptions,
    flashMode: Int,
    isNightMode: Boolean,
    onSettings: () -> Unit,
    onAiSettings: () -> Unit,
    onFlashToggle: () -> Unit,
    onVinToggle: () -> Unit,
    onDealershipToggle: () -> Unit,
    onShowTips: () -> Unit,
    isProActive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            IconButton(onClick = onSettings) {
                Icon(Icons.Default.Tune, null, tint = if(isProActive) Color.Cyan else Color.White)
            }
            if (isProActive) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Cyan).align(Alignment.TopEnd))
            }
        }

        IconButton(onClick = onAiSettings) {
            Icon(Icons.Default.Settings, null, tint = Color.Cyan)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // VIN Button (#2)
            IconButton(onClick = onVinToggle) {
                Icon(Icons.Default.QrCodeScanner, null, tint = Color.Cyan)
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Night Mode indicator (#22)
            if (isNightMode) {
                Icon(Icons.Default.Brightness3, null, tint = Color.Yellow, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            // Dealership Toggle
            Surface(
                onClick = onDealershipToggle,
                color = if (options.isDealershipMode) Color.Cyan else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text(if(options.isDealershipMode) "B2B ELITE" else "FREE", color = if(options.isDealershipMode) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Lightbulb Tips (#24)
            LightbulbButton(onClick = onShowTips)
        }

        IconButton(onClick = onFlashToggle) {
            Icon(if(flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff, null, tint = Color.White)
        }
    }
}

@Composable
private fun CameraBottomBar(
    options: com.studiocar.studio.data.models.EditOptions,
    batchCount: Int,
    onCapture: () -> Unit,
    onFinishBatch: () -> Unit,
    captureBlocked: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = StudioCarAnimations.ResponsiveSpring,
        label = "capture_button_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp)
            .premiumEntrance(delay = 300), // Entrada elegante
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (options.batchMode) {
            Text(
                "LOTE: $batchCount / ${options.batchCount}",
                color = StudioCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            // Capture Button com Animação Professional
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { if (!captureBlocked) onCapture() }
                    )
                    .then(if (captureBlocked) Modifier.graphicsLayer { alpha = 0.5f } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(if (options.isDealershipMode) StudioCyan else Color.White)
                        .then(if (isPressed) Modifier.background(Color.Gray.copy(alpha = 0.5f)) else Modifier)
                )
            }

            // Finish Batch Button com Animação
            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp)) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = options.batchMode && batchCount > 0,
                    enter = scaleIn(animationSpec = StudioCarAnimations.NaturalSpring) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    IconButton(
                        onClick = onFinishBatch,
                        modifier = Modifier.background(StudioCyan, CircleShape)
                    ) {
                        Icon(Icons.Default.Check, null, tint = Color.Black)
                    }
                }
            }
        }
    }
}

/**
 * Calcula ganhos R e B para compensar Temperatura Kelvin (#PRO)
 */
private fun calculateWbGains(kelvin: Int): Pair<Float, Float> {
    return if (kelvin < 5000) {
        val r = 1.0f + (5000 - kelvin) / 2500f
        val b = 2.0f - (5000 - kelvin) / 2500f
        r to b
    } else {
        val r = 2.0f - (kelvin - 5000) / 5000f
        val b = 1.0f + (kelvin - 5000) / 5000f
        r to b
    }
}
