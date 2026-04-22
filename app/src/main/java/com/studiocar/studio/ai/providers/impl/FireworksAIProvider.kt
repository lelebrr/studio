package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.network.*
import com.studiocar.studio.utils.BitmapExtensions
import com.studiocar.studio.utils.BitmapExtensions.toBase64
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class FireworksAIProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "fireworks"
    override val name: String = "Fireworks AI"
    override val description: String = "Inference ultra-rápida de modelos state-of-the-art."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        
        val content = mutableListOf<ContentItem>()
        content.add(ContentItem(type = "text", text = prompt))
        content.add(ContentItem(type = "image_url", imageUrl = ImageUrlItem(url = bitmap.toBase64(80))))
        
        val request = ChatRequest(
            model = options.aiModelId ?: "accounts/fireworks/models/llama-v3p2-11b-vision-instruct",
            messages = listOf(Message(role = "user", content = content))
        )

        return try {
            val response = NetworkModule.fireworksAiApi.getChatCompletion("Bearer $apiKey", request = request)
            val responseText = response.choices.firstOrNull()?.message?.content ?: return null
            
            if (responseText.startsWith("data:image") || responseText.length > 1000) {
                BitmapExtensions.fromBase64(responseText)
            } else {
                Timber.w("Fireworks retornou texto: $responseText")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Fireworks Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        val apiKey = securityUtils.getApiKey(id) ?: return "Erro"
        return try {
            val response = NetworkModule.fireworksAiApi.getChatCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = "accounts/fireworks/models/llama-v3-70b-instruct",
                    messages = listOf(Message(role = "user", content = listOf(ContentItem(type = "text", text = prompt))))
                )
            )
            response.choices.firstOrNull()?.message?.content ?: "Sem resposta"
        } catch (e: Exception) {
            "Falha: ${e.message}"
        }
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        if (apiKey.isEmpty()) return false
        return try {
            val response = NetworkModule.fireworksAiApi.getChatCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = "accounts/fireworks/models/llama-v3-70b-instruct",
                    messages = listOf(Message(role = "user", content = listOf(ContentItem(type = "text", text = "Ping"))))
                )
            )
            response.choices.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
