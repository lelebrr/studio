package com.studiocar.studio.data.models

/**
 * Modelo de dados para ajustes de imagem profissionais.
 */
data class ImageAdjustments(
    val brightness: Float = 0f,      // -100 a 100
    val contrast: Float = 1f,        // 0.5 a 1.5
    val saturation: Float = 1f,      // 0.0 a 2.0
    val exposure: Float = 0f,        // -2.0 a 2.0
    val temperature: Float = 0f,     // -100 (Frio) a 100 (Quente)
    val shadows: Float = 0f,         // -100 a 100
    val highlights: Float = 0f,      // -100 a 100
    val sharpen: Float = 0f,         // 0 a 100
    val clarity: Float = 0f          // 0 a 100
)

/**
 * Estilos de iluminação direcional para simulação de estúdio.
 */
enum class DirectionalLightStyle(val label: String) {
    SOFT_FRONT("Luz Suave Frontal"),
    SIDE_LEFT("Luz Lateral Esquerda"),
    SIDE_RIGHT("Luz Lateral Direita"),
    DRAMATIC_45("Luz Dramática (45°)"),
    TOP_STUDIO("Luz de Topo (Estúdio)"),
    NATURAL_WINDOW("Luz Natural de Janela")
}
