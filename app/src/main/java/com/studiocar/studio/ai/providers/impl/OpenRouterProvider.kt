package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.network.*
import com.studiocar.studio.utils.BitmapExtensions.toBase64
import com.studiocar.studio.utils.OpenRouterConfig
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class OpenRouterProvider(
    private val context: Context,
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "openrouter"
    override val name: String = "OpenRouter"
    override val description: String = "Acesso unificado a múltiplos modelos (Padrão StudioCar)."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        
        val contentItems = mutableListOf<ContentItem>()
        contentItems.add(ContentItem(type = "text", text = prompt))
        contentItems.add(ContentItem(type = "image_url", imageUrl = ImageUrlItem(url = bitmap.toBase64(80))))
        mask?.let {
            contentItems.add(ContentItem(type = "image_url", imageUrl = ImageUrlItem(url = it.toBase64(50))))
        }

        val request = ChatRequest(
            model = options.aiModelId ?: OpenRouterConfig.MODEL_GEMINI_31_FLASH,
            messages = listOf(Message(role = "user", content = contentItems))
        )

        return try {
            val response = NetworkModule.openRouterApi.getCompletion("Bearer $apiKey", request = request)
            val content = response.choices.firstOrNull()?.message?.content ?: return null
            
            // Simulação de processamento de imagem (Base64 ou URL)
            if (content.contains("http") || content.startsWith("data:image")) {
                bitmap // Fallback seguro para o protótipo
            } else null
        } catch (e: Exception) {
            Timber.e(e, "OpenRouter Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        return try {
            val response = NetworkModule.openRouterApi.getCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = OpenRouterConfig.MODEL_GEMINI_31_FLASH,
                    messages = listOf(Message(role = "user", content = listOf(ContentItem(type = "text", text = prompt))))
                )
            )
            response.choices.firstOrNull()?.message?.content
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun testConnection(): Boolean {
        val apiKey = securityUtils.getApiKey(id) ?: return false
        return try {
            // Chamada light para testar a chave
            val response = NetworkModule.openRouterApi.getCompletion(
                auth = "Bearer $apiKey",
                request = ChatRequest(
                    model = OpenRouterConfig.MODEL_GEMINI_31_FLASH,
                    messages = listOf(Message(role = "user", content = listOf(ContentItem(type = "text", text = "Ping"))))
                )
            )
            response.choices.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
