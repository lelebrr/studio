package com.studiocar.studio.data.models

/**
 * StudioScene V1.0 - Representa um cenário completo de estúdio.
 * Diferente de apenas um fundo, a cena inclui chão, paredes e iluminação integrada.
 */
data class StudioScene(
    val id: String,
    val name: String,
    val imageAsset: String, // Caminho na pasta assets/studio_scenes/
    val category: SceneCategory,
    val isPremium: Boolean = true,
    val isRecommended: Boolean = false
)

enum class SceneCategory(val label: String) {
    SHOWROOM("Showroom Premium"),
    STUDIO("Estúdio Fotográfico"),
    LUXURY("Ambiente Luxuoso")
}
