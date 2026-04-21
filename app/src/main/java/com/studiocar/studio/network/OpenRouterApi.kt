package com.studiocar.studio.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interface Retrofit para a API do OpenRouter.
 */
interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") auth: String,
        @Header("HTTP-Referer") referer: String = "https://tlinestudio.com",
        @Header("X-Title") title: String = "StudioCar Pro",
        @Body request: ChatRequest
    ): ChatResponse
}

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val modalities: List<String>? = null, // Ex: ["image", "text"]
    @SerialName("image_config") val imageConfig: ImageConfig? = null,
    val seed: Long? = null
)

@Serializable
data class ImageConfig(
    @SerialName("image_size") val imageSize: String? = "1024x1024",
    val quality: String? = "hd"
)

@Serializable
data class Message(
    val role: String,
    val content: List<ContentItem>
)

@Serializable
data class ContentItem(
    val type: String,
    val text: String? = null,
    @SerialName("image_url") val imageUrl: ImageUrlItem? = null
)

@Serializable
data class ImageUrlItem(
    val url: String // Pode ser uma URL ou "data:image/jpeg;base64,..."
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    val content: String
)



