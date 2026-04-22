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

class TogetherAIProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "together"
    override val name: String = "Together AI"
    override val description: String = "Open-source premium: Llama 3 Vision e FLUX.1 (Velocidade Ultra)."
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
            model = options.aiModelId ?: "meta-llama/Llama-3.2-11B-Vision-Instruct-Turbo",
            messages = listOf(Message(role = "user", content = content))
        )

        return try {
            val response = NetworkModule.togetherAiApi.getChatCompletion("Bearer $apiKey", request = request)
            val responseText = response.choices.firstOrNull()?.message?.content ?: return null
            
            if (responseText.startsWith("data:image") || responseText.length > 1000) {
                BitmapExtensions.fromBase64(responseText)
            } else {
                Timber.w("TogetherAI retornou texto: $responseText")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "TogetherAI Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        val apiKey = securityUtils.getApiKey(id) ?: return "Erro"
        return try {
            val response = NetworkModule.togetherAiApi.getChatCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = "meta-llama/Llama-3-70b-chat-hf",
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
            val response = NetworkModule.togetherAiApi.getChatCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = "meta-llama/Llama-3-70b-chat-hf",
                    messages = listOf(Message(role = "user", content = listOf(ContentItem(type = "text", text = "Ping"))))
                )
            )
            response.choices.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
