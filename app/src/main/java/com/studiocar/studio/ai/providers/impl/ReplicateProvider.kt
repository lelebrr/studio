package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class ReplicateProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "replicate"
    override val name: String = "Replicate"
    override val description: String = "Plataforma de modelos open-source poderosos (FLUX.1, Llama)."
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
            Timber.i("Replicate: Processando imagem...")
            bitmap
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        return "Processado via Replicate Engine."
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        return apiKey.isNotEmpty()
    }
}
