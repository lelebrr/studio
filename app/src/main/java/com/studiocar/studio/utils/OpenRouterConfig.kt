package com.studiocar.studio.utils

import com.studiocar.studio.BuildConfig

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
    
    const val DEFAULT_API_KEY = BuildConfig.OPENROUTER_API_KEY

    /**
     * PROMPT BASE FLUX 2026 (V17): Elite Studio Platinum.
     * QUALIDADE MÁXIMA - Foco em óptica física e realismo absoluto.
     */
    fun getElite2026BasePrompt(carColor: String, carModel: String, bgDesc: String, floorDesc: String): String = """
        Ultra-realistic 8K professional automotive photography of a $carColor $carModel.
        Vehicle integrated into a high-end $bgDesc studio environment.
        Physically based rendering (PBR) metallic paint with deep gloss, realistic Fresnel reflections, and accurate highlights.
        Transparent windows with optical refraction, sub-surface scattering on trim, and crystal-clear visibility of interior through glass.
        $floorDesc with ray-traced contact shadows under tires and soft global illumination.
        Mirror-like floor reflections, professional 3-point studio lighting setup, cinematic depth of field.
        No grain, zero artifacts, perfect edges, shot on Phase One XF IQ4, 100MP, sharp focus.
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
    /**
     * PROMPT B2B ELITE (#14): Padrão Platinum de Concessionária.
     */
    fun getB2BElitePrompt(carColor: String, carModel: String, dealerName: String): String = """
        Ultra-high-end $carColor $carModel showcase for $dealerName dealership, 
        premium showroom lighting, flawless metallic paint, 4K HDR detail, 
        transparent windows with high-refractive glass (#13), 
        perfectly clean floor with contact shadows (#10), 
        minimalist elegant background, absolute realism, no people.
    """.trimIndent()

    /**
     * MODO NOTURNO (#22): Longa Exposição e Nitidez Artificial.
     */
    fun getNightModePrompt(carColor: String, carModel: String): String = """
        Dynamic night photography of $carColor $carModel, urban city lights in background, 
        long exposure lighting trails, extremely high ISO clarity with absolute zero grain, 
        sharp highlights on car body contours, noir aesthetic, cinematic atmosphere.
    """.trimIndent()

    /**
     * GEMINI CAPTION GENERATOR (#11): Copywriting Automotivo de Alto Nível.
     */
    fun getCaptionPrompt(vinData: String, currentOptions: String): String = """
        Você é um copywriter profissional de carros de luxo. 
        Gere uma legenda elegante para Instagram/Web baseada nestes dados: $vinData. 
        Opções aplicadas: $currentOptions. 
        A legenda deve ter:
        1. Gancho de impacto.
        2. 3 destaques técnicos.
        3. Chamada de ação (CTA).
        4. No máximo 500 caracteres, tom sofisticado.
    """.trimIndent()

    /**
     * REMOÇÃO DE OBJETOS (#9): Limpeza de Cena via Inpainting.
     */
    fun getObjectRemovalPrompt(): String = "Remove all people, trash, street clutter, and unwanted reflections. Reconstruct background with seamless texture matching the car's environment."

    /**
     * REFINAMENTO ULTRA PREMIUM (GEMINI 3.1): Máscara e Vidros.
     */
    fun getUltraRefinementPrompt(): String = """
        Refine this car mask for maximum precision. 
        1. Remove all rough edges and debris (rebarbas).
        2. Smooth edges naturally while preserving sharp details on wheels, grill, and antennas.
        3. Identify transparent glass areas and create a separate alpha mask for them.
        4. Ensure accurate light refraction and realistic reflections of the new environment through the windows.
        High-fidelity professional studio quality output required.
    """.trimIndent()

    /**
     * POLIMENTO FINAL ULTRA (FLUX 1.1 PRO): Realismo Extremo Platinum.
     * QUALIDADE MÁXIMA - O carro deve parecer que realmente está dentro do estúdio.
     */
    fun getFluxUltraPolishingPrompt(bgDesc: String, floorDesc: String): String = """
        Extreme photorealistic automotive studio masterwork. 
        Flawless integration of the car into a $bgDesc environment. 
        $floorDesc with pixel-perfect occlusion shadows and sharp mirror reflections.
        Enhance anisotropy on wheels, chrome highlights with zero chromatic aberration.
        Preserve 100% of fine details: antennas, emblems, and textures. 
        Lighting must match perfectly with the background's global illumination.
        Final result must be indistinguishable from a real professional photo.
    """.trimIndent()
}



