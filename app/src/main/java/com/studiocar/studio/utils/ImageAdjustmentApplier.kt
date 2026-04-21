package com.studiocar.studio.utils

import android.graphics.*
import androidx.core.graphics.createBitmap
import com.studiocar.studio.data.models.DirectionalLightStyle
import com.studiocar.studio.data.models.ImageAdjustments
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.pow

/**
 * ImageAdjustmentApplier V1.0 - Motor de Edição Profissional StudioCar.
 * Aplica ajustes de cor, nitidez e iluminação direcional usando ColorMatrix e Canvas.
 */
object ImageAdjustmentApplier {

    /**
     * Aplica todos os ajustes a um bitmap.
     * Retorna um NOVO bitmap para evitar mutação indesejada.
     */
    fun applyAdjustments(
        source: Bitmap,
        adjustments: ImageAdjustments,
        lightStyle: DirectionalLightStyle? = null
    ): Bitmap {
        val width = source.width
        val height = source.height
        val result = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Matriz de Cor Combinada (Brightness, Contrast, Saturation, Exposure, Temperature)
        val colorMatrix = ColorMatrix()
        
        // Ajuste de Brilho e Contraste
        val cmContrast = ColorMatrix()
        val contrast = adjustments.contrast
        val brightness = adjustments.brightness
        cmContrast.set(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        colorMatrix.postConcat(cmContrast)

        // Ajuste de Saturação
        val cmSaturation = ColorMatrix()
        cmSaturation.setSaturation(adjustments.saturation)
        colorMatrix.postConcat(cmSaturation)

        // Ajuste de Exposição (Simplificado via Matriz)
        if (adjustments.exposure != 0f) {
            val exp = 2.0.pow(adjustments.exposure.toDouble()).toFloat()
            val cmExposure = ColorMatrix(floatArrayOf(
                exp, 0f, 0f, 0f, 0f,
                0f, exp, 0f, 0f, 0f,
                0f, 0f, exp, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(cmExposure)
        }

        // Ajuste de Temperatura (Frio/Quente)
        if (adjustments.temperature != 0f) {
            val temp = adjustments.temperature / 100f
            val rMod = if (temp > 0) 1f + temp * 0.2f else 1f + temp * 0.1f
            val bMod = if (temp > 0) 1f - temp * 0.2f else 1f - temp * 0.4f
            val cmTemp = ColorMatrix(floatArrayOf(
                rMod, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, bMod, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(cmTemp)
        }

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(source, 0f, 0f, paint)

        // 2. Aplicação de Nitidez/Sharpen (Simulação via Alpha Blending de cópia deslocada ou similar)
        // Para um realismo maior em Android puro sem Shaders, usamos uma técnica de High-Pass leve
        if (adjustments.sharpen > 0f) {
             applySharpenEffect(canvas, result, adjustments.sharpen)
        }

        // 3. Aplicação de Luz Direcional (Overlays de Gradiente)
        lightStyle?.let {
            applyDirectionalLight(canvas, width, height, it)
        }

        return result
    }

    private fun applySharpenEffect(canvas: Canvas, bitmap: Bitmap, amount: Float) {
        // Técnica simples de Sharpen: Sobrepor o bitmap sobre ele mesmo com um leve offset e blending
        // Em um app de produção usaríamos um RenderScript IntrinsicConvolve3x3 ou ScriptIntrinsicBlur
        // Aqui simulamos um efeito de "Clarity/Sharpen" combinando contraste local
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.alpha = (amount * 0.3f).toInt().coerceIn(0, 50)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.OVERLAY)
        canvas.drawBitmap(bitmap, 1f, 1f, paint)
    }

    private fun applyDirectionalLight(canvas: Canvas, width: Int, height: Int, style: DirectionalLightStyle) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN) // Screen ou Overlay para luz

        val gradient: Shader = when (style) {
            DirectionalLightStyle.SOFT_FRONT -> {
                RadialGradient(
                    width / 2f, height / 2f, width.toFloat(),
                    intArrayOf(Color.argb(40, 255, 255, 255), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP
                )
            }
            DirectionalLightStyle.SIDE_LEFT -> {
                LinearGradient(
                    0f, 0f, width * 0.4f, 0f,
                    intArrayOf(Color.argb(60, 255, 255, 255), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP
                )
            }
            DirectionalLightStyle.SIDE_RIGHT -> {
                LinearGradient(
                    width.toFloat(), 0f, width * 0.6f, 0f,
                    intArrayOf(Color.argb(60, 255, 255, 255), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP
                )
            }
            DirectionalLightStyle.DRAMATIC_45 -> {
                LinearGradient(
                    0f, 0f, width * 0.7f, height * 0.7f,
                    intArrayOf(Color.argb(80, 255, 255, 255), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP
                )
            }
            DirectionalLightStyle.TOP_STUDIO -> {
                LinearGradient(
                    0f, 0f, 0f, height * 0.3f,
                    intArrayOf(Color.argb(100, 255, 255, 255), Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP
                )
            }
            DirectionalLightStyle.NATURAL_WINDOW -> {
                // Simula luz de janela com faixas
                val colors = intArrayOf(
                    Color.argb(50, 255, 255, 255), Color.TRANSPARENT,
                    Color.argb(50, 255, 255, 255), Color.TRANSPARENT
                )
                LinearGradient(
                    0f, 0f, width * 0.5f, height * 0.5f,
                    colors, floatArrayOf(0f, 0.2f, 0.4f, 1f), Shader.TileMode.REPEAT
                )
            }
        }

        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

}
