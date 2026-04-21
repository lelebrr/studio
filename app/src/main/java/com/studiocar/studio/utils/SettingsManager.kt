package com.studiocar.studio.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "studiocar_settings")

/**
 * Gerenciador de configurações B2B do StudioCar.
 * V2.0 — Expandido com branding, vendedor, modo offline, backgrounds, etc.
 */
class SettingsManager(private val context: Context) {

    companion object {
        // API
        private val API_KEY = stringPreferencesKey("openrouter_api_key")
        private val DEFAULT_MODEL = stringPreferencesKey("default_model")

        // Estúdio
        private val AUTO_SAVE = booleanPreferencesKey("auto_save_vendas")
        private val RES_LIMIT = intPreferencesKey("resolution_limit")
        private val DEMO_MODE = booleanPreferencesKey("is_demo_mode")
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")

        // FLUX
        private val FLUX_STRENGTH = floatPreferencesKey("flux_strength")
        private val FLUX_STEPS = intPreferencesKey("flux_steps")
        private val FLUX_GUIDANCE = floatPreferencesKey("flux_guidance")
        private val PIPELINE_MODE = stringPreferencesKey("pipeline_mode")
        private val MULTI_PASS_ENABLED = booleanPreferencesKey("multi_pass_enabled")
        private val IS_ADVANCED_MODE = booleanPreferencesKey("is_advanced_mode")
        private val USE_PRO_MODELS = booleanPreferencesKey("use_pro_models")

        // Branding (#5)
        private val DEALERSHIP_NAME = stringPreferencesKey("dealership_name")
        private val DEALERSHIP_LOGO_PATH = stringPreferencesKey("dealership_logo_path")
        private val WATERMARK_ENABLED = booleanPreferencesKey("watermark_enabled")
        private val IS_BATCH_MODE = booleanPreferencesKey("is_batch_mode")
        private val BATCH_COUNT = intPreferencesKey("batch_count")

        // Vendedor (#15)
        private val CURRENT_VENDOR_ID = stringPreferencesKey("current_vendor_id")
        private val CURRENT_VENDOR_NAME = stringPreferencesKey("current_vendor_name")

        // Modo Offline (#7)
        private val OFFLINE_MODE = booleanPreferencesKey("offline_demo_mode")

        // Exportação (#16)
        private val DEFAULT_EXPORT_SIZE = stringPreferencesKey("default_export_size")

        // Modo Noturno (#22)
        private val NIGHT_MODE_AUTO = booleanPreferencesKey("night_mode_auto_detect")

        // Backgrounds Personalizados (#19)
        private val CUSTOM_BG_PATHS = stringSetPreferencesKey("custom_background_paths")

        // Guia de Enquadramento Inteligente (#24)
        private val SMART_FRAMING_ENABLED = booleanPreferencesKey("smart_framing_enabled")
        private val PREFERRED_CAR_TYPE = stringPreferencesKey("preferred_car_type")

        // Configurações Pro de Câmera
        private val CAM_ISO = intPreferencesKey("cam_iso")
        private val CAM_SHUTTER = longPreferencesKey("cam_shutter")
        private val CAM_EV = floatPreferencesKey("cam_ev")
        private val CAM_WB = intPreferencesKey("cam_wb")
        private val CAM_MANUAL_FOCUS = booleanPreferencesKey("cam_manual_focus")
        private val CAM_METERING = stringPreferencesKey("cam_metering")
        private val CAM_RES = stringPreferencesKey("cam_res")
        private val CAM_QUALITY = stringPreferencesKey("cam_quality")
        private val CAM_TIMER = intPreferencesKey("cam_timer")
        private val CAM_GRID = stringPreferencesKey("cam_grid")
        private val CAM_HISTOGRAM = booleanPreferencesKey("cam_histogram")

        // Multi-Provider IA
        private val PRIMARY_AI_PROVIDER = stringPreferencesKey("primary_ai_provider")
        private val ACTIVE_AI_PROVIDERS = stringSetPreferencesKey("active_ai_providers")
        private val AI_FALLBACK_ORDER = stringSetPreferencesKey("ai_fallback_order")
        private val AUTO_FALLBACK_ENABLED = booleanPreferencesKey("auto_fallback_enabled")
    }

    // === Fluxos de leitura ===

    // API
    val apiKey: Flow<String?> = context.dataStore.data.map { it[API_KEY] }
    val defaultModel: Flow<String> = context.dataStore.data.map { it[DEFAULT_MODEL] ?: "google/gemini-3.1-flash-image-preview" }

    // Estúdio
    val autoSave: Flow<Boolean> = context.dataStore.data.map { it[AUTO_SAVE] ?: true }
    val resLimit: Flow<Int> = context.dataStore.data.map { it[RES_LIMIT] ?: 3072 }
    val isDemoMode: Flow<Boolean> = context.dataStore.data.map { it[DEMO_MODE] ?: false }
    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data.map { it[HAS_SEEN_ONBOARDING] ?: true }

