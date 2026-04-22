package com.studiocar.studio.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.studiocar.studio.R
import com.studiocar.studio.data.models.PhotoAngle
import com.studiocar.studio.data.models.VehicleType

/**
 * Gerencia e renderiza as silhuetas de guia na tela da câmera
 * baseadas no tipo de veículo detectado e o ângulo atual.
 */
object VehicleSilhouetteManager {

    /**
     * Componente Composable que exibe a silhueta em cima da câmera.
     */
    @Composable
    fun SilhouetteOverlay(
        type: VehicleType?,
        angle: PhotoAngle,
        modifier: Modifier = Modifier
    ) {
        // Se nenhum tipo foi detectado ainda, podemos usar SEDAN como fallback padrão
        val currentType = type ?: VehicleType.SEDAN
        
        val resourceId = getSilhouetteResource(currentType, angle)
        
        AnimatedVisibility(
            visible = resourceId != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = modifier.fillMaxSize()
        ) {
            resourceId?.let { resId ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Nota: O ideal é ajustar o padding/escala dependendo do ângulo.
                    // Silhuetas geralmente ocupam o centro da tela.
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Guia de Silhueta",
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(32.dp).fillMaxSize(0.8f)
                    )
                }
            }
        }
    }

    /**
     * Retorna o ID de drawable correspondente para a combinação.
     * Estes são placeholders até que os assets reais sejam adicionados ao projeto.
     * TODO: Adicionar os drawables reais na pasta res/drawable/
     */
    private fun getSilhouetteResource(type: VehicleType, angle: PhotoAngle): Int? {
        // Mapeando PhotoAngle (que tem vários ângulos) para a lógica de "FRENTE, LATERAL, TRASEIRA"
        val isFront = angle == PhotoAngle.FRONT_THREE_QUARTER || angle == PhotoAngle.FRONT || angle == PhotoAngle.ANGLE_45
        val isRear = angle == PhotoAngle.REAR_THREE_QUARTER || angle == PhotoAngle.REAR
        val isSide = angle == PhotoAngle.LEFT_SIDE || angle == PhotoAngle.RIGHT_SIDE
        
        // Se não for um dos principais ângulos externos, não mostramos silhueta
        if (!isFront && !isRear && !isSide) return null

        // Mapeamento usando o tipo de veículo e ângulo
        // Nota: Atualmente todos retornam ic_launcher_background como placeholder
        // até que os assets específicos (ex: silhouette_sedan_front) sejam adicionados.
        return when (type) {
            VehicleType.HATCH, VehicleType.SEDAN, VehicleType.SUV, 
            VehicleType.PICKUP, VehicleType.CROSSOVER, VehicleType.MINIVAN, 
            VehicleType.STATION_WAGON, VehicleType.COUPE -> {
                R.drawable.ic_launcher_background
            }
        }
    }
}
