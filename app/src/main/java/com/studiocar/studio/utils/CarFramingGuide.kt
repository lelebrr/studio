package com.studiocar.studio.utils

import android.graphics.RectF

/**
 * Lógica de análise de enquadramento inteligente para StudioCar.
 * V1.0 - Especialista em Hero Shot (45°).
 */
object CarFramingGuide {

    enum class CarType { SEDAN, SUV }

    enum class FramingStatus {
        TOO_HIGH,   // Carro muito no topo -> Abaixar câmera
        TOO_LOW,    // Carro muito na base -> Subir câmera
        PERFECT,    // Altura ideal (centro da roda/farol)
        NOT_FOUND   // Carro não detectado
    }

    data class AnalysisResult(
        val status: FramingStatus,
        val message: String,
        val arrow: String? = null
    )

    /**
     * Analisa a posição do carro na tela e sugere correções.
     * @param carBox Bounding box do carro (normalizado 0.0 a 1.0)
     * @param type Tipo do veículo para ajuste de "ideal height"
     */
    fun analyze(carBox: RectF?, type: CarType): AnalysisResult {
        if (carBox == null || carBox.isEmpty) {
            return AnalysisResult(FramingStatus.NOT_FOUND, "Buscando carro...")
        }

        // O centro vertical do carro deve estar próximo ao horizonte ideal
        // Para Sedans: centro do carro ~ 0.6 (levemente abaixo do centro da tela para dar imponência)
        // Para SUVs: centro do carro ~ 0.65 (um pouco mais baixo para compensar a altura do carro)
        
        val carCenterY = carBox.centerY()
        val idealCenterY = when (type) {
            CarType.SEDAN -> 0.60f
            CarType.SUV -> 0.65f
        }

        val tolerance = 0.05f

        return when {
            carCenterY < idealCenterY - tolerance -> {
                AnalysisResult(
                    FramingStatus.TOO_HIGH, 
                    "Pouco pra baixo ↓", 
                    "↓"
                )
            }
            carCenterY > idealCenterY + tolerance -> {
                AnalysisResult(
                    FramingStatus.TOO_LOW, 
                    "Pouco pra cima ↑", 
                    "↑"
                )
            }
            else -> {
                AnalysisResult(
                    FramingStatus.PERFECT, 
                    "Perfeito! 👍", 
                    null
                )
            }
        }
    }
}
