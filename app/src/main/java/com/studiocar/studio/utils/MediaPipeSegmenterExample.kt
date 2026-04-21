package com.studiocar.studio.utils

import android.graphics.Bitmap
import timber.log.Timber
import com.studiocar.studio.utils.MediaPipeSegmenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * MediaPipeSegmenterExample - Elite 2026.
 * Exemplos simples de uso do novo MediaPipeSegmenter.
 */
object MediaPipeSegmenterExample {

    private const val TAG = "MediaPipeExample"

    /**
     * Exemplo Básico: Remove fundo de um bitmap
     */
    suspend fun simpleBackgroundRemoval(context: android.content.Context, bitmap: Bitmap): Bitmap? {
        val segmenter = MediaPipeSegmenter(context)
        return try {
            if (segmenter.initializeSegmenter()) {
                segmenter.segmentVehicle(bitmap)
            } else null
        } finally {
            segmenter.release()
        }
    }

    /**
     * Exemplo com carregamento de assets
     */
    suspend fun processAssetsImage(
        context: android.content.Context,
        assetsPath: String
    ): Bitmap? {
        val segmenter = MediaPipeSegmenter(context)
        
        try {
            if (!segmenter.initializeSegmenter()) return null
            
            val (inputStream, imageBitmap) = withContext(Dispatchers.IO) {
                val stream = context.assets.open(assetsPath)
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                stream.close()
                Pair(stream, bitmap)
            }
            
            return if (imageBitmap != null) segmenter.segmentVehicle(imageBitmap) else null
        } finally {
            segmenter.release()
        }
    }
}



