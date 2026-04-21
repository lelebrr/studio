package com.studiocar.studio.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.ExportSize
import com.studiocar.studio.ui.theme.*
import com.studiocar.studio.utils.SettingsManager
import kotlinx.coroutines.launch

/**
 * SettingsScreen V2.1.0 - StudioCar Elite Management.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsManager: SettingsManager,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val apiKey by settingsManager.apiKey.collectAsState(initial = "")
    val useProModels by settingsManager.useProModels.collectAsState(initial = true)
    val isBatchMode by settingsManager.isBatchMode.collectAsState(initial = false)
    val batchCount by settingsManager.batchCount.collectAsState(initial = 4)
    val isOfflineMode by settingsManager.isOfflineMode.collectAsState(initial = false)
    val dealershipName by settingsManager.dealershipName.collectAsState(initial = "")
    val vendorName by settingsManager.currentVendorName.collectAsState(initial = "")
    val watermarkEnabled by settingsManager.watermarkEnabled.collectAsState(initial = false)
    val exportSizeStr by settingsManager.defaultExportSize.collectAsState(initial = "ORIGINAL_4K")
    val exportSize = remember(exportSizeStr) { 
        runCatching { ExportSize.valueOf(exportSizeStr) }.getOrDefault(ExportSize.ORIGINAL_4K)
    }
    val smartFramingEnabled by settingsManager.smartFramingEnabled.collectAsState(initial = true)
    val preferredCarType by settingsManager.preferredCarType.collectAsState(initial = "SEDAN")
    val advancedStabilization by settingsManager.advancedStabilization.collectAsState(initial = true)

    val logoLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { scope.launch { settingsManager.setDealershipLogo(it.toString()) } }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CONFIGURAÇÕES ELITE", fontSize = 14.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- SEÇÃO: IA E CONEXÃO ---
            SettingsSection("IA & CONEXÃO (OPENROUTER)") {
                OutlinedTextField(
                    value = apiKey ?: "",
                    onValueChange = { scope.launch { settingsManager.setApiKey(it) } },
                    label = { Text("OPENROUTER API KEY", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors()
                )
                
                SettingsSwitch(
                    title = "MODO DEMO (OFFLINE)",
                    subtitle = "Processamento local via MediaPipe",
                    checked = isOfflineMode,
                    onCheckedChange = { scope.launch { settingsManager.setOfflineMode(it) } }
                )

                SettingsSwitch(
                    title = "PRECISÃO PLATINUM (PRO)",
                    subtitle = "Usa Gemini 3 Pro + FLUX 1.1 Pro Ultra",
                    checked = useProModels,
                    onCheckedChange = { scope.launch { settingsManager.setUseProModels(it) } }
                )
            }

            // --- SEÇÃO: BRANDING CONCESSIONÁRIA ---
            SettingsSection("BRANDING (B2B ELITE)") {
                OutlinedTextField(
                    value = dealershipName,
                    onValueChange = { scope.launch { settingsManager.setDealershipName(it) } },
                    label = { Text("NOME DA LOJA", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                SettingsSwitch(
                    title = "MARCA D'ÁGUA EM 4K",
                    subtitle = "Aplica branding automaticamente no export",
                    checked = watermarkEnabled,
                    onCheckedChange = { scope.launch { settingsManager.setWatermarkEnabled(it) } }
                )

                Button(
                    onClick = { logoLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.UploadFile, null, tint = StudioCyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("LOGOTIPO DA LOJA (.PNG)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // --- SEÇÃO: VENDEDOR E OPERAÇÃO ---
            SettingsSection("VENDEDOR & OPERAÇÃO") {
                OutlinedTextField(
                    value = vendorName,
                    onValueChange = { scope.launch { settingsManager.setCurrentVendor("0", it) } },
                    label = { Text("NOME DO VENDEDOR ATUAL", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                SettingsSwitch(
                    title = "MODO LOTE (BATCH)",
                    subtitle = "Fila de processamento simultâneo",
                    checked = isBatchMode,
                    onCheckedChange = { scope.launch { settingsManager.setBatchMode(it) } }
                )

                if (isBatchMode) {
                    Column {
                        Text("TAMANHO DO LOTE: $batchCount FOTOS", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = batchCount.toFloat(),
                            onValueChange = { scope.launch { settingsManager.setBatchCount(it.toInt()) } },
                            valueRange = 4f..10f,
                            steps = 5,
                            colors = SliderDefaults.colors(thumbColor = StudioCyan, activeTrackColor = StudioCyan)
                        )
                    }
                }
            }

            // --- SEÇÃO: CÂMERA ---
            SettingsSection("CÂMERA") {
                SettingsSwitch(
                    title = "ESTABILIZAÇÃO AVANÇADA",
                    subtitle = "Usa OIS e redução de tremores para capturas mais nítidas",
                    checked = advancedStabilization,
                    onCheckedChange = { scope.launch { settingsManager.setAdvancedStabilization(it) } }
                )
            }

            // --- SEÇÃO: SISTEMA ---
            SettingsSection("SISTEMA") {
                Button(
                    onClick = onNavigateToAbout,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Icon(Icons.Default.Info, null, tint = StudioCyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("SOBRE O STUDIOCAR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = StudioCyan, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("StudioCar Elite Suite V2.1.0", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text("B2B ENTERPRISE LICENSE ACTIVE", color = Color.DarkGray, fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(title, color = StudioCyan, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        content()
    }
}

@Composable
fun SettingsSwitch(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        color = StudioSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = StudioCyan,
                    checkedTrackColor = StudioCyan.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
private fun ExportChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        color = if (selected) StudioCyan else StudioSurfaceVariant,
        shape = RoundedCornerShape(20.dp),
        border = if (selected) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
            Text(label, color = if (selected) StudioBlack else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = StudioCyan,
    unfocusedBorderColor = Color.DarkGray,
    focusedLabelColor = StudioCyan,
    cursorColor = StudioCyan,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
