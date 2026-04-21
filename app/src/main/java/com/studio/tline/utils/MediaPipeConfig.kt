package com.studio.tline.utils

import com.google.mediapipe.tasks.vision.core.RunningMode

/**
 * Configurações globais para o MediaPipe Tasks Vision no T-Line Studio.
 */
object MediaPipeConfig {
    
    // Caminho do modelo nos assets
    const val MODEL_PATH = "deeplab_v3.tflite" // Melhor para carros
    
    // Configurações de Segmentação
    const val CONFIDENCE_THRESHOLD = 0.5f // Confiança mínima para o modelo
    const val MASK_THRESHOLD = 0.4f       // Threshold para a máscara binária
    
    // Modo de Execução (Prioriza IMAGE para alta qualidade)
    val RUNNING_MODE = RunningMode.IMAGE
    
    // Tipo de Saída (Category Mask é ideal para obter índices de classes)
    const val OUTPUT_TYPE_CATEGORY = true
    const val OUTPUT_TYPE_CONFIDENCE = false
    
    // Resolução e Performance
    const val MAX_IMAGE_SIZE = 4096 // Limite para evitar OOM (4K)
    const val DOWNSCALE_FOR_PROCESSING = 1024 // Resolução interna de processamento
    
    // Hardware Delegation
    const val USE_GPU = true // Tenta GPU primeiro, fallback automático para CPU
    
    // Logging
    const val LOG_TAG = "TLine_MediaPipe"
}
