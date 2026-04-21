package com.studio.tline.utils

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton de Telemetria V9.0.
 * Armazena métricas e estágios visuais para o Laboratório de Testes.
 */
object DebugDataManager {

    data class ExecutionStats(
        val mediaPipeTime: Long = 0,
        val geminiPass1Time: Long = 0,
        val geminiPass2Time: Long = 0,
        val stableDiffusionTime: Long = 0,
        val postProcessTime: Long = 0,
        val totalTime: Long = 0,
        val initialResolution: String = "",
        val finalResolution: String = "",
        val memoryUsageMb: Long = 0,
        val qualityScore: Int = 0, // 0-100
        val burrPresenceScore: Int = 0, // 0-100 (menor é melhor)
        
        // Estágios Intermediários para TestScreen
        var stageMask: Bitmap? = null,
        var stageGemini: Bitmap? = null,
        var stageFinal: Bitmap? = null
    )

    // Stage Tracking for Pipeline V2026
    var maskBitmap: Bitmap? = null
    var geminiBitmap: Bitmap? = null
    var finalBitmap: Bitmap? = null

    private val _lastExecutionStats = MutableStateFlow<ExecutionStats?>(null)
    val lastExecutionStats = _lastExecutionStats.asStateFlow()

    fun updateStats(stats: ExecutionStats) {
        _lastExecutionStats.value = stats
    }

    fun getMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    }

    /**
     * Limpa bitmaps da memória quando não necessários.
     */
    fun clearIntermediateStages() {
        _lastExecutionStats.value?.let { 
            it.stageMask?.recycle()
            it.stageGemini?.recycle()
            it.stageFinal?.recycle()
        }
    }
}
