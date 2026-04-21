package com.studio.tline.utils

import android.graphics.Bitmap

/**
 * Representa o pacote de dados pronto para processamento avançado por IA (ex: Gemini).
 */
data class GeminiExportData(
    val mainCarImage: Bitmap,      // Carro sem fundo
    val glassMask: Bitmap?,        // Máscara de transparência para vidros
    val originalMetadata: Map<String, String> // EXIF e dados técnicos
)

object GeminiMetadataHelper {
    
    /**
     * Prepara o pacote de exportação completo.
     */
    fun prepareExport(
        original: Bitmap,
        processed: Bitmap,
        glassMask: Bitmap?
    ): GeminiExportData {
        return GeminiExportData(
            mainCarImage = processed,
            glassMask = glassMask,
            originalMetadata = mapOf(
                "version" to "1.1",
                "engine" to "MediaPipe+TLine",
                "timestamp" to System.currentTimeMillis().toString()
            )
        )
    }
}
