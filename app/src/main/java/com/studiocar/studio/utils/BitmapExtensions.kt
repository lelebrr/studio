package com.studiocar.studio.utils

import android.graphics.Bitmap
import androidx.core.graphics.scale
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Extensões de Bitmap para a pipeline StudioCar.
 */
object BitmapExtensions {

    /**
     * Converte Bitmap para String Base64 formatada para APIs (data:image/jpeg;base64,...).
     */
    fun Bitmap.toBase64(quality: Int = 80): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return "data:image/jpeg;base64," + Base64.encodeToString(byteArray, Base64.NO_WRAP)
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
}
