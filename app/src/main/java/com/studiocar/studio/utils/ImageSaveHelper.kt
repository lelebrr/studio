package com.studiocar.studio.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.scale
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.studiocar.studio.data.models.ExportSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Motor de Salvamento de Alta Fidelidade StudioCar. 
 * V2.0 — Suporte a multi-tamanho, marca d'água e branding StudioCar.
 */
object ImageSaveHelper {

    /**
     * Carrega um Bitmap a partir de uma URI.
     */
    fun getBitmapFromUri(context: Context, uri: android.net.Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            android.graphics.BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Timber.e(e, "Erro ao carregar bitmap da URI")
            null
        }
    }

    /**
     * Salva em Qualidade Máxima (4K+) com metadados EXIF e Branding StudioCar.
     */
    suspend fun saveImageHighQuality(
        context: Context,
        bitmap: Bitmap,
        exportSize: ExportSize = ExportSize.ORIGINAL_4K,
        carModel: String? = null,
        carColor: String? = null,
        dealerName: String? = null,
        applyWatermark: Boolean = false
    ): String? = withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "studiocar_${timestamp}_${carModel ?: "car"}_${carColor ?: "color"}"
        val extension = if (exportSize == ExportSize.ORIGINAL_4K) "png" else "jpg"
        val mimeType = if (exportSize == ExportSize.ORIGINAL_4K) "image/png" else "image/jpeg"
        
        // Aplicar redimensionamento se necessário
        val processedBitmap = if (bitmap.width > exportSize.maxDimension || bitmap.height > exportSize.maxDimension) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val nw = if (ratio > 1) exportSize.maxDimension else (exportSize.maxDimension * ratio).toInt()
            val nh = if (ratio > 1) (exportSize.maxDimension / ratio).toInt() else exportSize.maxDimension
            bitmap.scale(nw, nh, true)
        } else bitmap

        // Aplicar Marca d'água (#5)
        val finalBitmap = if (applyWatermark) {
            applyWatermark(processedBitmap, dealerName ?: "StudioCar Professional")
        } else processedBitmap

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.$extension")
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StudioCar")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val uri = resolver.insert(collection, contentValues) ?: return@withContext null
            
            resolver.openOutputStream(uri).use { out ->
                if (out != null) {
                    val format = if (extension == "png") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                    finalBitmap.compress(format, exportSize.quality, out)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
                
                // Injeção de EXIF StudioCar
                resolver.openFileDescriptor(uri, "rw")?.use { pfd ->
                    val exif = ExifInterface(pfd.fileDescriptor)
                    val date = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).format(Date())
                    exif.setAttribute(ExifInterface.TAG_SOFTWARE, "StudioCar Pro Engine V1.1")
                    exif.setAttribute(ExifInterface.TAG_DATETIME, date)
                    exif.setAttribute(ExifInterface.TAG_ARTIST, dealerName ?: "StudioCar")
                    exif.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "Processed by StudioCar Professional AI Suite")
                    exif.setAttribute(ExifInterface.TAG_MODEL, Build.MODEL)
                    exif.setAttribute(ExifInterface.TAG_MAKE, Build.MANUFACTURER)
                    exif.saveAttributes()
                }
            }
            
            Timber.i("Imagem StudioCar salva com sucesso: $fileName")
            uri.toString()
        } catch (e: Exception) {
            Timber.e(e, "Erro ao salvar imagem StudioCar")
            null
        }
    }

    /**
     * Aplica uma marca d'água simples no canto inferior direito.
     */
    private fun applyWatermark(bitmap: Bitmap, text: String): Bitmap {
        val result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            color = android.graphics.Color.WHITE
            alpha = 180
            textSize = bitmap.height / 25f
            isAntiAlias = true
            setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
        }
        
        val margin = 40f
        val x = bitmap.width - paint.measureText(text) - margin
        val y = bitmap.height - margin
        
        canvas.drawText(text, x, y, paint)
        return result
    }

    /**
     * Salva na galeria com branding StudioCar.
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, name: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StudioCar")
            }
        }

        try {
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out!!)
                }
            }
            Timber.i("Imagem salva na galeria: $name")
        } catch (e: Exception) {
            Timber.e(e, "Erro ao salvar na galeria StudioCar")
        }
    }

    /**
     * Salva em arquivo privado cache para histórico/lote.
     */
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, name: String): String? {
        return try {
            val file = File(context.cacheDir, name)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            Timber.e(e, "Erro ao salvar arquivo cache StudioCar")
            null
        }
    }

    /**
     * Salva thumbnail leve para a lista de histórico.
     */
    suspend fun saveThumbnail(context: Context, bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, "thumbnails")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "thumb_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val nw = 512
                val nh = (512 / ratio).toInt()
                val thumb = bitmap.scale(nw, nh, true)
                thumb.compress(Bitmap.CompressFormat.JPEG, 70, out)
                thumb.recycle()
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}



