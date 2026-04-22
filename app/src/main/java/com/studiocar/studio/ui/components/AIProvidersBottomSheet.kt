package com.studiocar.studio.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.ai.providers.AIProviderManager
import com.studiocar.studio.ai.providers.ImageAIProvider
import com.studiocar.studio.utils.SecurityUtils
import com.studiocar.studio.utils.SettingsManager
import kotlinx.coroutines.launch

/**
 * AIProvidersBottomSheet V2026 - O painel de controle definitivo para IAs de imagem.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIProvidersBottomSheet(
    onDismiss: () -> Unit,
    settingsManager: SettingsManager,
    aiProviderManager: AIProviderManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val securityUtils = remember { SecurityUtils(context) }
    
    val primaryProviderId by settingsManager.primaryAiProvider.collectAsState(initial = "openrouter")
    val activeProviders by settingsManager.activeAiProviders.collectAsState(initial = setOf("openrouter"))
    val autoFallback by settingsManager.autoFallbackEnabled.collectAsState(initial = true)
    
    var selectedProviderForConfig by remember { mutableStateOf<ImageAIProvider?>(null) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0A0A0A).copy(alpha = 0.95f),
        scrimColor = Color.Black.copy(alpha = 0.7f),
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.DarkGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "PROVEDORES DE IA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Orquestração Inteligente StudioCar",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Surface(
                    color = Color.Cyan.copy(alpha = 0.1f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.2f))
                ) {
                    IconButton(onClick = { scope.launch { settingsManager.setAutoFallbackEnabled(!autoFallback) } }) {
                        Icon(
                            if (autoFallback) Icons.Default.AutoMode else Icons.Default.SettingsBackupRestore,
                            contentDescription = null,
                            tint = if (autoFallback) Color.Cyan else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Provider List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(aiProviderManager.getAllProviders()) { provider ->
                    ProviderItem(
                        provider = provider,
                        isPrimary = primaryProviderId == provider.id,
                        isActive = activeProviders.contains(provider.id),
                        onSelect = { 
                            scope.launch { 
                                settingsManager.setPrimaryAiProvider(provider.id)
                                if (!activeProviders.contains(provider.id)) {
                                    settingsManager.setActiveAiProviders(activeProviders + provider.id)
                                }
                            }
                        },
                        onToggle = { active ->
                            scope.launch {
                                val newSet = if (active) activeProviders + provider.id else activeProviders - provider.id
                                settingsManager.setActiveAiProviders(newSet)
                            }
                        },
                        onConfig = {
                            selectedProviderForConfig = provider
                            showApiKeyDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showApiKeyDialog && selectedProviderForConfig != null) {
        ApiKeyConfigDialog(
            provider = selectedProviderForConfig!!,
            securityUtils = securityUtils,
            onDismiss = { showApiKeyDialog = false }
        )
    }
}

@Composable
fun ProviderItem(
    provider: ImageAIProvider,
    isPrimary: Boolean,
    isActive: Boolean,
    onSelect: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onConfig: () -> Unit
) {
    val borderColor = if (isPrimary) Color.Cyan.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
    val bgColor = if (isPrimary) Color(0xFF1A1A1A) else Color(0xFF111111)

    Surface(
        onClick = onSelect,
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone baseado no ID do provedor
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color.Cyan.copy(alpha = 0.1f) else Color.DarkGray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(provider.id) {
                        "gemini" -> Icons.Default.AutoAwesome
                        "openai" -> Icons.Default.SmartToy
                        "claude" -> Icons.Default.Draw
                        "stability" -> Icons.Default.ColorLens
                        "replicate" -> Icons.Default.AccountTree
                        "together" -> Icons.Default.Hub
                        "fireworks" -> Icons.Default.Flare
                        "huggingface" -> Icons.Default.EmojiEmotions
                        "grok" -> Icons.Default.Psychology
                        else -> Icons.Default.Cloud
                    },
                    contentDescription = null,
                    tint = if (isActive) Color.Cyan else Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    provider.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (isPrimary) {
                    Text("PRINCIPAL", color = Color.Cyan, fontWeight = FontWeight.Black, fontSize = 9.sp)
                } else {
                    Text(if (isActive) "ATIVO" else "DESATIVADO", color = Color.Gray, fontSize = 10.sp)
                }
            }

            Row {
                IconButton(onClick = onConfig) {
                    Icon(Icons.Default.VpnKey, null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Cyan,
                        checkedTrackColor = Color.Cyan.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }
        }
    }
}

@Composable
fun ApiKeyConfigDialog(
    provider: ImageAIProvider,
    securityUtils: SecurityUtils,
    onDismiss: () -> Unit
) {
    var key by remember { mutableStateOf(securityUtils.getApiKey(provider.id) ?: "") }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF151515),
        title = {
            Text(
                "CONFIGURAR ${provider.name.uppercase()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("API KEY", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Cyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Button(
                    onClick = { 
                        isTesting = true
                        testResult = null
                        scope.launch {
                            // Real provider connection test
                            testResult = provider.testConnection(key)
                            isTesting = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    enabled = !isTesting
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Cyan, strokeWidth = 2.dp)
                    } else {
                        Text(
                            when(testResult) {
                                true -> "CONEXÃO OK!"
                                false -> "ERRO NA CHAVE"
                                else -> "TESTAR CONEXÃO"
                            },
                            color = when(testResult) {
                                true -> Color.Green
                                false -> Color.Red
                                else -> Color.White
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                securityUtils.saveApiKey(provider.id, key)
                onDismiss()
            }) {
                Text("SALVAR", color = Color.Cyan, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCELAR", color = Color.Gray)
            }
        }
    )
}

// Helper to scale switch
@Composable
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.padding(((1 - scale) * 24).dp) // Adjust padding to compensate scale for simple usage
)
