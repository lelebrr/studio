package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.network.*
import com.studiocar.studio.utils.BitmapExtensions
import com.studiocar.studio.utils.BitmapExtensions.toBase64
import com.studiocar.studio.utils.SecurityUtils
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
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
        val apiKey = securityUtils.getApiKey(id) ?: return null
        val auth = "Token $apiKey"
        
        return try {
            val input = buildJsonObject {
                put("prompt", JsonPrimitive(prompt))
                put("image", JsonPrimitive(bitmap.toBase64(80)))
                mask?.let { put("mask", JsonPrimitive(it.toBase64(80))) }
            }
            
            val request = ReplicateRequest(
                version = options.aiModelId, // Se null, Replicate pode exigir o version ID
                input = input
            )
            
            var prediction = NetworkModule.replicateApi.createPrediction(auth, request)
            
            // Polling simples (máximo 30 segundos)
            var attempts = 0
            while (prediction.status != "succeeded" && prediction.status != "failed" && attempts < 15) {
                kotlinx.coroutines.delay(2000)
                prediction = NetworkModule.replicateApi.getPrediction(auth, prediction.id)
                attempts++
            }
            
            if (prediction.status == "succeeded") {
                val output = prediction.output?.jsonPrimitive?.content ?: return null
                if (output.startsWith("http")) {
                    BitmapExtensions.fromUrl(output)
                } else {
                    BitmapExtensions.fromBase64(output)
                }
            } else null
        } catch (e: Exception) {
            Timber.e(e, "Replicate Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        return "Processado via Replicate."
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        return apiKey.isNotEmpty()
    }
}
