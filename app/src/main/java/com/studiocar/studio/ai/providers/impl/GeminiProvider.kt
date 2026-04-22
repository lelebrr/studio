package com.studiocar.studio.ai.providers.impl

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.BitmapExtensions
import com.studiocar.studio.utils.SecurityUtils
import timber.log.Timber

class GeminiProvider(
    private val securityUtils: SecurityUtils
) : ImageAIProvider {

    override val id: String = "gemini"
    override val name: String = "Google Gemini"
    override val description: String = "IA oficial do Google com visão de alta fidelidade e raciocínio multimodal."
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
            Timber.i("Processando com Gemini API (SDK Oficial)...")
            
            val model = com.google.ai.client.generativeai.GenerativeModel(
                modelName = options.aiModelId ?: "gemini-1.5-flash",
                apiKey = apiKey
            )
            
            val inputContent = com.google.ai.client.generativeai.type.content {
                image(bitmap)
                mask?.let { image(it) }
                text(prompt)
            }
            
            val response = model.generateContent(inputContent)
            val resultText = response.text ?: return null
            
            // Se o Gemini retornar uma imagem em Base64 no texto (alguns modelos experimentais fazem isso via ferramentas)
            // Ou se usarmos a nova capacidade de inpainting via Vertex AI (Imagen).
            // Para o StudioCar 2026, assumimos que o Gemini retorna metadados ou a imagem processada via multimodal.
            if (resultText.startsWith("data:image")) {
                BitmapExtensions.fromBase64(resultText)
            } else {
                // Fallback: se não retornou imagem, mas o prompt pedia edição, 
                // pode ser que o Gemini apenas tenha descrito a mudança.
                Timber.w("Gemini retornou texto em vez de imagem: $resultText")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Gemini Error")
            null
        }
    }

    override suspend fun generateCaption(prompt: String): String {
        val apiKey = securityUtils.getApiKey(id) ?: return "Erro na API"
        return try {
            val model = com.google.ai.client.generativeai.GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
            val response = model.generateContent(prompt)
            response.text ?: "Sem resposta"
        } catch (e: Exception) {
            "Erro: ${e.message}"
        }
    }

    override suspend fun testConnection(apiKey: String): Boolean {
        if (apiKey.isEmpty()) return false
        return try {
            val model = com.google.ai.client.generativeai.GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey
            )
            val response = model.generateContent("Ping")
            response.text != null
        } catch (e: Exception) {
            false
        }
    }
}
