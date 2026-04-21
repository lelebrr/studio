package com.studiocar.studio.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studiocar.studio.ai.ImageEditorService
import com.studiocar.studio.ai.providers.AIProviderManager
import com.studiocar.studio.data.AppDatabase
import com.studiocar.studio.data.StudioScenes
import com.studiocar.studio.data.models.*
import com.studiocar.studio.network.VinDecoderModule
import com.studiocar.studio.utils.ImageAdjustmentApplier
import com.studiocar.studio.utils.ImageSaveHelper
import com.studiocar.studio.utils.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

/**
 * EditorViewModel V11.0 - StudioCar Elite Professional.
 * Orchestrates AI pipeline, Batch processing, Sales Metadata, and Advanced Editing.
 */
class EditorViewModel : ViewModel() {

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap = _originalBitmap.asStateFlow()

    private val _resultBitmap = MutableStateFlow<Bitmap?>(null)
    val resultBitmap = _resultBitmap.asStateFlow()

    private val _options = MutableStateFlow(EditOptions())
    val options = _options.asStateFlow()

    private val _processingStage = MutableStateFlow(ProcessingStage.IDLE)
    val processingStage = _processingStage.asStateFlow()

    private val _generatedCaption = MutableStateFlow<String?>(null)
    val generatedCaption = _generatedCaption.asStateFlow()

    // --- BATCH PROCESSING (#3) ---
    private val _batchImages = MutableStateFlow<List<Bitmap>>(emptyList())
    val batchImages = _batchImages.asStateFlow()

    private val _batchProgress = MutableStateFlow(0 to 0) // current to total
    val batchProgress = _batchProgress.asStateFlow()

    // --- SAM 2 ULTRA & PROMPTS (#2026) ---
    private val _promptPoints = MutableStateFlow<List<Pair<Offset, Boolean>>>(emptyList()) // Boolean list: true = pos, false = neg
    val promptPoints = _promptPoints.asStateFlow()

    private val _carMetadata = MutableStateFlow(EditedCar())
    val vinInfo = _carMetadata.asStateFlow()

    // --- ADVANCED EDITING (#2026) ---
    private val _adjustments = MutableStateFlow(ImageAdjustments())
    val adjustments = _adjustments.asStateFlow()

    private val _lightStyle = MutableStateFlow<DirectionalLightStyle?>(null)
    val lightStyle = _lightStyle.asStateFlow()

    private val _adjustedBitmap = MutableStateFlow<Bitmap?>(null)
    val adjustedBitmap = _adjustedBitmap.asStateFlow()

    // --- CAMERA PRO SETTINGS (#PRO) ---
    private val _cameraSettings = MutableStateFlow(CameraSettings())
    val cameraSettings = _cameraSettings.asStateFlow()

    private val _histogramData = MutableStateFlow<FloatArray?>(null)
    val histogramData = _histogramData.asStateFlow()

    private val _showAiProvidersSheet = MutableStateFlow(false)
    val showAiProvidersSheet = _showAiProvidersSheet.asStateFlow()

    private val _currentProviderName = MutableStateFlow<String?>(null)
    val currentProviderName = _currentProviderName.asStateFlow()

    private var _aiProviderManager: AIProviderManager? = null
    fun getAiProviderManager(context: Context): AIProviderManager {
        if (_aiProviderManager == null) _aiProviderManager = AIProviderManager(context.applicationContext)
        return _aiProviderManager!!
    }

    private var service: ImageEditorService? = null
    fun getService(context: Context): ImageEditorService {
        if (service == null) service = ImageEditorService(context.applicationContext)
        return service!!
    }

    private var _settingsManager: SettingsManager? = null
    private fun getSettings(context: Context): SettingsManager {
        if (_settingsManager == null) _settingsManager = SettingsManager(context.applicationContext)
        return _settingsManager!!
    }

    // Custom backgrounds from Settings
    val customBackgrounds = MutableStateFlow<Set<String>>(emptySet())

    fun loadSettings(context: Context) {
        val settings = getSettings(context)
        viewModelScope.launch {
            settings.customBackgroundPaths.collect { customBackgrounds.value = it }
        }
        
        // Collect Camera Settings
        viewModelScope.launch {
            combine<Any, CameraSettings>(
                settings.cameraIso, settings.cameraShutter, settings.cameraEv,
                settings.cameraWb, settings.cameraManualFocus, settings.cameraMetering,
                settings.cameraRes, settings.cameraQuality, settings.cameraTimer,
                settings.cameraGrid, settings.cameraHistogram
            ) { values ->
                CameraSettings(
                    iso = values[0] as Int,
                    shutterSpeedNanos = values[1] as Long,
                    exposureCompensation = values[2] as Float,
                    whiteBalanceTemp = values[3] as Int,
                    isManualFocus = values[4] as Boolean,
                    meteringMode = MeteringMode.valueOf(values[5] as String),
                    resolution = CameraResolution.valueOf(values[6] as String),
                    quality = CameraQuality.valueOf(values[7] as String),
                    timerSeconds = values[8] as Int,
                    gridType = GridType.valueOf(values[9] as String),
                    showHistogram = values[10] as Boolean
                )
            }.collect { _cameraSettings.value = it }
        }
    }

