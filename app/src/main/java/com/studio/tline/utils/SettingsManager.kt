package com.studio.tline.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tline_settings")

/**
 * Gerenciador de configurações B2B do Studio Pro.
 */
class SettingsManager(private val context: Context) {

    companion object {
        private val API_KEY = stringPreferencesKey("openrouter_api_key")
        private val DEFAULT_MODEL = stringPreferencesKey("default_model")
        private val AUTO_SAVE = booleanPreferencesKey("auto_save_vendas")
        private val RES_LIMIT = intPreferencesKey("resolution_limit")
        private val DEMO_MODE = booleanPreferencesKey("is_demo_mode")
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        
        // Parâmetros FLUX.1 (Passo 13/14)
        private val FLUX_STRENGTH = floatPreferencesKey("flux_strength")
        private val FLUX_STEPS = intPreferencesKey("flux_steps")
        private val FLUX_GUIDANCE = floatPreferencesKey("flux_guidance")
        private val PIPELINE_MODE = stringPreferencesKey("pipeline_mode") // Hybrid, Nano, Flux
        private val MULTI_PASS_ENABLED = booleanPreferencesKey("multi_pass_enabled")
        private val IS_ADVANCED_MODE = booleanPreferencesKey("is_advanced_mode")
        private val USE_PRO_MODELS = booleanPreferencesKey("use_pro_models")
    }

    // Fluxos de dados
    val apiKey: Flow<String?> = context.dataStore.data.map { it[API_KEY] }
    val defaultModel: Flow<String> = context.dataStore.data.map { it[DEFAULT_MODEL] ?: "google/gemini-3.1-flash-image-preview" }
    val autoSave: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SAVE] ?: true }
    val resLimit: Flow<Int> = context.dataStore.data.map { it[RES_LIMIT] ?: 3072 }
    val isDemoMode: Flow<Boolean> = context.dataStore.data.map { it[DEMO_MODE] ?: false }
    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map { it[HAS_SEEN_ONBOARDING] ?: true }

    // FLUX Params B2B Elite Defaults
    val fluxStrength: Flow<Float> = context.dataStore.data.map { it[FLUX_STRENGTH] ?: 0.40f }
    val fluxSteps: Flow<Int> = context.dataStore.data.map { it[FLUX_STEPS] ?: 35 }
    val fluxGuidance: Flow<Float> = context.dataStore.data.map { it[FLUX_GUIDANCE] ?: 3.8f }
    val pipelineMode: Flow<String> = context.dataStore.data.map { it[PIPELINE_MODE] ?: "Hybrid" }
    val multiPassEnabled: Flow<Boolean> = context.dataStore.data.map { it[MULTI_PASS_ENABLED] ?: true }
    val isAdvancedMode: Flow<Boolean> = context.dataStore.data.map { it[IS_ADVANCED_MODE] ?: false }
    val useProModels: Flow<Boolean> = context.dataStore.data.map { it[USE_PRO_MODELS] ?: true }

    // Funções de escrita
    suspend fun setApiKey(key: String) {
        context.dataStore.edit { it[API_KEY] = key }
    }

    suspend fun setDefaultModel(model: String) {
        context.dataStore.edit { it[DEFAULT_MODEL] = model }
    }

    suspend fun setAutoSave(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_SAVE] = enabled }
    }

    suspend fun setResLimit(limit: Int) {
        context.dataStore.edit { it[RES_LIMIT] = limit }
    }

    suspend fun setDemoMode(enabled: Boolean) {
        context.dataStore.edit { it[DEMO_MODE] = enabled }
    }

    suspend fun setOnboardingSeen(seen: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_ONBOARDING] = seen }
    }

    suspend fun setFluxParams(strength: Float, steps: Int, guidance: Float, multiPass: Boolean) {
        context.dataStore.edit {
            it[FLUX_STRENGTH] = strength
            it[FLUX_STEPS] = steps
            it[FLUX_GUIDANCE] = guidance
            it[MULTI_PASS_ENABLED] = multiPass
        }
    }

    suspend fun setPipelineMode(mode: String) {
        context.dataStore.edit { it[PIPELINE_MODE] = mode }
    }

    suspend fun setAdvancedMode(enabled: Boolean) {
        context.dataStore.edit { it[IS_ADVANCED_MODE] = enabled }
    }

    suspend fun setUseProModels(enabled: Boolean) {
        context.dataStore.edit { it[USE_PRO_MODELS] = enabled }
    }
}
