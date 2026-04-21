package com.studiocar.studio.data.models

/**
 * Configurações profissionais de câmera para o StudioCar.
 * Simula controles de uma câmera DSLR/Estúdio.
 */
data class CameraSettings(
    val iso: Int = 100, // 100 a 3200
    val shutterSpeedNanos: Long = 16_666_666L, // 1/60s padrão em nanosegundos
    val exposureCompensation: Float = 0f, // -3.0f a +3.0f
    val whiteBalanceTemp: Int = 5500, // 2500K a 10000K
    val isManualFocus: Boolean = false,
    val meteringMode: MeteringMode = MeteringMode.MATRIX,
    val resolution: CameraResolution = CameraResolution.RES_4K,
    val quality: CameraQuality = CameraQuality.MAXIMUM,
    val timerSeconds: Int = 0, // 0 (desligado), 3, 5, 10
    val gridType: GridType = GridType.RULE_OF_THIRDS,
    val showHistogram: Boolean = true
)

enum class MeteringMode(val label: String) {
    MATRIX("Matrix"),
    CENTER_WEIGHTED("Center-weighted"),
    SPOT("Spot")
}

enum class CameraResolution(val label: String, val width: Int, val height: Int) {
    RES_4K("4K Ultra HD", 3840, 2160),
    RES_6K("6K Pro", 6144, 3160),
    RES_8K("8K Cinema", 7680, 4320)
}

enum class CameraQuality(val label: String) {
    HIGH("Alta"),
    MAXIMUM("Máxima")
}

enum class GridType(val label: String) {
    NONE("Nenhum"),
    RULE_OF_THIRDS("Regra dos Terços"),
    GOLDEN_RATIO("Proporção Áurea"),
    CENTER("Centro")
}
