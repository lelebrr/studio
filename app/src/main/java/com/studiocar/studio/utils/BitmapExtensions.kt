package com.studiocar.studio.utils

import android.graphics.Bitmap
import androidx.core.graphics.scale
import android.util.Base64
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

/**
 * Extensões de Bitmap para a pipeline StudioCar.
 */
object BitmapExtensions {

    /**
     * Converte Bitmap para String Base64 formatada para APIs (data:image/jpeg;base64,...).
     */
    fun Bitmap.toBase64(quality: Int = 80): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val byteArray = outputStream.toByteArray()
            "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Converte String Base64 de volta para Bitmap.
     */
    fun fromBase64(base64: String): Bitmap? {
        return try {
            val cleanBase64 = if (base64.contains(",")) base64.split(",")[1] else base64
            val decodedString = Base64.decode(cleanBase64, Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Redimensiona o bitmap mantendo o aspect ratio, respeitando um tamanho máximo.
     */
    fun Bitmap.scaleToMax(maxSize: Int): Bitmap {
        if (width <= maxSize && height <= maxSize) return this
        val ratio = width.toFloat() / height.toFloat()
        val (newWidth, newHeight) = if (width > height) {
            maxSize to (maxSize / ratio).toInt()
        } else {
            (maxSize * ratio).toInt() to maxSize
        }
        return this.scale(newWidth, newHeight, true)
    }

    /**
     * Extrai a Bounding Box de uma máscara de segmentação.
     * Varre os pixels para encontrar os limites (min X, min Y, max X, max Y).
     */
    fun Bitmap.extractBoundingBox(): android.graphics.RectF? {
        var minX = width
        var minY = height
        var maxX = -1
        var maxY = -1
        var found = false

        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val alpha = android.graphics.Color.alpha(pixels[y * width + x])
                if (alpha > 128) { // Consideramos pixel opaco como parte do objeto
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                    found = true
                }
            }
        }

        return if (found) {
            android.graphics.RectF(minX.toFloat(), minY.toFloat(), maxX.toFloat(), maxY.toFloat())
        } else null
    }

    /**
     * Calcula o brilho médio de um bitmap para auto-ajuste.
     */
    fun Bitmap.calculateAverageBrightness(): Float {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)
        var totalBrightness = 0L
        for (pixel in pixels) {
            val r = android.graphics.Color.red(pixel)
            val g = android.graphics.Color.green(pixel)
            val b = android.graphics.Color.blue(pixel)
            totalBrightness += (r + g + b) / 3
        }
        return totalBrightness.toFloat() / (width * height)
    }

    /**
     * Converte Bitmap para MultipartBody.Part para upload em APIs.
     */
    fun Bitmap.toMultipartBody(name: String, filename: String = "image.png"): okhttp3.MultipartBody.Part {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.Config.ARGB_8888.let { if (android.os.Build.VERSION.SDK_INT >= 30) Bitmap.CompressFormat.WEBP_LOSSLESS else Bitmap.CompressFormat.PNG }, 100, outputStream)
        val requestBody = outputStream.toByteArray().toRequestBody(
            "image/png".toMediaTypeOrNull()
        )
        return okhttp3.MultipartBody.Part.createFormData(name, filename, requestBody)
    }

    /**
     * Carrega um Bitmap a partir de uma URL.
     */
    suspend fun fromUrl(url: String): Bitmap? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            android.graphics.BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Erro ao baixar imagem da URL: $url")
            null
        }
    }
}
