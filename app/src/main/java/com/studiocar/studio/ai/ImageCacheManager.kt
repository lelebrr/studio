package com.studiocar.studio.ai

import android.graphics.Bitmap
import android.util.LruCache

/**
 * Cache de Bitmaps para a pipeline StudioCar.
 */
object ImageCacheManager {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 4 // 25% da memória disponível

    private val cache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
        
        override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Bitmap?, newValue: Bitmap?) {
            // Se necessário, reciclar aqui, mas cuidado com referências ativas
        }
    }

    fun put(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }

    fun get(key: String): Bitmap? {
        return cache.get(key)
    }

    fun clear() {
        cache.evictAll()
    }
}
