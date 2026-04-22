package com.studiocar.studio.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * Módulo de rede centralizado para o StudioCar.
 */
object NetworkModule {
    private val json = Json { 
        ignoreUnknownKeys = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val openRouterApi: OpenRouterApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(OpenRouterApi::class.java)
    }

    val stabilityApi: StabilityApi by lazy {
        createApi("https://api.stability.ai/")
    }

    val openAiApi: OpenAiApi by lazy {
        createApi("https://api.openai.com/v1/")
    }

    val anthropicApi: AnthropicApi by lazy {
        createApi("https://api.anthropic.com/")
    }

    val huggingFaceApi: HuggingFaceApi by lazy {
        createApi("https://api-inference.huggingface.co/")
    }

    val replicateApi: ReplicateApi by lazy {
        createApi("https://api.replicate.com/")
    }

    val togetherAiApi: OpenAiApi by lazy {
        createApi("https://api.together.xyz/v1/")
    }

    val fireworksAiApi: OpenAiApi by lazy {
        createApi("https://api.fireworks.ai/inference/v1/")
    }

    val grokApi: OpenAiApi by lazy {
        createApi("https://api.x.ai/v1/")
    }

    private fun <T> createApi(baseUrl: String, service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(service)
    }

    private inline fun <reified T> createApi(baseUrl: String): T = createApi(baseUrl, T::class.java)
}