    // FLUX
    val fluxStrength: Flow<Float> = context.dataStore.data.map { it[FLUX_STRENGTH] ?: 0.40f }
    val fluxSteps: Flow<Int> = context.dataStore.data.map { it[FLUX_STEPS] ?: 35 }
    val fluxGuidance: Flow<Float> = context.dataStore.data.map { it[FLUX_GUIDANCE] ?: 3.8f }
    val pipelineMode: Flow<String> = context.dataStore.data.map { it[PIPELINE_MODE] ?: "Hybrid" }
    val multiPassEnabled: Flow<Boolean> = context.dataStore.data.map { it[MULTI_PASS_ENABLED] ?: true }
    val isAdvancedMode: Flow<Boolean> = context.dataStore.data.map { it[IS_ADVANCED_MODE] ?: false }
    val useProModels: Flow<Boolean> = context.dataStore.data.map { it[USE_PRO_MODELS] ?: true }

    // Branding (#5)
    val dealershipName: Flow<String> = context.dataStore.data.map { it[DEALERSHIP_NAME] ?: "" }
    val dealershipLogoPath: Flow<String?> = context.dataStore.data.map { it[DEALERSHIP_LOGO_PATH] }
    val watermarkEnabled: Flow<Boolean> = context.dataStore.data.map { it[WATERMARK_ENABLED] ?: true }
    val isBatchMode: Flow<Boolean> = context.dataStore.data.map { it[IS_BATCH_MODE] ?: false }
    val batchCount: Flow<Int> = context.dataStore.data.map { it[BATCH_COUNT] ?: 4 }

    // Vendedor (#15)
    val currentVendorId: Flow<String> = context.dataStore.data.map { it[CURRENT_VENDOR_ID] ?: "" }
    val currentVendorName: Flow<String> = context.dataStore.data.map { it[CURRENT_VENDOR_NAME] ?: "" }

    // Offline (#7)
    val isOfflineMode: Flow<Boolean> = context.dataStore.data.map { it[OFFLINE_MODE] ?: false }

    // Exportação (#16)
    val defaultExportSize: Flow<String> = context.dataStore.data.map { it[DEFAULT_EXPORT_SIZE] ?: "ORIGINAL_4K" }

    // Modo Noturno (#22)
    val nightModeAutoDetect: Flow<Boolean> = context.dataStore.data.map { it[NIGHT_MODE_AUTO] ?: true }

    // Backgrounds Personalizados (#19)
    val customBackgroundPaths: Flow<Set<String>> = context.dataStore.data.map { it[CUSTOM_BG_PATHS] ?: emptySet() }

    // Guia de Enquadramento Inteligente (#24)
    val smartFramingEnabled: Flow<Boolean> = context.dataStore.data.map { it[SMART_FRAMING_ENABLED] ?: true }
    val preferredCarType: Flow<String> = context.dataStore.data.map { it[PREFERRED_CAR_TYPE] ?: "SEDAN" }

    // Configurações Pro de Câmera
    val cameraIso: Flow<Int> = context.dataStore.data.map { it[CAM_ISO] ?: 100 }
    val cameraShutter: Flow<Long> = context.dataStore.data.map { it[CAM_SHUTTER] ?: 16_666_666L }
    val cameraEv: Flow<Float> = context.dataStore.data.map { it[CAM_EV] ?: 0f }
    val cameraWb: Flow<Int> = context.dataStore.data.map { it[CAM_WB] ?: 5500 }
    val cameraManualFocus: Flow<Boolean> = context.dataStore.data.map { it[CAM_MANUAL_FOCUS] ?: false }
    val cameraMetering: Flow<String> = context.dataStore.data.map { it[CAM_METERING] ?: "MATRIX" }
    val cameraRes: Flow<String> = context.dataStore.data.map { it[CAM_RES] ?: "RES_4K" }
    val cameraQuality: Flow<String> = context.dataStore.data.map { it[CAM_QUALITY] ?: "MAXIMUM" }
    val cameraTimer: Flow<Int> = context.dataStore.data.map { it[CAM_TIMER] ?: 0 }
    val cameraGrid: Flow<String> = context.dataStore.data.map { it[CAM_GRID] ?: "RULE_OF_THIRDS" }
    val cameraHistogram: Flow<Boolean> = context.dataStore.data.map { it[CAM_HISTOGRAM] ?: true }

