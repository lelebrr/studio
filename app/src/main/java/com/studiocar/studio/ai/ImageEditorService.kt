package com.studiocar.studio.ai

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.AIProviderManager
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.data.models.EditedCar
import com.studiocar.studio.network.*
import com.studiocar.studio.utils.*
import com.studiocar.studio.utils.BitmapExtensions.extractBoundingBox
import com.studiocar.studio.utils.BitmapExtensions.scaleToMax
import com.studiocar.studio.utils.BitmapExtensions.toBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * ImageEditorService V2.5 - StudioCar Ultra Engine.
 * Orquestrador central da pipeline IA: MediaPipe + Gemini + FLUX + PostProcessor.
 */
class ImageEditorService(private val context: Context) {

    private val segmenter by lazy { MediaPipeSegmenter(context) }
    private val samSegmenter by lazy { SAMSegmenter(context) } // Integração SAM 2
    private val advancedSegmenter by lazy { AdvancedCarSegmenter(context) }
    private val securityUtils by lazy { SecurityUtils(context) }
    private val settingsManager by lazy { SettingsManager(context) }
    private val aiProviderManager by lazy { AIProviderManager(context) }

    /**
     * Processa um LOTE de fotos (#3).
     */
    suspend fun processBatch(
        images: List<Bitmap>,
        options: EditOptions,
        onProgress: (Int, Int) -> Unit
    ): List<Bitmap> = withContext(Dispatchers.Default) {
        images.mapIndexed { index, bitmap ->
            onProgress(index + 1, images.size)
            processCarPhoto(bitmap, options)
        }
    }

