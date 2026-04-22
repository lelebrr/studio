@file:Suppress("DEPRECATION")
package com.studiocar.studio.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

/**
 * Utilitário de segurança para chaves sensíveis (OpenRouter API Key).
 */
@Suppress("DEPRECATION")
class SecurityUtils(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "studiocar_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        context.getSharedPreferences("studiocar_secure_prefs_fallback", Context.MODE_PRIVATE)
    }

    fun saveApiKey(providerId: String, key: String) {
        sharedPreferences.edit { putString("api_key_$providerId", key) }
    }

    fun getApiKey(providerId: String): String? {
        return sharedPreferences.getString("api_key_$providerId", null)
    }

    // Mantém compatibilidade com código antigo enquanto migramos
    fun getOldApiKey(): String? {
        return sharedPreferences.getString("openrouter_api_key", null)
    }
}
