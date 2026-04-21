package com.studiocar.studio.utils

import android.graphics.*
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.core.graphics.toColorInt

/**
 * Utilitários de pós-processamento para imagens e máscaras.
 */
object PostProcessor {

    /**
     * Aplica uma dilatação ou erosão na máscara e um blur Gaussian para suavizar bordas.
     */
    fun refineCarEdges(
        mask: Bitmap,
        featherAmount: Float,
        threshold: Float? = null,
        morphAmount: Int = 0 // 0 = nenhum, >0 = dilate, <0 = erode
    ): Bitmap {
        val width = mask.width
        val height = mask.height
        val refined = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(refined)
        
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // 1. Operação Morfológica Simples (Dilate/Erode) via Stroke
        val morphBitmap = if (morphAmount != 0) {
            applyMorphology(mask, morphAmount)
        } else {
            mask
        }

        // 2. Se houver threshold, aplica primeiro
        if (threshold != null) {
            val colorFilter = ColorMatrixColorFilter(ColorMatrix(floatArrayOf(
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f, 0f,
                0f, 0f, 0f, 255f, -255f * threshold
            )))
            paint.colorFilter = colorFilter
        }

        // 3. Aplica Blur (Gaussian-like) para o Feathering
        if (featherAmount > 0) {
            paint.maskFilter = BlurMaskFilter(featherAmount.coerceAtLeast(0.1f), BlurMaskFilter.Blur.NORMAL)
        }

        canvas.drawBitmap(morphBitmap, 0f, 0f, paint)
        
        if (morphBitmap != mask) morphBitmap.recycle()
        
        return refined
    }

    private fun applyMorphology(mask: Bitmap, amount: Int): Bitmap {
        val result = createBitmap(mask.width, mask.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        if (amount > 0) {
            // Dilate: Desenha a máscara e um stroke por fora
            canvas.drawBitmap(mask, 0f, 0f, null)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = amount.toFloat()
            paint.color = Color.WHITE
            canvas.drawBitmap(mask, 0f, 0f, paint)
        } else {
            // Erode: Desenha por cima com transparente usando stroke
            canvas.drawBitmap(mask, 0f, 0f, null)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = (-amount).toFloat()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            canvas.drawBitmap(mask, 0f, 0f, paint)
        }
        return result
    }

    /**
     * Heurística para extrair máscara de vidros baseada em luminância.
     * Útil para preparação de dados para o Gemini.
     */
    fun extractGlassMask(original: Bitmap, carMask: Bitmap): Bitmap {
        val width = original.width
        val height = original.height
        val glassMask = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        // Simulação de extração de áreas reflexivas (vidros)
        // Em um cenário real, usaríamos um modelo de detecção de vidros.
        // Aqui usamos uma heurística de luminância nas áreas do carro.
        for (y in 0 until height step 2) {
            for (x in 0 until width step 2) {
                if (Color.alpha(carMask[x, y]) > 200) {
                    val pixel = original[x, y]
                    val gray = (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()
                    
                    // Vidros geralmente têm alta reflexão ou cor específica
                    if (gray > 180) { 
                        glassMask[x, y] = Color.WHITE
                    }
                }
            }
        }
        return glassMask
    }

    /**
     * Cria um bitmap com fundo gradiente ou sólido.
     */
    fun createBackground(width: Int, height: Int, isGradient: Boolean): Bitmap {
        val background = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(background)
        val paint = Paint()
        
        if (isGradient) {
            paint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                "#EEEEEE".toColorInt(), "#BBBBBB".toColorInt(),
                Shader.TileMode.CLAMP
            )
        } else {
            paint.color = Color.WHITE
        }
        
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return background
    }
}



