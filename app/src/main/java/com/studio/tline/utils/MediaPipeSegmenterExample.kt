package com.studio.tline.utils

import android.graphics.Bitmap
import timber.log.Timber
import com.studio.tline.utils.MediaPipeSegmenter

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
            
            val inputStream = context.assets.open(assetsPath)
            val imageBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            return if (imageBitmap != null) segmenter.removeBackground(imageBitmap) else null
        } finally {
            segmenter.release()
        }
    }
}
