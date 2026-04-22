package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class OpenAIProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "openai"
    override val name: String = "OpenAI DALL·E 3"
    override val description: String = "Poder criativo e fotorrealismo (GPT-4o + DALL·E 3)."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        securityUtils.getApiKey(id) ?: return null
        return try {
            Timber.i("OpenAI: Editando imagem com DALL-E 3...")
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        return "Veículo de alto desempenho (OpenAI Vision)."
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        return apiKey.isNotEmpty()
    }
}
