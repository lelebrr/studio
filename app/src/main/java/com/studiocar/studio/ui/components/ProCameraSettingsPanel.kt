package com.studiocar.studio.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProCameraSettingsPanel(
    settings: CameraSettings,
    onSettingsChanged: (CameraSettings) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color(0xFF121212).copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Configurações Profissionais",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Controle manual de estúdio",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                Divider(color = Color.White.copy(alpha = 0.1f))

                // Seção: Presets
                SettingSection(title = "Presests de Cena", icon = Icons.Default.AutoAwesome) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CameraPreset.entries) { preset ->
                            PresetChip(
                                label = preset.label,
                                isSelected = false, // Presets aplicam e depois o usuário pode mexer
                                onClick = { onSettingsChanged(applyPreset(settings, preset)) }
                            )
                        }
                    }
                }

                // Seção: Exposição (ISO e Shutter)
                SettingSection(title = "Exposição & Sensor", icon = Icons.Default.CameraAlt) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // ISO
                        ManualSliderControl(
                            label = "ISO",
                            value = settings.iso.toFloat(),
                            range = 100f..3200f,
                            displayValue = settings.iso.toString(),
                            onValueChange = { onSettingsChanged(settings.copy(iso = it.roundToInt())) }
                        )

                        // Shutter Speed
                        val shutterDisplay = formatShutterSpeed(settings.shutterSpeedNanos)
                        ManualSliderControl(
                            label = "Obturador",
                            value = nanosToSliderPosition(settings.shutterSpeedNanos),
                            range = 0f..1f,
                            displayValue = shutterDisplay,
                            onValueChange = { onSettingsChanged(settings.copy(shutterSpeedNanos = sliderPositionToNanos(it))) }
                        )

                        // EV
                        ManualSliderControl(
                            label = "Comp. Exposição (EV)",
                            value = settings.exposureCompensation,
                            range = -3f..3f,
                            displayValue = String.format("%.1f", settings.exposureCompensation),
                            onValueChange = { onSettingsChanged(settings.copy(exposureCompensation = it)) }
                        )
                    }
                }

                // Seção: White Balance
                SettingSection(title = "Balanço de Branco", icon = Icons.Default.WbSunny) {
                    ManualSliderControl(
                        label = "Temperatura",
                        value = settings.whiteBalanceTemp.toFloat(),
                        range = 2500f..10000f,
                        displayValue = "${settings.whiteBalanceTemp}K",
                        onValueChange = { onSettingsChanged(settings.copy(whiteBalanceTemp = it.roundToInt())) }
                    )
                }

                // Seção: Foco e Medição
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ToggleBox(
                            label = "Foco Manual / Trava",
                            isActive = settings.isManualFocus,
                            onClick = { onSettingsChanged(settings.copy(isManualFocus = !settings.isManualFocus)) }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GridSelector(
                            label = "Medição de Luz",
                            current = settings.meteringMode.label,
                            onClick = {
                                val next = MeteringMode.entries[(settings.meteringMode.ordinal + 1) % MeteringMode.entries.size]
                                onSettingsChanged(settings.copy(meteringMode = next))
                            }
                        )
                    }
                }

                // Seção: Outras Configurações
                SettingSection(title = "Sistema & Auxiliares", icon = Icons.Default.Settings) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Resolução
                        RowSetting(
                            label = "Resolução de Captura",
                            value = settings.resolution.label,
                            onClick = {
                                val next = CameraResolution.entries[(settings.resolution.ordinal + 1) % CameraResolution.entries.size]
                                onSettingsChanged(settings.copy(resolution = next))
                            }
                        )
                        // Timer
                        RowSetting(
                            label = "Timer",
                            value = if (settings.timerSeconds == 0) "Desligado" else "${settings.timerSeconds}s",
                            onClick = {
                                val options = listOf(0, 3, 5, 10)
                                val nextIndex = (options.indexOf(settings.timerSeconds) + 1) % options.size
                                onSettingsChanged(settings.copy(timerSeconds = options[nextIndex]))
                            }
                        )
                        // Grade
                        RowSetting(
                            label = "Grade de Enquadramento",
                            value = settings.gridType.label,
                            onClick = {
                                val next = GridType.entries[(settings.gridType.ordinal + 1) % GridType.entries.size]
                                onSettingsChanged(settings.copy(gridType = next))
                            }
                        )
                        // Histograma
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Histograma em Tempo Real", color = Color.White, fontSize = 14.sp)
                            Switch(
                                checked = settings.showHistogram,
                                onCheckedChange = { onSettingsChanged(settings.copy(showHistogram = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.Cyan)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Color.Cyan, modifier = Modifier.size(16.dp))
            Text(title, color = Color.Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        content()
    }
}

@Composable
private fun ManualSliderControl(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 14.sp)
            Text(displayValue, color = Color.Cyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Cyan,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun PresetChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if(isSelected) Color.Cyan else Color.White.copy(alpha = 0.2f))
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ToggleBox(label: String, isActive: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if(isActive) Color.Cyan else Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(if(isActive) "MANUAL" else "AUTO", color = if(isActive) Color.Cyan else Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun GridSelector(label: String, current: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(current.uppercase(java.util.Locale.ROOT), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RowSetting(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, color = Color.Gray, fontSize = 14.sp)
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

// Helpers
private fun formatShutterSpeed(nanos: Long): String {
    val seconds = nanos / 1_000_000_000.0
    return if (seconds >= 1.0) {
        "${seconds.toInt()}s"
    } else {
        "1/${(1.0 / seconds).roundToInt()}"
    }
}

private fun nanosToSliderPosition(nanos: Long): Float {
    // Escala logarítmica ou mapeamento simples para fins demo: 1/1000 a 1s
    // 0.0f = 1s, 1.0f = 1/1000s
    val minNanos = 1_000_000L // 1/1000s
    val maxNanos = 1_000_000_000L // 1s
    return 1f - ((nanos - minNanos).toFloat() / (maxNanos - minNanos))
}

private fun sliderPositionToNanos(pos: Float): Long {
    val minNanos = 1_000_000L
    val maxNanos = 1_000_000_000L
    return (maxNanos - (pos * (maxNanos - minNanos))).toLong()
}

enum class CameraPreset(val label: String) {
    SHOWROOM("Showroom"),
    STUDIO("Estúdio Pro"),
    BLACK_CAR("Carro Preto"),
    WHITE_CAR("Carro Branco"),
    NATURAL("Luz Natural"),
    LOW_LIGHT("Baixa Luz")
}

private fun applyPreset(current: CameraSettings, preset: CameraPreset): CameraSettings {
    return when(preset) {
        CameraPreset.SHOWROOM -> current.copy(iso = 200, shutterSpeedNanos = 16_666_666L, whiteBalanceTemp = 4500, exposureCompensation = 0f)
        CameraPreset.STUDIO -> current.copy(iso = 100, shutterSpeedNanos = 8_000_000L, whiteBalanceTemp = 5500, exposureCompensation = 0f)
        CameraPreset.BLACK_CAR -> current.copy(iso = 100, shutterSpeedNanos = 10_000_000L, exposureCompensation = -0.7f)
        CameraPreset.WHITE_CAR -> current.copy(iso = 100, shutterSpeedNanos = 10_000_000L, exposureCompensation = 0.7f)
        CameraPreset.NATURAL -> current.copy(iso = 100, shutterSpeedNanos = 2_000_000L, whiteBalanceTemp = 6000)
        CameraPreset.LOW_LIGHT -> current.copy(iso = 1600, shutterSpeedNanos = 66_666_666L, whiteBalanceTemp = 3000)
    }
}
