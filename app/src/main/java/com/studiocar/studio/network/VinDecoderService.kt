package com.studiocar.studio.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface Retrofit para decodificação de VIN via NHTSA vPIC API (gratuita, sem API key).
 * https://vpic.nhtsa.dot.gov/api/
 */
interface VinDecoderApi {
    @GET("vehicles/DecodeVinValues/{vin}?format=json")
    suspend fun decodeVin(@Path("vin") vin: String): NhtsaResponse
}

@Serializable
data class NhtsaResponse(
    @SerialName("Results") val results: List<NhtsaResult>
)

@Serializable
data class NhtsaResult(
    @SerialName("Make") val make: String = "",
    @SerialName("Model") val model: String = "",
    @SerialName("ModelYear") val modelYear: String = "",
    @SerialName("BodyClass") val bodyClass: String = "",
    @SerialName("DriveType") val driveType: String = "",
    @SerialName("FuelTypePrimary") val fuelType: String = "",
    @SerialName("PlantCountry") val plantCountry: String = "",
    @SerialName("ErrorCode") val errorCode: String = "",
    @SerialName("ErrorText") val errorText: String = "",
    @SerialName("VehicleType") val vehicleType: String = "",
    @SerialName("Trim") val trim: String = "",
    @SerialName("Series") val series: String = "",
    @SerialName("EngineConfiguration") val engineConfig: String = "",
    @SerialName("DisplacementL") val displacement: String = "",
    @SerialName("EngineCylinders") val cylinders: String = "",
    @SerialName("TransmissionStyle") val transmission: String = ""
)

/**
 * Módulo de rede para NHTSA API.
 */
object VinDecoderModule {
    private val json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
    }

    val vinDecoderApi: VinDecoderApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl("https://vpic.nhtsa.dot.gov/api/")
            .client(
                okhttp3.OkHttpClient.Builder()
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(VinDecoderApi::class.java)
    }
}
