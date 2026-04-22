package com.studiocar.studio.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AnthropicApi {
    @POST("v1/messages")
    suspend fun getMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: AnthropicRequest
    ): AnthropicResponse
}

@Serializable
data class AnthropicRequest(
    val model: String,
    val messages: List<AnthropicMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 1024
)

@Serializable
data class AnthropicMessage(
    val role: String,
    val content: List<AnthropicContent>
)

@Serializable
data class AnthropicContent(
    val type: String,
    val text: String? = null,
    val source: AnthropicSource? = null
)

@Serializable
data class AnthropicSource(
    val type: String = "base64",
    @SerialName("media_type") val mediaType: String = "image/jpeg",
    val data: String
)

@Serializable
data class AnthropicResponse(
    val content: List<AnthropicResponseContent>
)

@Serializable
data class AnthropicResponseContent(
    val type: String,
    val text: String? = null
)