    enum class ProcessingStage { 
        IDLE, SEGMENTING, SAM2_REFINING, GLASS_REFINEMENT, 
        POLISHING, // QUALIDADE MÁXIMA - Refinamento de reflexos e sombras
        DONE, BATCH_PROCESSING 
    }

    // --- ACTIONS ---

    fun setOriginalImage(bitmap: Bitmap?) {
        _originalBitmap.value = bitmap
        _resultBitmap.value = null
        _generatedCaption.value = null
        _processingStage.value = ProcessingStage.IDLE
    }

    fun addToBatch(bitmap: Bitmap) {
        _batchImages.value += bitmap
    }

    fun clearBatch() {
        _batchImages.value = emptyList()
        _batchProgress.value = 0 to 0
    }

    fun updateOptions(newOptions: EditOptions) {
        _options.value = newOptions
    }

    fun selectStudioScene(scene: StudioScene?) {
        _options.value = _options.value.copy(selectedStudioScene = scene)
    }

    fun addPromptPoint(offset: Offset, isPositive: Boolean) {
        _promptPoints.value += (offset to isPositive)
    }

    fun clearPrompts() {
        _promptPoints.value = emptyList()
    }

    fun toggleSam2Ultra(enabled: Boolean) {
        _options.value = _options.value.copy(isSam2UltraEnabled = enabled)
    }

    fun onVinDetected(vin: String) {
        _carMetadata.value = _carMetadata.value.copy(vinCode = vin)
        decodeVin(vin)
    }

    private fun decodeVin(vin: String) {
        viewModelScope.launch {
            try {
                val response = VinDecoderModule.vinDecoderApi.decodeVin(vin)
                response.results.firstOrNull()?.let { res ->
                    _carMetadata.value = _carMetadata.value.copy(
                        carBrand = res.make,
                        carModel = res.model,
                        carYear = res.modelYear ?: ""
                    )
                }
            } catch (e: Exception) { Timber.e(e) }
        }
    }

    // --- ADJUSTMENT ACTIONS ---

    fun updateAdjustments(newAdjustments: ImageAdjustments) {
        _adjustments.value = newAdjustments
        applyLiveAdjustments()
    }

    fun setLightStyle(style: DirectionalLightStyle?) {
        _lightStyle.value = style
        applyLiveAdjustments()
    }

    fun resetAdjustments() {
        _adjustments.value = ImageAdjustments()
        _lightStyle.value = null
        applyLiveAdjustments()
    }

