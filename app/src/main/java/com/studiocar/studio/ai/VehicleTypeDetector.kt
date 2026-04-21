package com.studiocar.studio.ai

import androidx.camera.core.ImageProxy
import com.studiocar.studio.data.models.VehicleType
import timber.log.Timber

/**
 * Responsável por processar o feed da câmera e identificar o tipo de veículo.
 * Atualmente implementa uma estrutura pronta para integração com ML Kit ou
 * modelo TFLite customizado (vehicle_type.tflite).
 */
class VehicleTypeDetector {

    // Simulação: manter o estado do último veículo detectado para evitar flutuações rápidas
    private var lastDetectedType: VehicleType? = null
    private var confidenceFrames = 0
    private val confidenceThreshold = 5

    /**
     * Processa a imagem para detectar o tipo de veículo.
     * Retorna o VehicleType detectado, ou nulo se nenhum veículo for reconhecido.
     */
    @Suppress("UNUSED_PARAMETER")
    fun processImage(imageProxy: ImageProxy, onTypeDetected: (VehicleType?) -> Unit) {
        try {
            // Em uma implementação real com ML Kit Object Detection ou TFLite:
            // 1. Converter ImageProxy para InputImage (ML Kit) ou ByteBuffer (TFLite)
            // val image = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            // 2. Passar pelo modelo
            // 3. Obter a label com maior probabilidade
            
            // Simulação (Mock)
            // Vamos simular uma detecção de SEDAN para testar a interface.
            // Em produção, isso virá da inferência do modelo TFLite.
            val mockDetected = VehicleType.SEDAN 
            
            // Lógica de estabilização da detecção (só atualiza após X frames de confiança)
            if (mockDetected == lastDetectedType) {
                confidenceFrames++
            } else {
                lastDetectedType = mockDetected
                confidenceFrames = 1
            }

            if (confidenceFrames >= confidenceThreshold) {
                onTypeDetected(lastDetectedType)
            } else {
                onTypeDetected(null) // Ainda detectando...
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Erro na detecção do tipo de veículo")
        } finally {
            // Importante: NÃO fechar o imageProxy aqui, pois ele pode ser usado
            // pelo VIN Scanner, Night Mode Detector ou Histograma em seguida.
            // Quem coordena o fechamento é o ImageAnalysis.setAnalyzer principal.
        }
    }
}
