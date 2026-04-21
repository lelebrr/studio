package com.studiocar.studio.ai.providers

import android.content.Context
import android.graphics.Bitmap
import com.studiocar.studio.ai.providers.impl.*
import com.studiocar.studio.data.models.EditOptions
import com.studiocar.studio.utils.SecurityUtils
import com.studiocar.studio.utils.SettingsManager
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * AIProviderManager - Cérebro da orquestração multi-IA do StudioCar.
 * Gerencia o ciclo de vida dos provedores e a lógica de fallback resiliente.
 */
class AIProviderManager(private val context: Context) {

    private val securityUtils = SecurityUtils(context)
    private val settingsManager = SettingsManager(context)

    private val providers = mutableMapOf<String, ImageAIProvider>()

    init {
        registerProviders()
    }

    private fun registerProviders() {
        // Registro manual dos provedores principais
        val openRouter = OpenRouterProvider(context, securityUtils)
        val gemini = GeminiProvider(securityUtils)
        val claude = ClaudeProvider(securityUtils)
        val openai = OpenAIProvider(securityUtils)
        val stability = StabilityAIProvider(securityUtils)
        val replicate = ReplicateProvider(securityUtils)
        val together = TogetherAIProvider(securityUtils)
        val fireworks = FireworksAIProvider(securityUtils)
        val huggingface = HuggingFaceProvider(securityUtils)
        val grok = GrokProvider(securityUtils)

        providers[openRouter.id] = openRouter
        providers[gemini.id] = gemini
        providers[claude.id] = claude
        providers[openai.id] = openai
        providers[stability.id] = stability
        providers[replicate.id] = replicate
        providers[together.id] = together
        providers[fireworks.id] = fireworks
        providers[huggingface.id] = huggingface
        providers[grok.id] = grok
        
        Timber.i("AIProviderManager: ${providers.size} provedores registrados com sucesso.")
    }

    /**
     * Retorna o provedor principal baseado nas configurações do usuário.
     */
    suspend fun getPrimaryProvider(): ImageAIProvider {
        val preferredId = settingsManager.primaryAiProvider.first() ?: "openrouter"
        return providers[preferredId] ?: providers["openrouter"]!!
    }

    /**
     * Executa a edição de imagem com tentativa de fallback automática.
     */
    suspend fun editImageWithFallback(
        bitmap: Bitmap,
        mask: Bitmap?,
        prompt: String,
        options: EditOptions
    ): Bitmap? {
        val useAutoFallback = settingsManager.autoFallbackEnabled.first()
        val primary = getPrimaryProvider()
        
        // Tenta o principal primeiro
        Timber.i("Tentando processar com provedor principal: ${primary.name}")
        val result = runCatching { primary.editCarImage(bitmap, mask, prompt, options) }.getOrNull()
        
        if (result != null) return result
        
        // Se falhar e fallback estiver ativo, tenta os próximos na ordem de preferência
        if (useAutoFallback) {
            val fallbackOrder = settingsManager.aiFallbackOrder.first().toList()
            for (providerId in fallbackOrder) {
                if (providerId == primary.id) continue
                
                val provider = providers[providerId] ?: continue
                if (!provider.isAvailable) continue

                Timber.w("Provedor ${primary.name} falhou. Tentando fallback: ${provider.name}")
                val fallbackResult = runCatching { provider.editCarImage(bitmap, mask, prompt, options) }.getOrNull()
                if (fallbackResult != null) {
                    Timber.i("Fallback bem sucedido com ${provider.name}")
                    return fallbackResult
                }
            }
        }

        Timber.e("Todos os provedores de IA falharam.")
        return null
    }

    fun getAllProviders(): List<ImageAIProvider> = providers.values.toList()

    fun getProvider(id: String): ImageAIProvider? = providers[id]
}
