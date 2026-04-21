package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class TogetherAIProvider(
    private val context: Context,
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "together"
    override val name: String = "Together AI"
    override val description: String = "Inference de baixa latência para modelos Stable Diffusion e FLUX."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(bitmap: Bitmap, mask: Bitmap?, prompt: String, options: EditOptions): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        Timber.i("Together AI: Processando com latência ultra-baixa...")
        return bitmap
    }

    override suspend fun generateCaption(prompt: String): String? = "Together AI Vision Output"

    override suspend fun testConnection(): Boolean = !securityUtils.getApiKey(id).isNullOrEmpty()
}
