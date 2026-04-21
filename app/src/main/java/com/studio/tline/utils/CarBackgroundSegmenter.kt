package com.studio.tline.utils

import android.content.Context
import android.graphics.*
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
import timber.log.Timber
import kotlin.math.roundToInt

/**
 * CarBackgroundSegmenter V2.0 - Elite Dealer Edition (2026).
 * Refactored for MediaPipe 0.10.14.
 */
class CarBackgroundSegmenter(private val context: Context) {

    private var imageSegmenter: ImageSegmenter? = null

    init {
        initializeSegmenter()
    }

    private fun initializeSegmenter() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("selfie_segmenter.tflite") // Ou o modelo de carro específico se disponível
                .setDelegate(Delegate.GPU)
                .build()

            val options = ImageSegmenterOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setOutputCategoryMask(true)
                .build()

            imageSegmenter = ImageSegmenter.createFromOptions(context, options)
            Timber.d("CarBackgroundSegmenter inicializado (GPU)")
        } catch (e: Exception) {
            Timber.e(e, "Falha ao inicializar. Fallback para CPU.")
            try {
                val options = ImageSegmenterOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setModelAssetPath("selfie_segmenter.tflite").setDelegate(Delegate.CPU).build())
                    .setRunningMode(RunningMode.IMAGE)
                    .setOutputCategoryMask(true)
                    .build()
                imageSegmenter = ImageSegmenter.createFromOptions(context, options)
            } catch (ex: Exception) {
                Timber.e(ex, "Falha total no MediaPipe")
            }
        }
    }

    suspend fun removeBackground(
        originalBitmap: Bitmap,
        feather: Float = 5f,
        morph: Int = 0
    ): Bitmap = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        
        val mpImage = BitmapImageBuilder(originalBitmap).build()
        val result = imageSegmenter?.segment(mpImage) ?: return@withContext originalBitmap

        val rawMask = extractMask(result, originalBitmap.width, originalBitmap.height)
        
        // Simulação de refinamento de bordas (PostProcessor deve existir no projeto)
        // val refinedMask = PostProcessor.refineCarEdges(rawMask, feather, null, morph)
        
        val resultBitmap = applyMaskToOriginal(originalBitmap, rawMask)

        Timber.i("Processamento V2 finalizado em ${System.currentTimeMillis() - startTime}ms")
        resultBitmap
    }

    private fun extractMask(result: ImageSegmenterResult, width: Int, height: Int): Bitmap {
        val categoryMask = result.categoryMask().get()
        val byteBuffer = ByteBufferExtractor.extract(categoryMask)
        byteBuffer.rewind()
        
        val maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        
        for (i in 0 until width * height) {
            val val8 = if (byteBuffer.hasRemaining()) byteBuffer.get().toInt() else 0
            pixels[i] = if (val8 > 0) Color.WHITE else Color.TRANSPARENT
        }
        
        maskBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return maskBitmap
    }

    private fun applyMaskToOriginal(original: Bitmap, mask: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        canvas.drawBitmap(original, 0f, 0f, null)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(mask, 0f, 0f, paint)
        
        return result
    }

    fun close() {
        imageSegmenter?.close()
        imageSegmenter = null
    }
}
