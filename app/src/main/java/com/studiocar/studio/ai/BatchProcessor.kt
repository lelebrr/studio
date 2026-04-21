package com.studiocar.studio.ai

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.data.models.EditOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * BatchProcessor V1.0 - StudioCar Fila de Elite.
 * Gerencia o processamento de múltiplas imagens em segundo plano.
 */
object BatchProcessor {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val queue = ConcurrentLinkedQueue<BatchTask>()
    
    private val _isBusy = MutableStateFlow(false)
    val isBusy = _isBusy.asStateFlow()

    private val _remainingTasks = MutableStateFlow(0)
    val remainingTasks = _remainingTasks.asStateFlow()

    private var workerJob: Job? = null

    data class BatchTask(
        val bitmap: Bitmap,
        val options: EditOptions,
        val onComplete: (Bitmap) -> Unit,
        val onError: (Exception) -> Unit
    )

    /**
     * Adiciona uma tarefa à fila de processamento.
     */
    fun enqueue(context: Context, bitmap: Bitmap, options: EditOptions, onComplete: (Bitmap) -> Unit, onError: (Exception) -> Unit) {
        queue.add(BatchTask(bitmap, options, onComplete, onError))
        _remainingTasks.value = queue.size
        startWorkerIfNecessary(context.applicationContext)
    }

    private fun startWorkerIfNecessary(context: Context) {
        if (workerJob != null && workerJob?.isActive == true) return

        workerJob = scope.launch {
            _isBusy.value = true
            val service = ImageEditorService(context)
            
            while (queue.isNotEmpty()) {
                val task = queue.poll()
                _remainingTasks.value = queue.size
                
                if (task != null) {
                    try {
                        Timber.i("Processando tarefa em lote...")
                        val result = service.processCarPhoto(task.bitmap, task.options)
                        if (result != null) {
                            task.onComplete(result)
                        } else {
                            task.onError(Exception("Falha no processamento IA"))
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Erro no processamento em lote")
                        task.onError(e)
                    }
                }
            }
            
            _isBusy.value = false
            Timber.i("Processamento em lote concluído")
        }
    }

    fun stopAll() {
        workerJob?.cancel()
        queue.clear()
        _remainingTasks.value = 0
        _isBusy.value = false
    }
}