    /**
     * Pipeline Principal de Processamento StudioCar (Platinum Quality 2026).
     * Orquestração Híbrida: MediaPipe -> SAM 2 -> Gemini -> FLUX.
     * QUALIDADE MÁXIMA - O carro deve parecer que realmente está dentro do estúdio
     */
    suspend fun processCarPhoto(
        original: Bitmap,
        options: EditOptions,
        points: List<Pair<android.graphics.PointF, Boolean>>? = null
    ): Bitmap = withContext(Dispatchers.Default) {
        val totalStartTime = System.currentTimeMillis()
        var input: Bitmap? = null
        var mask: Bitmap? = null
        var geminiResult: Bitmap? = null
        var finalResult: Bitmap? = null
        
        try {
            val isDemo = settingsManager.isDemoMode.first()
            val isOffline = settingsManager.isOfflineMode.first()
            val primaryProvider = aiProviderManager.getPrimaryProvider()
            val dealershipName = settingsManager.dealershipName.first()
            val useSam2Ultra = options.isSam2UltraEnabled 

            // 1. SEGMENTAÇÃO INICIAL E REFINAMENTO SAM 2
            input = original.scaleToMax(options.maxResolution)
            
            val (segMask, _) = if (useSam2Ultra) {
                Timber.i("MODO SAM 2 ULTRA ATIVADO")
                val initialMask = segmenter.segmentVehicle(input)
                val box = initialMask?.extractBoundingBox()
                val boxArray = if (box != null) floatArrayOf(box.left, box.top, box.right, box.bottom) else null
                
                val samPoints = points?.map { (point, isPositive) ->
                    val x = (point.x / original.width) * 1024f
                    val y = (point.y / original.height) * 1024f
                    floatArrayOf(x, y) to intArrayOf(if (isPositive) 1 else 0)
                }

                val refinedSamMask = samSegmenter.segment(input, points = samPoints, box = boxArray)
                val finalMask = (refinedSamMask ?: initialMask)
                if (initialMask != finalMask) initialMask?.recycle()
                finalMask to null
            } else if (options.isUltraQuality) {
                advancedSegmenter.segmentUltra(input)
            } else {
                (segmenter.segmentVehicle(input) to null)
            }
            mask = segMask
            
            // 2. MODO OFFLINE / DEMO
            if (isOffline || isDemo || !primaryProvider.isAvailable) {
                return@withContext PostProcessor.refineImage(input, options).also {
                    input.recycle()
                    mask?.recycle()
                }
            }

            // 3. ESTÁGIO IA 1: GEMINI P/ ESTRUTURA E REFRAÇÃO
            val usePro = settingsManager.useProModels.first()
            val sceneDescription = options.selectedStudioScene?.name ?: options.background.description
            val floorDescription = options.selectedStudioScene?.name ?: options.floor.description

            val promptGemini = if (useSam2Ultra) {
                OpenRouterConfig.getUltraRefinementPrompt()
            } else if (options.isDealershipMode) {
                OpenRouterConfig.getB2BElitePrompt("Original", "Vehicle", dealershipName)
            } else {
                OpenRouterConfig.getEliteGlassPrompt(sceneDescription)
            }
            
            geminiResult = aiProviderManager.editImageWithFallback(
                bitmap = input,
                mask = mask,
                prompt = promptGemini,
                options = options.copy(aiModelId = if (usePro) OpenRouterConfig.MODEL_GEMINI_3_PRO else OpenRouterConfig.MODEL_GEMINI_31_FLASH)
            )

            // 4. ESTÁGIO IA 2: FLUX P/ POLIMENTO E REFLEXOS
            val fluxModel = if (usePro) OpenRouterConfig.MODEL_FLUX_11_PRO_ULTRA else OpenRouterConfig.MODEL_FLUX_2_PRO
            val fluxPrompt = if (useSam2Ultra) {
                OpenRouterConfig.getFluxUltraPolishingPrompt(sceneDescription, floorDescription)
            } else if (options.nightMode) {
                OpenRouterConfig.getNightModePrompt("Original", "Vehicle")
            } else {
                val base = OpenRouterConfig.getElite2026BasePrompt(
                    "High Gloss", "Premium Car", sceneDescription, floorDescription
                )
                if (options.isPhotographic) "$base Photographic style, real lenses." else base
            }

            finalResult = aiProviderManager.editImageWithFallback(
                bitmap = geminiResult ?: input,
                mask = mask,
                prompt = if (options.removeReflections) "$fluxPrompt Remove all existing distracting reflections." else fluxPrompt,
                options = options.copy(aiModelId = fluxModel)
            )

            // 5. PÓS-PROCESSAMENTO LOCAL PESADO
            val finalOptions = options.copy(
                isSam2UltraEnabled = useSam2Ultra,
                isUltraQuality = options.isUltraQuality || useSam2Ultra // Se SAM 2, força Ultra
            )
            val output = PostProcessor.refineImage(finalResult ?: geminiResult ?: input, finalOptions)
            
            Timber.i("Pipeline StudioCar Ultra finalizada em ${System.currentTimeMillis() - totalStartTime}ms")
            
            // Cleanup
            if (input != original) input.recycle()
            mask?.recycle()
            geminiResult?.recycle()
            finalResult?.recycle()
            
            output
        } catch (e: Exception) {
            Timber.e(e, "Falha crítica no processamento StudioCar")
            original
        }
    }

    /**
     * Gera uma legenda de Elite via Gemini (#11, #19).
     */
    suspend fun generateCaption(car: EditedCar, options: EditOptions): String = withContext(Dispatchers.IO) {
        try {
            val provider = aiProviderManager.getPrimaryProvider()
            if (!provider.isAvailable) return@withContext "Oferta imperdível no StudioCar!"

            val vinData = "Marca: ${car.carBrand}, Modelo: ${car.carModel}, Ano: ${car.carYear}, Cor: ${car.carColor}"
            val optData = "Background: ${options.background.name}, Pack: ${if(options.isDealershipMode) "Platinum" else "Standard"}"
            
            provider.generateCaption(OpenRouterConfig.getCaptionPrompt(vinData, optData)) 
                ?: "OFERTA EXCLUSIVA: Carro em estado de novo! #StudioCar"
        } catch (e: Exception) {
            "OFERTA EXCLUSIVA: Venha conferir! #StudioCar"
        }
    }

    // Mantemos por compatibilidade, mas o fluxo principal agora usa o ProviderManager
    private suspend fun callVisionModel(
        bitmap: Bitmap,
        mask: Bitmap?,
        model: String,
        prompt: String
    ): Bitmap? {
        val provider = aiProviderManager.getProvider("openrouter") ?: return null
        return provider.editCarImage(bitmap, mask, prompt, EditOptions(aiModelId = model))
    }

    fun release() {
        segmenter.release()
        ImageCacheManager.clear()
    }
}
