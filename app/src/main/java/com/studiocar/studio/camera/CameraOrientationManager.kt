package com.studiocar.studio.camera

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.*
import com.studiocar.studio.data.models.PhotoAngle
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.data.models.PhotoMode

/**
 * Gerencia a lógica inteligente de orientação da câmera para StudioCar.
 * Decide quando sugerir ou obrigar o modo landscape baseado no tipo de foto.
 */
class CameraOrientationManager(private val context: Context) {

    /**
     * Define se o ângulo atual exige modo landscape para melhor enquadramento.
     */
    fun isLandscapeRecommended(options: EditOptions): Boolean {
        // Se estiver em modo Batch (Sequência/360), landscape é recomendado para consistência profissional
        if (options.batchMode) return true

        // Se for modo Exterior, os ângulos amplos exigem modo horizontal
        if (options.photoMode == PhotoMode.EXTERIOR) {
            return when (options.currentAngle) {
                PhotoAngle.FRONT_THREE_QUARTER, // Hero Shot
                PhotoAngle.LEFT_SIDE,           // Side Profile
                PhotoAngle.RIGHT_SIDE,          // Side Profile
                PhotoAngle.REAR_THREE_QUARTER,  // Traseira 3/4
                PhotoAngle.REAR -> true
                else -> false
            }
        }
        
        // Fotos de detalhe longo (ex: lateral completa do carro ou painel)
        if (options.photoMode == PhotoMode.DETAIL) {
            // Em modo detalhe, podemos ter uma flag ou simplesmente sugerir para enquadramentos horizontais
            // Por enquanto, seguimos a orientação do usuário: "Detalhes Longos"
            return true 
        }

        return false
    }

    /**
     * Verifica a orientação atual do dispositivo.
     */
    @Composable
    fun rememberOrientation(): Int {
        val configuration = context.resources.configuration
        return configuration.orientation
    }

    /**
     * Determina se o aviso de rotação deve ser exibido.
     */
    fun shouldShowRotationWarning(options: EditOptions, currentOrientation: Int): Boolean {
        return isLandscapeRecommended(options) && currentOrientation == Configuration.ORIENTATION_PORTRAIT
    }
}
