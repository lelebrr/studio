package com.studiocar.studio.utils

import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

/**
 * Utilitário central de execução segura e gerenciamento de recursos.
 */
object AppUtils {

    /**
     * Executa blocos de código com tratamento de erro centralizado via Timber.
     */
    inline fun <T> safeExecute(tag: String, crossinline block: () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            Timber.tag(tag).e(e, "Erro na execução segura")
            null
        }
    }

    private val bitmapPool = ConcurrentHashMap<String, MutableList<Bitmap>>()

    /**
     * Bitmap Pool Simples para reciclagem agressiva.
     */
    fun acquireBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        val key = "${width}x${height}_${config.name}"
        val pool = bitmapPool[key]
        if (!pool.isNullOrEmpty()) {
            val bitmap = pool.removeAt(0)
            if (!bitmap.isRecycled && bitmap.isMutable) {
                bitmap.eraseColor(0)
                return bitmap
            }
        }
        return createBitmap(width, height, config)
    }

    fun releaseBitmap(bitmap: Bitmap?) {
        if (bitmap == null || bitmap.isRecycled) return
        if (!bitmap.isMutable) {
            bitmap.recycle()
            return
        }
        val key = "${bitmap.width}x${bitmap.height}_${bitmap.config?.name ?: "UNKNOWN"}"
        val pool = bitmapPool.getOrPut(key) { mutableListOf() }
        if (pool.size < 10) { // Limite por tamanho
            pool.add(bitmap)
        } else {
            bitmap.recycle()
        }
    }

    /**
     * Aplica marca d'água de elite (Branding StudioCar).
     */
    fun drawWatermark(
        source: Bitmap,
        dealershipName: String,
        logo: Bitmap? = null
    ): Bitmap {
        val workingBitmap = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = android.graphics.Canvas(workingBitmap)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            alpha = 180
            textSize = source.height / 30f
            isAntiAlias = true
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
        }

        val padding = source.width * 0.03f
        
        // Desenha Nome da Concessionária (Canto Inferior Direito)
        val textWidth = paint.measureText(dealershipName)
        canvas.drawText(
            dealershipName,
            source.width - textWidth - padding,
            source.height - padding,
            paint
        )

        // Desenha Logo se houver (Canto Inferior Esquerdo)
        logo?.let {
            val logoWidth = source.width * 0.15f
            val logoHeight = (it.height.toFloat() / it.width) * logoWidth
            val destRect = android.graphics.RectF(
                padding,
                source.height - logoHeight - padding,
                padding + logoWidth,
                source.height - padding
            )
            canvas.drawBitmap(it, null, destRect, paint)
        }

        return workingBitmap
    }
}
