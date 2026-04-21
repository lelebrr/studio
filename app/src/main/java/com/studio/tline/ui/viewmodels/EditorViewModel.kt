package com.studio.tline.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studio.tline.ai.ImageEditorService
import com.studio.tline.data.AppDatabase
import com.studio.tline.data.models.EditedCar
import com.studio.tline.models.CarBackground
import com.studio.tline.models.CarFloor
import com.studio.tline.models.EditOptions
import com.studio.tline.utils.ImageSaveHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * EditorViewModel V8.0 - Ready for Dealerships.
 */
class EditorViewModel : ViewModel() {

    private val _originalBitmap = MutableStateFlow<Bitmap?>(null)
    val originalBitmap = _originalBitmap.asStateFlow()

    private val _resultBitmap = MutableStateFlow<Bitmap?>(null)
    val resultBitmap = _resultBitmap.asStateFlow()

    private val _options = MutableStateFlow(EditOptions())
    val options = _options.asStateFlow()

    private val _processingStage = MutableStateFlow<ProcessingStage>(ProcessingStage.IDLE)
    val processingStage = _processingStage.asStateFlow()

    private val _generatedCaption = MutableStateFlow<String?>(null)
    val generatedCaption = _generatedCaption.asStateFlow()

    enum class ProcessingStage { 
        IDLE, SEGMENTING, GEMINI_REFINING, SD_REFINING, POST_PROCESSING, DONE 
    }

    // Fluxo de histórico (Passo 15/16)
    fun historyList(context: Context): StateFlow<List<EditedCar>> {
        return AppDatabase.getDatabase(context).carDao().getAllCars()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setOriginalImage(bitmap: Bitmap?) {
        _originalBitmap.value = bitmap
        _resultBitmap.value = null
        _generatedCaption.value = null
        _processingStage.value = ProcessingStage.IDLE
    }

    /**
     * Ativa o Modo Concessionária com presets profissionais.
     */
    fun toggleDealershipMode(enabled: Boolean) {
        _options.value = if (enabled) {
            _options.value.copy(
                isDealershipMode = true,
                isUltraQuality = true,
                background = CarBackground.WHITE_SHOWROOM,
                floor = CarFloor.POLISHED_CONCRETE,
                autoWhiteBalance = true,
                extremeSharpening = true
            )
        } else {
            _options.value.copy(isDealershipMode = false)
        }
    }

    /**
     * Atualiza as opções de edição.
     */
    fun updateOptions(newOptions: EditOptions) {
        _options.value = newOptions
    }

    fun processImage(context: Context) {
        val original = _originalBitmap.value ?: return
        
        viewModelScope.launch {
            _processingStage.value = ProcessingStage.SEGMENTING
            try {
                val service = ImageEditorService(context)
                val result = service.processCarPhoto(original, _options.value)
                
                if (result != null) {
                    _resultBitmap.value = result
                    _processingStage.value = ProcessingStage.DONE
                    
                    // Gerar legenda automaticamente se estiver no Modo Concessionária
                    if (_options.value.isDealershipMode) {
                        _generatedCaption.value = service.generateAutoCaption(result)
                    }
                    
                    saveToHistory(context, original, result)
                } else {
                    _processingStage.value = ProcessingStage.IDLE
                }
            } catch (e: Exception) {
                Timber.e(e, "Erro Pipeline V8")
                _processingStage.value = ProcessingStage.IDLE
            }
        }
    }

    private suspend fun saveToHistory(context: Context, original: Bitmap, result: Bitmap) {
        try {
            val resultPath = ImageSaveHelper.saveBitmapToFile(context, result, "sales_${System.currentTimeMillis()}.png")
            val originalPath = ImageSaveHelper.saveBitmapToFile(context, original, "orig_${System.currentTimeMillis()}.png")
            
            if (resultPath != null && originalPath != null) {
                val car = EditedCar(
                    originalPhotoPath = originalPath,
                    resultPhotoPath = resultPath,
                    backgroundName = _options.value.background.description,
                    floorName = _options.value.floor.description
                )
                AppDatabase.getDatabase(context).carDao().insertCar(car)
            }
        } catch (e: Exception) {
            Timber.e(e, "Erro ao salvar histórico de vendas")
        }
    }
}
