package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class GrokProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "grok"
    override val name: String = "Grok Image API"
    override val description: String = "IA da xAI com capacidades de visão de nova geração."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(bitmap: Bitmap, mask: Bitmap?, prompt: String, options: EditOptions): Bitmap? {
        securityUtils.getApiKey(id) ?: return null
        Timber.i("Grok Image API: Processando...")
        return bitmap
    }

    override suspend fun generateCaption(prompt: String): String = "Grok Vision Insight"

    override suspend fun testConnection(): Boolean = !securityUtils.getApiKey(id).isNullOrEmpty()
}
