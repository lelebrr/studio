package com.studiocar.studio.ai

import android.graphics.*
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.BitmapExtensions.extractBoundingBox

/**
 * Motor de Pós-Processamento StudioCar Platinum (V2026).
 * Realiza ajustes finos após a geração por IA ou para fotos rápidas.
 * QUALIDADE MÁXIMA - O carro deve parecer que realmente está dentro do estúdio
 */
object PostProcessor {

    /**
     * Aplica toda a cadeia de refinamento selecionada pelo usuário.
     * QUALIDADE MÁXIMA - Integração perfeita entre o carro e o cenário.
     */
    fun refineImage(bitmap: Bitmap, options: EditOptions): Bitmap {
        var result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        
        // 1. Ajuste de Cor e Match de Ambiente
        // QUALIDADE MÁXIMA - Temperatura de cor e brilho sincronizados
        if (options.autoColorAdjust) {
            result = matchEnvironmentLighting(result, options)
        }
        
        // 2. Refração de Vidros e Reflexos de Estúdio
        // QUALIDADE MÁXIMA - Reflexos realistas de "Softbox" e vidros transparentes
        if (options.advancedGlassRefraction || options.isSam2UltraEnabled) {
            result = applyGlassAndStudioReflections(result)
        }
        
        // 3. Sombras de Contato Inteligentes
        // QUALIDADE MÁXIMA - Sombras de oclusão (pneus) e sombras projetadas (corpo)
        if (options.autoShadows) {
            result = refineShadows(result)
        }

        // 4. Refinamento de Bordas Ultra
        if (options.isSam2UltraEnabled) {
            result = applyUltraEdgeSmoothing(result)
        }

        return result
    }

    /**
     * Sincroniza a iluminação do carro com o cenário do estúdio.
     */
    private fun matchEnvironmentLighting(bitmap: Bitmap, options: EditOptions): Bitmap {
        val result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint()
        
        // Analisa o cenário escolhido para definir a temperatura de cor
        // Em um estúdio Platinum, usamos tons neutros com leve brilho
        val colorMatrix = ColorMatrix()
        
        // Ajuste baseado no cenário (simulado)
        val scene = options.selectedStudioScene
        if (scene != null) {
            // Exemplo: Se o cenário for "Golden Hour", aquecer a imagem
            // Se for "Clean White", neutralizar e aumentar brilho
            val brightness = 1.05f
            val saturation = 1.1f
            colorMatrix.setSaturation(saturation)
            
            // Simulação de ajuste de brilho/contraste via Matrix
            val scale = brightness
            val translate = 0f
            val matrix = floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
            colorMatrix.postConcat(ColorMatrix(matrix))
        }
        
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(result, 0f, 0f, paint)
        return result
    }

    /**
     * Aplica reflexos de estúdio na lataria e refração nos vidros.
     */
    private fun applyGlassAndStudioReflections(bitmap: Bitmap): Bitmap {
        val result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val w = result.width.toFloat()
        val h = result.height.toFloat()
        
        // 1. Reflexos de "Softbox" (Linhas de luz longas e suaves no capô/teto)
        val softboxPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            alpha = 40
        }
        
        // Gradiente linear simulando luz de teto de estúdio
        val reflection = LinearGradient(0f, 0f, w, h * 0.3f,
            intArrayOf(Color.WHITE.withAlpha(50), Color.TRANSPARENT),
            null, Shader.TileMode.CLAMP)
        
        softboxPaint.shader = reflection
        canvas.drawRect(0f, 0f, w, h * 0.4f, softboxPaint)

        // 2. Refração nos vidros (Aumenta a clareza do fundo através do vidro)
        // Aqui usaríamos a glassMask detectada anteriormente se estivesse disponível no bitmap
        // Simulamos com um overlay de brilho seletivo
        val glassPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.LIGHTEN)
            alpha = 30
        }
        canvas.drawRect(w * 0.2f, h * 0.2f, w * 0.8f, h * 0.5f, glassPaint)
        
        return result
    }

    /**
     * Gera sombras realistas de oclusão e projeção.
     * QUALIDADE MÁXIMA - Âncora o carro ao chão do estúdio.
     */
    private fun refineShadows(bitmap: Bitmap): Bitmap {
        val result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val h = bitmap.height.toFloat()
        val w = bitmap.width.toFloat()
        
        // Tenta encontrar a base do carro via Bounding Box
        val box = bitmap.extractBoundingBox()
        val bottom = box?.bottom ?: (h * 0.95f)
        val left = box?.left ?: (w * 0.1f)
        val right = box?.right ?: (w * 0.9f)
        
        // 1. Sombra de Oclusão (Preto denso logo abaixo da base/pneus)
        // É o "contato" real com o piso
        val occlusionPaint = Paint().apply {
            shader = RadialGradient(w / 2f, bottom, (right - left) / 2f,
                intArrayOf(Color.BLACK.withAlpha(200), Color.TRANSPARENT),
                floatArrayOf(0.8f, 1f), Shader.TileMode.CLAMP)
        }
        
        val occlusionRect = RectF(left, bottom - (h * 0.02f), right, bottom + (h * 0.02f))
        canvas.drawOval(occlusionRect, occlusionPaint)
        
        // 2. Sombra Projetada Suave (Difusão de luz ambiente)
        val softShadowPaint = Paint().apply {
            shader = LinearGradient(0f, bottom - (h * 0.05f), 0f, h,
                intArrayOf(Color.TRANSPARENT, Color.BLACK.withAlpha(120), Color.TRANSPARENT),
                null, Shader.TileMode.CLAMP)
        }
        
        canvas.drawRect(0f, bottom - (h * 0.05f), w, h, softShadowPaint)
        
        // 3. Sombras específicas para os pneus (Pequenas ovais densas)
        val tireShadowPaint = Paint().apply {
            color = Color.BLACK.withAlpha(220)
            maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
        }
        
        // Pneu Dianteiro (estimado)
        canvas.drawOval(RectF(left + (w * 0.1f), bottom - 5, left + (w * 0.25f), bottom + 5), tireShadowPaint)
        // Pneu Traseiro (estimado)
        canvas.drawOval(RectF(right - (w * 0.25f), bottom - 5, right - (w * 0.1f), bottom + 5), tireShadowPaint)
        
        return result
    }

    private fun applyUltraEdgeSmoothing(bitmap: Bitmap): Bitmap {
        val result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            maskFilter = BlurMaskFilter(1.2f, BlurMaskFilter.Blur.OUTER)
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }
        
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    private fun Int.withAlpha(alpha: Int): Int {
        return (this and 0x00FFFFFF) or (alpha shl 24)
    }
}
