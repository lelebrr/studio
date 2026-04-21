package com.studio.tline.utils

import android.content.Context
import android.graphics.Bitmap
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
 * MediaPipeSegmenter V2.0 - Elite 2026 Edition.
 * Fully compatible with MediaPipe Tasks Vision 0.10.14.
 */
class MediaPipeSegmenter(private val context: Context) {

    companion object {
        private const val TAG = "MediaPipeSegmenter"
        private const val HUMAN_SEGMENTER_MODEL = "human_segmenter.tflite"
        private const val MAX_IMAGE_SIZE = 4096
    }

    private var segmenter: ImageSegmenter? = null
    private var isInitialized = false

    /**
     * Inicializa o segmentador com suporte a GPU.
     */
    suspend fun initializeSegmenter(modelFileName: String = HUMAN_SEGMENTER_MODEL): Boolean = withContext(Dispatchers.IO) {
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
            Timber.i("Segmentador inicializado (GPU)")
            true
        } catch (e: Exception) {
            Timber.e("Falha na inicialização MediaPipe: ${e.message}")
            // Fallback para CPU
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
                Timber.e("Falha total no fallback: ${fallbackE.message}")
                false
            }
        }
    }

    fun release() {
        segmenter?.close()
        segmenter = null
        isInitialized = false
    }

    fun isReady(): Boolean = isInitialized && segmenter != null

    /**
     * Segmentação de Veículo/Carro para o Estúdio Profissional.
     */
    suspend fun segmentVehicle(bitmap: Bitmap): Bitmap? = withContext(Dispatchers.Default) {
        if (!isInitialized) initializeSegmenter()
        if (!isReady()) return@withContext null

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val result = segmenter?.segment(mpImage) ?: return@withContext null
            
            return@withContext extractMask(result, bitmap.width, bitmap.height)
        } catch (e: Exception) {
            Timber.e("Erro na segmentação: ${e.message}")
            null
        }
    }

    /**
     * Extrai a máscara do resultado do MediaPipe.
     */
    private fun extractMask(result: ImageSegmenterResult, width: Int, height: Int): Bitmap {
        val categoryMask = result.categoryMask().get()
        val byteBuffer = ByteBufferExtractor.extract(categoryMask)
        byteBuffer.rewind()
        
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        
        for (i in 0 until width * height) {
            val category = byteBuffer.get().toInt()
            // No modelo default, 0 costuma ser background. Se > 0 é o objeto.
            pixels[i] = if (category > 0) Color.WHITE else Color.TRANSPARENT
        }
        
        maskBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return maskBitmap
    }

    suspend fun removeBackground(bitmap: Bitmap): Bitmap? = segmentVehicle(bitmap)

    fun getModelInfo(): String = if (isReady()) "Elite Segmenter Active" else "Offline"
}
