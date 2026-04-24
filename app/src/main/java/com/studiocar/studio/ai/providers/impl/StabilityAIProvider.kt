package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import com.studiocar.studio.utils.BitmapExtensions.toMultipartBody
import com.studiocar.studio.network.NetworkModule
import okhttp3.RequestBody.Companion.toRequestBody
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
        val apiKey = securityUtils.getApiKey(id) ?: return null
        val actualMask = mask ?: return null // Stability inpaint requer máscara
        
        return try {
            Timber.i("Stability AI: Iniciando inpainting...")
            
            val imagePart = bitmap.toMultipartBody("image")
            val maskPart = actualMask.toMultipartBody("mask")
            val promptBody = prompt.toRequestBody(okhttp3.MultipartBody.FORM)
            val outputFormat = "png".toRequestBody(okhttp3.MultipartBody.FORM)

            val responseBody = NetworkModule.stabilityApi.inpaint(
                auth = "Bearer $apiKey",
                image = imagePart,
                mask = maskPart,
                prompt = promptBody,
                outputFormat = outputFormat
            )

            val bytes = responseBody.bytes()
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            Timber.e(e, "Stability AI Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        return "Processado via Stability AI Inpainting."
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        return apiKey.isNotEmpty()
    }
}
