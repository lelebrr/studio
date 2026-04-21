package com.studio.tline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studio.tline.utils.SettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    settingsManager: SettingsManager
) {
    val scope = rememberCoroutineScope()
    
    // Estados do SettingsManager
    val apiKey by settingsManager.apiKey.collectAsState(initial = "")
    val useProModels by settingsManager.useProModels.collectAsState(initial = false)

    Scaffold(
        containerColor = Color(0xFF0F0F0F),
        topBar = {
            TopAppBar(
                title = { Text("CONFIGURAÇÃO B2B", fontSize = 16.sp, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    // Corrigido: usando Icons.AutoMirrored.Filled.ArrowBack em vez de Icons.Filled.ArrowBack depreciado
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionHeader("CONEXÃO API")
            OutlinedTextField(
                value = apiKey ?: "",
                onValueChange = { scope.launch { settingsManager.setApiKey(it) } },
                label = { Text("OPENROUTER API KEY", fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedLabelColor = Color.Cyan
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsSectionHeader("CONFIGURAÇÕES DE ESTÚDIO")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SALVAMENTO AUTOMÁTICO 4K", color = Color.Gray, fontSize = 12.sp)
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Cyan)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A1A))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("MODO ULTRA (ELITE)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Máxima qualidade fotográfica 4K", color = Color.Gray, fontSize = 12.sp)
                }
                Switch(
                    checked = useProModels,
                    onCheckedChange = { scope.launch { settingsManager.setUseProModels(it) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Cyan)
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Selo de Versão Gold Master
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("VERSÃO GOLD MASTER v1.0.0", color = Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        title,
        color = Color.Cyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
