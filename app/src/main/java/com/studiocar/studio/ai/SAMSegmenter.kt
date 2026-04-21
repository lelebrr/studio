package com.studiocar.studio.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.Collections

/**
 * SAMSegmenter.kt - Motor de Segmentação SAM 2 (Segment Anything Model 2).
 * Especializado em recorte de carros com alta precisão e suporte a prompts.
 * QUALIDADE MÁXIMA - O carro deve parecer que realmente está dentro do estúdio
 */
class SAMSegmenter(private val context: Context) {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private var encoderSession: OrtSession? = null
    private var decoderSession: OrtSession? = null
    private var isInitialized = false

    companion object {
        private const val ENCODER_MODEL = "sam2_hiera_tiny_encoder.onnx"
        private const val DECODER_MODEL = "sam2_hiera_tiny_decoder.onnx"
        private const val INPUT_SIZE = 1024 // SAM 2 padrão
    }

    /**
     * Inicializa as sessões do ONNX Runtime.
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext true
        try {
            val sessionOptions = OrtSession.SessionOptions().apply {
                setIntraOpNumThreads(4)
                setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
                // Opcional: Adicionar NNAPI para aceleração hardware
                // addNnapi() 
            }

            encoderSession = env.createSession(loadModel(ENCODER_MODEL), sessionOptions)
            decoderSession = env.createSession(loadModel(DECODER_MODEL), sessionOptions)
            
            isInitialized = true
            Timber.i("SAM 2 Ultra Engine Inicializado com sucesso.")
            true
        } catch (e: Exception) {
            Timber.e(e, "Erro ao carregar modelos SAM 2")
            false
        }
    }

    private fun loadModel(fileName: String): ByteArray {
        return context.assets.open(fileName).readBytes()
    }

    /**
     * Segmenta o carro usando uma pipeline híbrida.
     * @param bitmap Imagem original.
     * @param points Lista de pontos de prompt (x, y, label where 1 is foreground, 0 is background).
     * @param box Bounding box opcional [x, y, x2, y2].
     */
    suspend fun segment(
        bitmap: Bitmap,
        points: List<Pair<FloatArray, IntArray>>? = null,
        box: FloatArray? = null
    ): Bitmap? = withContext(Dispatchers.Default) {
        if (!isInitialized) initialize()
        if (!isInitialized) return@withContext null

        try {
            val startTime = System.currentTimeMillis()
            
            // 1. Preprocessamento (Resize 1024x1024 + Normalização)
            val resized = bitmap.scale(INPUT_SIZE, INPUT_SIZE, true)
            val inputTensor = bitmapToFloatBuffer(resized)
            
            // 2. Run Encoder (Geração de Embeddings)
            val encoderInput = mapOf("image" to OnnxTensor.createTensor(env, inputTensor, longArrayOf(1, 3, INPUT_SIZE.toLong(), INPUT_SIZE.toLong())))
            val encoderOutput = encoderSession?.run(encoderInput)
            val imageEmbeddings = encoderOutput?.get(0) as? OnnxTensor ?: return@withContext null

            // 3. Prepare Prompts for Decoder
            // Se não houver prompt, usamos o centro como default (baseado na lógica Ultra)
            val finalPoints = points ?: listOf(floatArrayOf(0.5f * INPUT_SIZE, 0.5f * INPUT_SIZE) to intArrayOf(1))
            
            // 4. Run Decoder
            val decoderResult = runDecoder(imageEmbeddings, finalPoints, box)
            
            val totalTime = System.currentTimeMillis() - startTime
            Timber.i("SAM 2 Inferência concluída em ${totalTime}ms")

            decoderResult
        } catch (e: Exception) {
            Timber.e(e, "Erro durante a segmentação SAM 2")
            null
        }
    }

    private fun runDecoder(
        imageEmbeddings: OnnxTensor,
        points: List<Pair<FloatArray, IntArray>>,
        box: FloatArray?
    ): Bitmap? {
        val numPoints = points.size + (if (box != null) 2 else 0)
        val pointCoords = FloatBuffer.allocate(numPoints * 2)
        val pointLabels = IntBuffer.allocate(numPoints)

        // Add touch points
        points.forEach { (coord, label) ->
            pointCoords.put(coord)
            pointLabels.put(label)
        }

        // Add box points if available
        box?.let {
            pointCoords.put(it[0]); pointCoords.put(it[1]) // Top-left
            pointLabels.put(2) // Label 2 usually means box corner
            pointCoords.put(it[2]); pointCoords.put(it[3]) // Bottom-right
            pointLabels.put(3) 
        }

        pointCoords.rewind()
        pointLabels.rewind()

        val decoderInputs = mutableMapOf<String, OnnxTensor>()
        decoderInputs["image_embeddings"] = imageEmbeddings
        decoderInputs["point_coords"] = OnnxTensor.createTensor(env, pointCoords, longArrayOf(1, numPoints.toLong(), 2))
        decoderInputs["point_labels"] = OnnxTensor.createTensor(env, pointLabels, longArrayOf(1, numPoints.toLong()))
        
        // Máscara vazia inicial (placeholder)
        val emptyMask = FloatBuffer.allocate(1 * 1 * 256 * 256)
        decoderInputs["mask_input"] = OnnxTensor.createTensor(env, emptyMask, longArrayOf(1, 1, 256, 256))
        decoderInputs["has_mask_input"] = OnnxTensor.createTensor(env, FloatBuffer.wrap(floatArrayOf(0f)), longArrayOf(1))

        val decoderOutput = decoderSession?.run(decoderInputs)
        val masks = decoderOutput?.get("masks")?.get() as? OnnxTensor ?: return null
        
        return maskTensorToBitmap(masks)
    }

    private fun maskTensorToBitmap(maskTensor: OnnxTensor): Bitmap {
        val shape = maskTensor.info.shape // Expecting [1, 1, 256, 256] or similar
        val width = shape[3].toInt()
        val height = shape[2].toInt()
        val floatData = maskTensor.floatBuffer
        
        val maskBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        
        for (i in 0 until width * height) {
            val logit = floatData.get(i)
            // Logit > 0 significa objeto
            pixels[i] = if (logit > 0) Color.WHITE else Color.TRANSPARENT
        }
        
        maskBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return maskBitmap
    }

    private fun bitmapToFloatBuffer(bitmap: Bitmap): FloatBuffer {
        val buffer = FloatBuffer.allocate(1 * 3 * INPUT_SIZE * INPUT_SIZE)
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        // Normalização Standard (0-1) - Ajustar se o modelo exigir ImageNet
        for (c in 0 until 3) {
            for (p in pixels.indices) {
                val color = pixels[p]
                val value = when(c) {
                    0 -> Color.red(color)
                    1 -> Color.green(color)
                    else -> Color.blue(color)
                } / 255f
                buffer.put(value)
            }
        }
        buffer.rewind()
        return buffer
    }

    fun release() {
        encoderSession?.close()
        decoderSession?.close()
        isInitialized = false
    }
}
