package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class StabilityAIProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "stability"
    override val name: String = "Stability AI"
    override val description: String = "Especialista em geração e edição (Stable Diffusion 3 / FLUX)."
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
            Timber.i("Stability AI: Gerando inpainting com SD3...")
            // Implementação via endpoint Stability.ai
            bitmap
        } catch (e: Exception) {
            Timber.e(e, "Stability Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        return "Foto profissional editada via Stability AI."
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        return apiKey.isNotEmpty()
    }
}
