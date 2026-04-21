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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.*
import com.studiocar.studio.ui.theme.*
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
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            StudioSurface.copy(alpha = 0.95f),
                            StudioBlack.copy(alpha = 1.0f)
                        )
                    )
                )
                .padding(bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Drag Indicator
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .align(Alignment.CenterHorizontally)
                )

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "AJUSTES DE ESTÚDIO",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "CONTROLES MANUAIS PRO",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }

                // Seção: Presets
                ProfessionalGroup(title = "CENÁRIOS", icon = Icons.Default.AutoAwesome) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(CameraPreset.entries) { preset ->
                            PresetChip(
                                label = preset.label.uppercase(),
                                onClick = { onSettingsChanged(applyPreset(settings, preset)) }
                            )
                        }
                    }
                }

                // Seção: Exposição
                ProfessionalGroup(title = "EXPOSIÇÃO", icon = Icons.Default.Brightness6) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        ManualSliderControl(
                            label = "SENSIBILIDADE (ISO)",
                            value = settings.iso.toFloat(),
                            range = 100f..3200f,
                            displayValue = settings.iso.toString(),
                            onValueChange = { onSettingsChanged(settings.copy(iso = it.roundToInt())) }
                        )

                        val shutterDisplay = formatShutterSpeed(settings.shutterSpeedNanos)
                        ManualSliderControl(
                            label = "OBTURADOR (SPEED)",
                            value = nanosToSliderPosition(settings.shutterSpeedNanos),
                            range = 0f..1f,
                            displayValue = shutterDisplay,
                            onValueChange = { onSettingsChanged(settings.copy(shutterSpeedNanos = sliderPositionToNanos(it))) }
                        )

                        ManualSliderControl(
                            label = "COMPENSAÇÃO (EV)",
                            value = settings.exposureCompensation,
                            range = -3f..3f,
                            displayValue = if (settings.exposureCompensation > 0) "+${String.format(java.util.Locale.getDefault(), "%.1f", settings.exposureCompensation)}" else String.format(java.util.Locale.getDefault(), "%.1f", settings.exposureCompensation),
                            onValueChange = { onSettingsChanged(settings.copy(exposureCompensation = it)) }
                        )
                    }
                }

                // Seção: Cor
                ProfessionalGroup(title = "COR & BALANÇO", icon = Icons.Default.Palette) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ManualSliderControl(
                            label = "TEMPERATURA (WB)",
                            value = settings.whiteBalanceTemp.toFloat(),
                            range = 2500f..10000f,
                            displayValue = "${settings.whiteBalanceTemp}K",
                            onValueChange = { onSettingsChanged(settings.copy(whiteBalanceTemp = it.roundToInt())) }
                        )
                    }
                }

                // Seção: Qualidade e Foco
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = StudioSurfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if(settings.isManualFocus) StudioCyan.copy(alpha = 0.5f) else Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable { onSettingsChanged(settings.copy(isManualFocus = !settings.isManualFocus)) }
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(if(settings.isManualFocus) Icons.Default.FilterCenterFocus else Icons.Default.CenterFocusWeak, null, tint = if(settings.isManualFocus) StudioCyan else Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("FOCO", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            Text(if(settings.isManualFocus) "MANUAL" else "AUTO", color = if(settings.isManualFocus) StudioCyan else Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = StudioSurfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .clickable {
                                    val next = MeteringMode.entries[(settings.meteringMode.ordinal + 1) % MeteringMode.entries.size]
                                    onSettingsChanged(settings.copy(meteringMode = next))
                                }
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Grain, null, tint = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("MEDIÇÃO", color = Color.White, style = MaterialTheme.typography.labelSmall)
                            Text(settings.meteringMode.label.uppercase(), color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Seção: Outros
                ProfessionalGroup(title = "SISTEMA", icon = Icons.Default.Tune) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(containerColor = StudioSurfaceVariant),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SettingRow(
                                label = "Resolução",
                                value = settings.resolution.label,
                                onClick = {
                                    val next = CameraResolution.entries[(settings.resolution.ordinal + 1) % CameraResolution.entries.size]
                                    onSettingsChanged(settings.copy(resolution = next))
                                }
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                            SettingRow(
                                label = "Timer de Estúdio",
                                value = if (settings.timerSeconds == 0) "DESATIVADO" else "${settings.timerSeconds}S",
                                onClick = {
                                    val options = listOf(0, 3, 5, 10)
                                    val nextIndex = (options.indexOf(settings.timerSeconds) + 1) % options.size
                                    onSettingsChanged(settings.copy(timerSeconds = options[nextIndex]))
                                }
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                            SettingRow(
                                label = "Grade Auxiliar",
                                value = settings.gridType.label.uppercase(),
                                onClick = {
                                    val next = GridType.entries[(settings.gridType.ordinal + 1) % GridType.entries.size]
                                    onSettingsChanged(settings.copy(gridType = next))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalGroup(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(24.dp).background(StudioCyan.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = StudioCyan, modifier = Modifier.size(14.dp))
            }
            Text(
                title, 
                style = MaterialTheme.typography.labelLarge, 
                color = StudioCyan, 
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    displayValue, 
                    color = Color.White, 
                    style = MaterialTheme.typography.bodyLarge, 
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = StudioCyan,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}

@Composable
private fun PresetChip(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = StudioSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(value, color = StudioCyan, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelLarge)
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

// Helpers (keep same logic)
private fun formatShutterSpeed(nanos: Long): String {
    val seconds = nanos / 1_000_000_000.0
    return if (seconds >= 1.0) {
        "${seconds.toInt()}s"
    } else {
        "1/${(1.0 / seconds).roundToInt()}"
    }
}

private fun nanosToSliderPosition(nanos: Long): Float {
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
