package com.studiocar.studio.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import timber.log.Timber
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.ByteBufferExtractor
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter.ImageSegmenterOptions
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenterResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * MediaPipeSegmenter V2.1 - StudioCar Elite Edition.
 * Gerencia o motor de segmentação de imagem com suporte a aceleração por hardware (GPU).
 */
class MediaPipeSegmenter(private val context: Context) {

    companion object {
        private const val TAG = "MediaPipeSegmenter"
        private const val SELFIE_SEGMENTER_MODEL = "selfie_multiclass.tflite" // Modelo mais preciso para múltiplos objetos
        private const val MAX_IMAGE_SIZE = 3072
    }

    private var segmenter: ImageSegmenter? = null
    private var isInitialized = false

    /**
     * Inicializa o motor MediaPipe com prioridade máxima para GPU.
     * @param modelFileName Nome do arquivo .tflite na pasta assets.
     */
    suspend fun initializeSegmenter(modelFileName: String = SELFIE_SEGMENTER_MODEL): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) return@withContext true

            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelFileName)
                .setDelegate(Delegate.GPU)
                .build()

            val options = ImageSegmenterOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setOutputCategoryMask(true)
                .setOutputConfidenceMasks(false)
                .build()

            segmenter = ImageSegmenter.createFromOptions(context, options)
            isInitialized = true
            Timber.tag(TAG).i("Motor de Segmentação StudioCar inicializado (GPU Mode)")
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Falha na inicialização GPU. Tentando Fallback CPU...")
            try {
                val options = ImageSegmenterOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setModelAssetPath(modelFileName).setDelegate(Delegate.CPU).build())
                    .setRunningMode(RunningMode.IMAGE)
                    .setOutputCategoryMask(true)
                    .build()
                segmenter = ImageSegmenter.createFromOptions(context, options)
                isInitialized = true
                true
            } catch (fallbackE: Exception) {
                Timber.tag(TAG).e(fallbackE, "Falha total no motor MediaPipe")
                false
            }
        }
    }

    /**
     * Libera os recursos do MediaPipe para evitar vazamento de memória.
     */
    fun release() {
        segmenter?.close()
        segmenter = null
        isInitialized = false
    }

    fun isReady(): Boolean = isInitialized && segmenter != null

    /**
     * Gera uma máscara de segmentação para o objeto principal (veículo).
     * @param bitmap Bitmap original para processamento.
     * @return Máscara em preto e branco (Branco = Objeto, Transparente = Fundo).
     */
    suspend fun segmentVehicle(bitmap: Bitmap): Bitmap? = withContext(Dispatchers.Default) {
        if (!isInitialized) initializeSegmenter()
        if (!isReady()) return@withContext null

        try {
            // Downscale rápido se necessário para performance da IA
            val processedBitmap = if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
                bitmap.scale(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true)
            } else bitmap

            val mpImage = BitmapImageBuilder(processedBitmap).build()
            val result = segmenter?.segment(mpImage) ?: return@withContext null
            
            val mask = extractMask(result, processedBitmap.width, processedBitmap.height)
            
            // Se fizemos downscale, a máscara precisa voltar ao tamanho original
            return@withContext if (processedBitmap != bitmap) {
                mask.scale(bitmap.width, bitmap.height, false).also {
                    AppUtils.releaseBitmap(mask)
                }
            } else mask
            
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Erro durante a extração de silhueta")
            null
        }
    }

    private fun extractMask(result: ImageSegmenterResult, width: Int, height: Int): Bitmap {
        val categoryMask = result.categoryMask().get()
        val byteBuffer = ByteBufferExtractor.extract(categoryMask)
        byteBuffer.rewind()
        
        val maskBitmap = AppUtils.acquireBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        
        for (i in 0 until width * height) {
            val category = byteBuffer.get().toInt()
            // Assume-se que categoria > 0 é o objeto de interesse no modelo multiclass
            pixels[i] = if (category > 0) Color.WHITE else Color.TRANSPARENT
        }
        
        maskBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return maskBitmap
    }

    fun getModelStatus(): String = if (isReady()) "StudioCar-Segmenter: Active" else "StudioCar-Segmenter: Offline"
}



