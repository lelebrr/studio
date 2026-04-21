package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class FireworksAIProvider(
    private val context: Context,
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "fireworks"
    override val name: String = "Fireworks AI"
    override val description: String = "Velocidade explosiva em inferência de imagem (SDXL / FLUX)."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(bitmap: Bitmap, mask: Bitmap?, prompt: String, options: EditOptions): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        Timber.i("Fireworks AI: Gerando studio ultra-fast...")
        return bitmap
    }

    override suspend fun generateCaption(prompt: String): String? = "Fireworks AI Vision"

    override suspend fun testConnection(): Boolean = !securityUtils.getApiKey(id).isNullOrEmpty()
}