    // Multi-Provider IA
    val primaryAiProvider: Flow<String?> = context.dataStore.data.map { it[PRIMARY_AI_PROVIDER] ?: "openrouter" }
    val activeAiProviders: Flow<Set<String>> = context.dataStore.data.map { it[ACTIVE_AI_PROVIDERS] ?: setOf("openrouter") }
    val aiFallbackOrder: Flow<Set<String>> = context.dataStore.data.map { it[AI_FALLBACK_ORDER] ?: emptySet() }
    val autoFallbackEnabled: Flow<Boolean> = context.dataStore.data.map { it[AUTO_FALLBACK_ENABLED] ?: true }

    // === Funções de escrita ===

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

    // Branding (#5)
    suspend fun setDealershipName(name: String) {
        context.dataStore.edit { it[DEALERSHIP_NAME] = name }
    }

    suspend fun setDealershipLogoPath(path: String) {
        context.dataStore.edit { it[DEALERSHIP_LOGO_PATH] = path }
    }

    suspend fun setWatermarkEnabled(enabled: Boolean) {
        context.dataStore.edit { it[WATERMARK_ENABLED] = enabled }
    }

    suspend fun setBatchMode(enabled: Boolean) {
        context.dataStore.edit { it[IS_BATCH_MODE] = enabled }
    }

    suspend fun setBatchCount(count: Int) {
        context.dataStore.edit { it[BATCH_COUNT] = count }
    }

    suspend fun setDealershipLogo(context: Context, path: String) {
        setDealershipLogoPath(path)
    }

    // Vendedor (#15)
    suspend fun setCurrentVendor(id: String, name: String) {
        context.dataStore.edit {
            it[CURRENT_VENDOR_ID] = id
            it[CURRENT_VENDOR_NAME] = name
        }
    }

    // Offline (#7)
    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { it[OFFLINE_MODE] = enabled }
    }

    // Exportação (#16)
    suspend fun setDefaultExportSize(size: String) {
        context.dataStore.edit { it[DEFAULT_EXPORT_SIZE] = size }
    }

    // Modo Noturno (#22)
    suspend fun setNightModeAutoDetect(enabled: Boolean) {
        context.dataStore.edit { it[NIGHT_MODE_AUTO] = enabled }
    }

    // Backgrounds Personalizados (#19)
    suspend fun addCustomBackground(path: String) {
        context.dataStore.edit {
            val current = it[CUSTOM_BG_PATHS] ?: emptySet()
            it[CUSTOM_BG_PATHS] = current + path
        }
    }

    suspend fun removeCustomBackground(path: String) {
        context.dataStore.edit {
            val current = it[CUSTOM_BG_PATHS] ?: emptySet()
            it[CUSTOM_BG_PATHS] = current - path
        }
    }

    // Guia de Enquadramento Inteligente (#24)
    suspend fun setSmartFramingEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SMART_FRAMING_ENABLED] = enabled }
    }

    suspend fun setPreferredCarType(type: String) {
        context.dataStore.edit { it[PREFERRED_CAR_TYPE] = type }
    }

    // Configurações Pro de Câmera
    suspend fun setCameraIso(value: Int) { context.dataStore.edit { it[CAM_ISO] = value } }
    suspend fun setCameraShutter(value: Long) { context.dataStore.edit { it[CAM_SHUTTER] = value } }
    suspend fun setCameraEv(value: Float) { context.dataStore.edit { it[CAM_EV] = value } }
    suspend fun setCameraWb(value: Int) { context.dataStore.edit { it[CAM_WB] = value } }
    suspend fun setCameraManualFocus(enabled: Boolean) { context.dataStore.edit { it[CAM_MANUAL_FOCUS] = enabled } }
    suspend fun setCameraMetering(mode: String) { context.dataStore.edit { it[CAM_METERING] = mode } }
    suspend fun setCameraRes(res: String) { context.dataStore.edit { it[CAM_RES] = res } }
    suspend fun setCameraQuality(quality: String) { context.dataStore.edit { it[CAM_QUALITY] = quality } }
    suspend fun setCameraTimer(seconds: Int) { context.dataStore.edit { it[CAM_TIMER] = seconds } }
    suspend fun setCameraGrid(type: String) { context.dataStore.edit { it[CAM_GRID] = type } }
    suspend fun setCameraHistogram(enabled: Boolean) { context.dataStore.edit { it[CAM_HISTOGRAM] = enabled } }

    // Multi-Provider IA
    suspend fun setPrimaryAiProvider(providerId: String) {
        context.dataStore.edit { it[PRIMARY_AI_PROVIDER] = providerId }
    }

    suspend fun setActiveAiProviders(providers: Set<String>) {
        context.dataStore.edit { it[ACTIVE_AI_PROVIDERS] = providers }
    }

    suspend fun setAiFallbackOrder(order: Set<String>) {
        context.dataStore.edit { it[AI_FALLBACK_ORDER] = order }
    }

    suspend fun setAutoFallbackEnabled(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_FALLBACK_ENABLED] = enabled }
    }
}
