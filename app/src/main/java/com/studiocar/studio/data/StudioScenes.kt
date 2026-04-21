package com.studiocar.studio.data

import com.studiocar.studio.data.models.StudioScene
import com.studiocar.studio.data.models.SceneCategory

object StudioScenes {
    val allScenes = listOf(
        // --- Showrooms Premium (8) ---
        StudioScene(
            id = "showroom_white_minimal",
            name = "Showroom Branco Minimalista",
            imageAsset = "studio_scenes/showroom_white_minimal.png",
            category = SceneCategory.SHOWROOM,
            isRecommended = true
        ),
        StudioScene(
            id = "showroom_grey_modern",
            name = "Showroom Cinza Claro Moderno",
            imageAsset = "studio_scenes/showroom_gray_modern.png",
            category = SceneCategory.SHOWROOM
        ),
        StudioScene(
            id = "showroom_black_luxury",
            name = "Showroom Preto Luxo",
            imageAsset = "studio_scenes/showroom_black_luxury.png",
            category = SceneCategory.SHOWROOM,
            isPremium = true
        ),
        StudioScene(
            id = "showroom_beige_elegant",
            name = "Showroom Bege Elegante",
            imageAsset = "studio_scenes/showroom_beige_elegant.png",
            category = SceneCategory.SHOWROOM
        ),
        StudioScene(
            id = "showroom_white_led",
            name = "Showroom Branco com Luzes LED",
            imageAsset = "studio_scenes/showroom_white_led.png",
            category = SceneCategory.SHOWROOM,
            isRecommended = true
        ),
        StudioScene(
            id = "showroom_reflexive_floor",
            name = "Showroom com Piso Reflexivo",
            imageAsset = "studio_scenes/showroom_reflexive.png",
            category = SceneCategory.SHOWROOM
        ),
        StudioScene(
            id = "showroom_textured_wall",
            name = "Showroom com Parede Texturizada",
            imageAsset = "studio_scenes/showroom_gray_modern.png",
            category = SceneCategory.SHOWROOM
        ),
        StudioScene(
            id = "showroom_indirect_light",
            name = "Showroom com Teto de Luz Indireta",
            imageAsset = "studio_scenes/showroom_white_led.png",
            category = SceneCategory.SHOWROOM
        ),

        // --- Estúdios Fotográficos (8) ---
        StudioScene(
            id = "studio_cyclorama_white",
            name = "Estúdio Ciclorama Branco Infinito",
            imageAsset = "studio_scenes/showroom_white_minimal.png",
            category = SceneCategory.STUDIO,
            isRecommended = true
        ),
        StudioScene(
            id = "studio_grey_pro",
            name = "Estúdio Cinza Escuro Profissional",
            imageAsset = "studio_scenes/showroom_black_luxury.png",
            category = SceneCategory.STUDIO
        ),
        StudioScene(
            id = "studio_concrete_polished",
            name = "Estúdio com Piso de Concreto Polido",
            imageAsset = "studio_scenes/showroom_gray_modern.png",
            category = SceneCategory.STUDIO
        ),
        StudioScene(
            id = "studio_dark_wood",
            name = "Estúdio com Piso de Madeira Escura",
            imageAsset = "studio_scenes/showroom_black_luxury.png",
            category = SceneCategory.STUDIO
        ),
        StudioScene(
            id = "studio_curved_backdrop",
            name = "Estúdio com Backdrop Curvo",
            imageAsset = "studio_scenes/showroom_white_minimal.png",
            category = SceneCategory.STUDIO
        ),
        StudioScene(
            id = "studio_softbox_lights",
            name = "Estúdio com Luzes de Studio (Softbox)",
            imageAsset = "studio_scenes/showroom_white_led.png",
            category = SceneCategory.STUDIO,
            isRecommended = true
        ),
        StudioScene(
            id = "studio_black_ceiling_white_floor",
            name = "Estúdio com Teto Preto e Piso Branco",
            imageAsset = "studio_scenes/showroom_reflexive.png",
            category = SceneCategory.STUDIO
        ),
        StudioScene(
            id = "studio_glossy_epoxy",
            name = "Estúdio com Piso Epóxi Brilhante",
            imageAsset = "studio_scenes/showroom_reflexive.png",
            category = SceneCategory.STUDIO
        ),

        // --- Ambientes Luxuosos (8) ---
        StudioScene(
            id = "luxury_garage_modern",
            name = "Garagem de Luxo Moderna",
            imageAsset = "studio_scenes/showroom_black_luxury.png",
            category = SceneCategory.LUXURY,
            isRecommended = true
        ),
        StudioScene(
            id = "luxury_hall_columns",
            name = "Hall de Exposição com Colunas",
            imageAsset = "studio_scenes/showroom_beige_elegant.png",
            category = SceneCategory.LUXURY
        ),
        StudioScene(
            id = "luxury_glass_wall",
            name = "Ambiente com Parede de Vidro (Daylight)",
            imageAsset = "studio_scenes/showroom_white_minimal.png",
            category = SceneCategory.LUXURY,
            isRecommended = true
        ),
        StudioScene(
            id = "luxury_wood_leather",
            name = "Estúdio com Detalhes em Madeira e Couro",
            imageAsset = "studio_scenes/showroom_beige_elegant.png",
            category = SceneCategory.LUXURY
        ),
        StudioScene(
            id = "luxury_japanese_minimalist",
            name = "Ambiente Minimalista Japonês",
            imageAsset = "studio_scenes/showroom_white_minimal.png",
            category = SceneCategory.LUXURY
        ),
        StudioScene(
            id = "luxury_sand_beige",
            name = "Estúdio com Tons de Areia e Bege",
            imageAsset = "studio_scenes/showroom_beige_elegant.png",
            category = SceneCategory.LUXURY
        ),
        StudioScene(
            id = "luxury_dramatic_light",
            name = "Ambiente com Iluminação Dramática",
            imageAsset = "studio_scenes/showroom_black_luxury.png",
            category = SceneCategory.LUXURY
        ),
        StudioScene(
            id = "luxury_high_end_dark",
            name = "Estúdio High-End com Tons Escuros",
            imageAsset = "studio_scenes/showroom_black_luxury.png",
            category = SceneCategory.LUXURY,
            isPremium = true
        )
    )
}
