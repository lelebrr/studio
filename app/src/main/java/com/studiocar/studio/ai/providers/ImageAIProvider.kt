package com.studiocar.studio.ai.providers

import android.graphics.Bitmap
import com.studiocar.studio.data.models.EditOptions

/**
 * Interface base para provedores de IA de imagem do StudioCar.
 * Suporta edição de imagem e geração de legendas.
 */
interface ImageAIProvider {
    val id: String
    val name: String
    val description: String
    val isAvailable: Boolean

    /**
     * Realiza a edição da imagem do carro (Background removal, Inpainting, etc).
     */
    suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap?

    /**
     * Gera uma legenda descritiva/vendedora para o veículo.
     */
    suspend fun generateCaption(
        prompt: String
    ): String?

    /**
     * Testa a conexão com o provedor (validação de API Key).
     */
    suspend fun testConnection(apiKey: String): Boolean
}
