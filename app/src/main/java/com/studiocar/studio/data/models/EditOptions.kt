package com.studiocar.studio.data.models

/**
 * Modo de fotografia — define overlay e otimizações da câmera.
 */
enum class PhotoMode(val label: String, val icon: String) {
    EXTERIOR("Exterior", "🚗"),
    INTERIOR("Interior", "🪑"),
    ENGINE("Motor", "⚙️"),
    WHEELS("Rodas", "🛞"),
    DETAIL("Detalhe", "🔍")
}

/**
 * Tipo de acabamento do carro — usado para ajuste automático de cor/brilho.
 */
enum class CarFinish(val label: String, val description: String) {
    GLOSSY_BLACK("Preto Brilhante", "Deep glossy black with mirror-like reflections"),
    WHITE("Branco", "Clean white with soft highlights"),
    MATTE("Fosco", "Matte finish with minimal reflections"),
    METALLIC("Metálico", "Metallic paint with sparkle and color-shifting reflections"),
    AUTO("Auto-Detectar", "Automatically detect car finish type")
}

/**
 * Ângulo para guia de enquadramento.
 */
enum class PhotoAngle(val label: String, val description: String) {
    FRONT_THREE_QUARTER("Frente ¾", "Ângulo clássico de concessionária"),
    LEFT_SIDE("Lateral Esq", "Perfil lateral esquerdo completo"),
    RIGHT_SIDE("Lateral Dir", "Perfil lateral direito completo"),
    REAR("Traseira", "Vista traseira completa"),
    ANGLE_45("45°", "Ângulo dinâmico 45 graus"),
    FRONT("Frente", "Vista frontal direta")
}

/**
 * Tamanho de exportação.
 */
enum class ExportSize(val label: String, val maxDimension: Int, val quality: Int) {
    ORIGINAL_4K("4K Original", 4096, 100),
    MEDIUM_WHATSAPP("Médio (WhatsApp)", 1920, 85),
    THUMBNAIL_CRM("Thumbnail (CRM)", 512, 70)
}

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
    MODERN_GARAGE("Garagem Moderna", "Ultra-modern private garage with glass walls and minimalist design"),
    CUSTOM("Personalizado", "Custom dealership background")
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
 * V2.0 — Expandido com modo foto, batch, noturno, remoção de objetos, sombras, etc.
 */
data class EditOptions(
    val background: CarBackground = CarBackground.WHITE_SHOWROOM,
    val floor: CarFloor = CarFloor.WHITE_GLOSSY,
    val selectedStudioScene: StudioScene? = null,
    val isAdvancedMode: Boolean = false,

    // Qualidade
    val isUltraQuality: Boolean = false,
    val isDealershipMode: Boolean = true,
    val iterations: Int = 1,
    val removeReflections: Boolean = false,
    val isPhotographic: Boolean = true,
    val autoWhiteBalance: Boolean = true,
    val extremeSharpening: Boolean = false,
    val maxResolution: Int = 4096,

    // Modo de Foto (#12)
    val photoMode: PhotoMode = PhotoMode.EXTERIOR,
    val currentAngle: PhotoAngle = PhotoAngle.FRONT_THREE_QUARTER,

    // Batch (#3)
    val batchMode: Boolean = false,
    val batchCount: Int = 4,

    // Modo Noturno (#22)
    val nightMode: Boolean = false,
    val nightModeAutoDetect: Boolean = true,

    // Remoção de Objetos (#10)
    val removeUnwantedObjects: Boolean = true,

    // Sombras Automáticas (#9)
    val autoShadows: Boolean = true,

    // Refração Avançada (#8)
    val advancedGlassRefraction: Boolean = true,

    // Ajuste de Cor (#11)
    val autoColorAdjust: Boolean = true,
    val carFinish: CarFinish = CarFinish.AUTO,

    // Destaques (#20)
    val highlightAccessories: Boolean = false,

    // Background customizado (#19)
    val customBackgroundPath: String? = null,

    // SAM 2 Ultra Era (#2026)
    val isSam2UltraEnabled: Boolean = true
)