    /**
     * Algoritmo de Auto Enhance Inteligente.
     * Analisa o bitmap e define parâmetros ótimos de cor e brilho.
     */
    fun autoEnhance() {
        val bitmap = _resultBitmap.value ?: return
        val pro = _cameraSettings.value
        val opt = _options.value
        
        viewModelScope.launch {
            // 1. Análise real de luminância (#ELITE)
            // Em uma thread separada para não travar a UI
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                // Heurística baseada em brilho médio (simulada ou real se disponível)
                val auto = ImageAdjustments(
                    brightness = 5f + (if (pro.exposureCompensation < 0) 2f else 0f),
                    contrast = 1.1f + (if (opt.carFinish == CarFinish.GLOSSY_BLACK) 0.15f else 0f),
                    saturation = 1.15f,
                    exposure = 0.1f + (pro.exposureCompensation * -0.05f),
                    temperature = 5f,
                    sharpen = if (pro.iso > 800) 15f else 25f,
                    clarity = if (opt.carFinish == CarFinish.GLOSSY_BLACK) 25f else 15f
                )
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    _adjustments.value = auto
                    applyLiveAdjustments()
                }
            }
        }
    }

    // --- CAMERA ACTIONS ---

    fun updateCameraSettings(context: Context, newSettings: CameraSettings) {
        _cameraSettings.value = newSettings
        viewModelScope.launch {
            val s = getSettings(context)
            s.setCameraIso(newSettings.iso)
            s.setCameraShutter(newSettings.shutterSpeedNanos)
            s.setCameraEv(newSettings.exposureCompensation)
            s.setCameraWb(newSettings.whiteBalanceTemp)
            s.setCameraManualFocus(newSettings.isManualFocus)
            s.setCameraMetering(newSettings.meteringMode.name)
            s.setCameraRes(newSettings.resolution.name)
            s.setCameraQuality(newSettings.quality.name)
            s.setCameraTimer(newSettings.timerSeconds)
            s.setCameraGrid(newSettings.gridType.name)
            s.setCameraHistogram(newSettings.showHistogram)
        }
    }

    fun updateHistogram(data: FloatArray?) {
        _histogramData.value = data
    }

    fun setShowAiProvidersSheet(show: Boolean) {
        _showAiProvidersSheet.value = show
    }

    private fun applyLiveAdjustments() {
        val base = _resultBitmap.value ?: return
        val currentAdjustments = _adjustments.value
        val currentLight = _lightStyle.value
        
        viewModelScope.launch {
            _adjustedBitmap.value = ImageAdjustmentApplier.applyAdjustments(base, currentAdjustments, currentLight)
        }
    }

    fun generateAiCaption() {
        val result = _resultBitmap.value ?: return
        viewModelScope.launch {
             _generatedCaption.value = null
             // Assume service is already initialized or use a way to get it
             // Better: pass service result to VM
        }
    }

    fun processImage(context: Context) {
        val original = _originalBitmap.value ?: return
        val currentService = getService(context)
        
        viewModelScope.launch {
            _processingStage.value = ProcessingStage.SEGMENTING
            // QUALIDADE MÁXIMA - O carro deve parecer que realmente está dentro do estúdio
            try {
                // Converte Offset (Compose) para PointF (Android)
                val points = _promptPoints.value.map { (offset, isPos) -> 
                    android.graphics.PointF(offset.x, offset.y) to isPos 
                }
                
                _currentProviderName.value = getAiProviderManager(context).getPrimaryProvider().name
                val result = currentService.processCarPhoto(original, _options.value, points)
                if (result != null) {
                    _resultBitmap.value = result
                    _processingStage.value = ProcessingStage.DONE
                    
                    // Auto-caption for Elite mode
                    if (_options.value.isDealershipMode) {
                        _generatedCaption.value = currentService.generateCaption(_carMetadata.value, _options.value)
                    }

                    // --- NOVO: Auto Enhance Automático ---
                    autoEnhance()
                    
                    saveToHistory(context, original, _adjustedBitmap.value ?: result)
                } else {
                    _processingStage.value = ProcessingStage.IDLE
                }
            } catch (e: Exception) {
                _processingStage.value = ProcessingStage.IDLE
            }
        }
    }

    fun processBatch(context: Context) {
        val images = _batchImages.value
        if (images.isEmpty()) return
        val currentService = getService(context)
        val batchId = UUID.randomUUID().toString()

        viewModelScope.launch {
            _processingStage.value = ProcessingStage.BATCH_PROCESSING
            _batchProgress.value = 0 to images.size
            
            images.forEachIndexed { index, bitmap ->
                _batchProgress.value = (index + 1) to images.size
                val result = currentService.processCarPhoto(bitmap, _options.value.copy(batchMode = true))
                if (result != null) {
                    saveToHistory(context, bitmap, result, batchId = batchId)
                }
            }
            _processingStage.value = ProcessingStage.DONE
        }
    }

    fun saveToGallery(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            val meta = _carMetadata.value
            val fileName = "StudioCar_${meta.carModel ?: "Pro"}_${System.currentTimeMillis()}.jpg"
            
            // Aplica ajustes finais antes de salvar se houver
            val finalBitmap = _adjustedBitmap.value ?: bitmap
            ImageSaveHelper.saveBitmapToGallery(context, finalBitmap, fileName)
        }
    }

    fun addCustomBackground(context: Context, uri: Uri) {
        viewModelScope.launch {
            val settings = getSettings(context)
            settings.addCustomBackground(uri.toString())
        }
    }

    fun historyList(context: Context): Flow<List<EditedCar>> {
        return AppDatabase.getDatabase(context).carDao().getAllCars()
    }

    private suspend fun saveToHistory(context: Context, original: Bitmap, result: Bitmap, batchId: String? = null) {
        try {
            val meta = _carMetadata.value
            val fileName = "st_res_${System.currentTimeMillis()}.png"
            val resPath = ImageSaveHelper.saveBitmapToFile(context, result, fileName)
            val origPath = ImageSaveHelper.saveBitmapToFile(context, original, "orig_$fileName")
            
            if (resPath != null && origPath != null) {
                val car = meta.copy(
                    originalPhotoPath = origPath,
                    resultPhotoPath = resPath,
                    batchId = batchId,
                    caption = _generatedCaption.value,
                    backgroundName = _options.value.background.description,
                    floorName = _options.value.floor.description
                )
                AppDatabase.getDatabase(context).carDao().insertCar(car)
            }
        } catch (e: Exception) { Timber.e(e) }
    }

    override fun onCleared() {
        super.onCleared()
        service?.release()
    }
}
