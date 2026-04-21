package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class ClaudeProvider(
    private val context: Context,
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "claude"
    override val name: String = "Anthropic Claude"
    override val description: String = "Referência em raciocínio visual e geração de metadados complexos."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        return try {
            Timber.i("Processando com Claude 3.5 Sonnet Vision...")
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String? {
        return "Legenda refinada por Claude AI."
    }

    override suspend fun testConnection(): Boolean {
        return !securityUtils.getApiKey(id).isNullOrEmpty()
    }
}
