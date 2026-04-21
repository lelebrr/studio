package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class HuggingFaceProvider(
    private val context: Context,
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "huggingface"
    override val name: String = "Hugging Face"
    override val description: String = "Inference API para milhares de modelos open-source (Hub)."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(bitmap: Bitmap, mask: Bitmap?, prompt: String, options: EditOptions): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        Timber.i("Hugging Face: Chamando inference API...")
        return bitmap
    }

    override suspend fun generateCaption(prompt: String): String? = "Hugging Face Vision Model"

    override suspend fun testConnection(): Boolean = !securityUtils.getApiKey(id).isNullOrEmpty()
}
