package com.studiocar.studio.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Interface Retrofit para a API da Stability AI.
 * Focada em Inpainting e Image-to-Image.
 */
interface StabilityApi {
    
    @Multipart
    @POST("v2beta/stable-image/edit/inpaint")
    suspend fun inpaint(
        @Header("Authorization") auth: String,
        @Part image: MultipartBody.Part,
        @Part mask: MultipartBody.Part,
        @Part("prompt") prompt: RequestBody,
        @Part("output_format") outputFormat: RequestBody? = null,
        @Part("grow_mask") growMask: RequestBody? = null
    ): ResponseBody

    @Multipart
    @POST("v1/generation/{engineId}/image-to-image")
    suspend fun imageToImage(
        @Path("engineId") engineId: String,
        @Header("Authorization") auth: String,
        @Part image: MultipartBody.Part,
        @Part("text_prompts[0][text]") prompt: RequestBody,
        @Part("text_prompts[0][weight]") weight: RequestBody? = null
    ): ResponseBody
}

@Serializable
data class StabilityError(
    val message: String,
    val name: String? = null
)
