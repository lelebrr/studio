package com.studiocar.studio.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface HuggingFaceApi {
    @POST("models/{modelId}")
    suspend fun queryModel(
        @Header("Authorization") auth: String,
        @Path("modelId") modelId: String,
        @Body body: RequestBody
    ): ResponseBody
}
