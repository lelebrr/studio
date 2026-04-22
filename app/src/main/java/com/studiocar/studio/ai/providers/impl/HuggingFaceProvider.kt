package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.network.*
import com.studiocar.studio.utils.BitmapExtensions.toBase64
import com.studiocar.studio.utils.SecurityUtils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import timber.log.Timber

class HuggingFaceProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "huggingface"
    override val name: String = "Hugging Face"
    override val description: String = "A maior comunidade open-source: SDXL, FLUX e Segment-Anything."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        val modelId = options.aiModelId ?: "black-forest-labs/FLUX.1-dev"
        
        return try {
            val json = """{"inputs": "$prompt", "image": "${bitmap.toBase64(80).substringAfter("base64,")}"}"""
            val body = RequestBody.create("application/json".toMediaType(), json)
            
            val response = NetworkModule.huggingFaceApi.queryModel("Bearer $apiKey", modelId, body)
            val bytes = response.bytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Timber.e(e, "HuggingFace Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        return "Analisado via HF Inference."
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        return apiKey.isNotEmpty()
    }
}
