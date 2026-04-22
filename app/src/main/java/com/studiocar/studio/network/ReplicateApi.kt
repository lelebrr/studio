package com.studiocar.studio.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReplicateApi {
    @POST("v1/predictions")
    suspend fun createPrediction(
        @Header("Authorization") auth: String,
        @Body request: ReplicateRequest
    ): ReplicateResponse

    @GET("v1/predictions/{id}")
    suspend fun getPrediction(
        @Header("Authorization") auth: String,
        @Path("id") id: String
    ): ReplicateResponse
}

@Serializable
data class ReplicateRequest(
    val version: String? = null,
    val model: String? = null,
    val input: JsonObject
)

@Serializable
data class ReplicateResponse(
    val id: String,
    val status: String,
    val output: kotlinx.serialization.json.JsonElement? = null,
    val error: String? = null
)
