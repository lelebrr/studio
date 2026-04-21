package com.studio.tline.utils

import com.studio.tline.BuildConfig

/**
 * Biblioteca de Prompts V17 — Elite Automotive Edition 2026.
 * Otimizada para FLUX.1.1 Pro Ultra e Gemini 3 Pro.
 */
object OpenRouterConfig {
    
    // Modelos Recomendados (Abril 2026)
    const val MODEL_GEMINI_31_FLASH = "google/gemini-3.1-flash-image-preview" 
    const val MODEL_GEMINI_3_PRO = "google/gemini-3-pro-image" 
    const val MODEL_FLUX_11_PRO_ULTRA = "black-forest-labs/flux.1.1-pro-ultra" 
    const val MODEL_FLUX_2_PRO = "black-forest-labs/flux-2-pro" 
    const val MODEL_FLUX_KONTEXT = "black-forest-labs/flux-1-kontext-max"
    
    val DEFAULT_API_KEY = BuildConfig.OPENROUTER_API_KEY

    /**
     * PROMPT BASE FLUX 2026 (V17): Elite Studio.
     */
    fun getElite2026BasePrompt(carColor: String, carModel: String, bgDesc: String, floorDesc: String): String = """
        Professional 8K automotive studio photo of a clean $carColor $carModel, 
        glossy realistic paint with accurate metallic reflections and highlights, 
        transparent windows with natural light refraction clearly showing $bgDesc through the glass with correct reflections and depth, 
        $floorDesc with perfect perspective and soft realistic shadows under the car, 
        cinematic studio lighting, sharp details, high dynamic range, 
        shot on Canon EOS R5 85mm f/2.8 --stylize 0 --v 6.
    """.trimIndent()

    /**
     * PROMPT ESPECIALIZADO: Vidros Transparentes 2026.
     */
    fun getEliteGlassPrompt(bgDesc: String): String = """
        Realistic transparent car windows with precise light refraction, 
        background ($bgDesc) visible naturally through glass with accurate reflections and depth, 
        no distortion on car body or paint, professional concessionária studio photography.
    """.trimIndent()

    /**
     * NEGATIVE PROMPT ROBUSTO 2026 (V17): O Escudo Mestre.
     */
    fun getEliteNegativePrompt(): String = """
        blurry, artifacts, deformed wheels, extra windows, people or objects in background, 
        rough edges, rebarbas, bad anatomy, plastic look, oversaturated, cartoon, 
        fake shadows, text, watermark, low detail.
    """.trimIndent()
}
