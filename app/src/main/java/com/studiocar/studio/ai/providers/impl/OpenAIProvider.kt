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

class OpenAIProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "openai"
    override val name: String = "OpenAI GPT-4o"
    override val description: String = "O padrão da indústria para visão computacional e DALL-E 3."
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
            model = options.aiModelId ?: "gpt-4o",
            messages = listOf(Message(role = "user", content = content))
        )

        return try {
            val response = NetworkModule.openAiApi.getChatCompletion("Bearer $apiKey", request = request)
            val responseText = response.choices.firstOrNull()?.message?.content ?: return null
            
            if (responseText.startsWith("data:image") || responseText.length > 1000) {
                BitmapExtensions.fromBase64(responseText)
            } else {
                Timber.w("OpenAI retornou texto em vez de imagem: $responseText")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "OpenAI Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        val apiKey = securityUtils.getApiKey(id) ?: return "Erro"
        return try {
            val response = NetworkModule.openAiApi.getChatCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = "gpt-4o-mini",
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
            val response = NetworkModule.openAiApi.getChatCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = "gpt-4o-mini",
                    messages = listOf(Message(role = "user", content = listOf(ContentItem(type = "text", text = "Ping"))))
                )
            )
            response.choices.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
