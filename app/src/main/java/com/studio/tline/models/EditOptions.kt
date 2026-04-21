package com.studio.tline.models

/**
 * Opções de fundo para o cenário do carro.
 */
enum class CarBackground(val description: String, val prompt: String) {
    WHITE_SHOWROOM("Showroom Branco Puro", "Pure white minimalist luxury modern car showroom with soft overhead panel lighting"),
    GREY_SHOWROOM("Showroom Cinza Moderno", "Modern industrial grey car showroom with concrete textures and linear LED lighting"),
    BLACK_STUDIO("Estúdio Preto Premium", "Premium black photography studio, dramatic lighting, high contrast, luxury atmosphere"),
    SUNSET_ROAD("Estrada ao Pôr do Sol", "Empty coastal asphalt road during golden hour, epic sky, warm sunlight"),
    FOREST_ROAD("Floresta Exuberante", "Scenic road through a dense green forest, natural dappled sunlight through trees"),
    NEON_CITY("Cidade à Noite (Neon)", "Modern urban street at night, vibrant neon signs, wet asphalt reflections"),
    MODERN_GARAGE("Garagem Moderna", "Ultra-modern private garage with glass walls and minimalist design")
}

/**
 * Opções de piso (solo).
 */
enum class CarFloor(val description: String, val prompt: String) {
    WHITE_GLOSSY("Chão Branco Brilhante", "Glossy white reflective showroom floor, mirrors the car perfectly"),
    POLISHED_CONCRETE("Concreto Polido", "Clean grey industrial polished concrete floor with subtle texture"),
    WET_ASPHALT("Asfalto Molhado", "Dark wet asphalt with realistic puddles and sharp reflections"),
    BLACK_EPOXY("Piso Epóxi Preto", "Mirror-like black epoxy resin floor, ultra-high contrast reflections"),
    NATURAL_GRASS("Grama Natural", "Meticulously maintained green lawn, natural park-like environment")
}

/**
 * Configuração completa da tarefa de edição com foco em Ultra Qualidade.
 */
data class EditOptions(
    val background: CarBackground = CarBackground.WHITE_SHOWROOM,
    val floor: CarFloor = CarFloor.WHITE_GLOSSY,
    val isAdvancedMode: Boolean = false,
    
    // Novas Flags de Ultra Qualidade (Passo 7/8)
    val isUltraQuality: Boolean = false,
    val isDealershipMode: Boolean = true, // Ativa presets de revenda por padrão
    val iterations: Int = 1,
    val removeReflections: Boolean = false,
    val isPhotographic: Boolean = true,
    val autoWhiteBalance: Boolean = true,
    val extremeSharpening: Boolean = false,
    
    val maxResolution: Int = 4096 // Forçado no Modo Ultra
)
