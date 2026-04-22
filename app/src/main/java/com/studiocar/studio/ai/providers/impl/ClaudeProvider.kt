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

class ClaudeProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "claude"
    override val name: String = "Anthropic Claude"
    override val description: String = "Inteligência superior para análise e refinamento de detalhes."
    override val isAvailable: Boolean
        get() = !securityUtils.getApiKey(id).isNullOrEmpty()

    override suspend fun editCarImage(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        val apiKey = securityUtils.getApiKey(id) ?: return null
        
        val content = mutableListOf<AnthropicContent>()
        content.add(AnthropicContent(type = "text", text = prompt))
        content.add(AnthropicContent(
            type = "image", 
            source = AnthropicSource(data = bitmap.toBase64(80).substringAfter("base64,"))
        ))
        
        val request = AnthropicRequest(
            model = options.aiModelId ?: "claude-3-5-sonnet-20240620",
            messages = listOf(AnthropicMessage(role = "user", content = content))
        )

        return try {
            val response = NetworkModule.anthropicApi.getMessage(apiKey = apiKey, request = request)
            val responseText = response.content.firstOrNull()?.text ?: return null
            
            // Claude geralmente retorna texto. Se ele retornar uma imagem codificada (raro mas possível via ferramentas), decodificamos.
            // Para este caso de "editCarImage", se ele não retornar imagem, retornamos null ou processamos o texto.
            if (responseText.startsWith("data:image") || responseText.length > 1000) {
                BitmapExtensions.fromBase64(responseText)
            } else {
                Timber.w("Claude retornou texto em vez de imagem: $responseText")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Claude Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        val apiKey = securityUtils.getApiKey(id) ?: return "Erro de Conexão"
        return try {
            val response = NetworkModule.anthropicApi.getMessage(
                apiKey = apiKey,
                request = AnthropicRequest(
                    model = "claude-3-5-sonnet-20240620",
                    messages = listOf(AnthropicMessage(role = "user", content = listOf(AnthropicContent(type = "text", text = prompt))))
                )
            )
            response.content.firstOrNull()?.text ?: "Sem resposta"
        } catch (e: Exception) {
            "Falha ao gerar legenda: ${e.message}"
        }
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        if (apiKey.isEmpty()) return false
        return try {
            val response = NetworkModule.anthropicApi.getMessage(
                apiKey = apiKey,
                request = AnthropicRequest(
                    model = "claude-3-5-sonnet-20240620",
                    messages = listOf(AnthropicMessage(role = "user", content = listOf(AnthropicContent(type = "text", text = "Ping")))),
                    maxTokens = 10
                )
            )
            response.content.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
