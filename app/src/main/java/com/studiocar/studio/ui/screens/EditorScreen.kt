@file:OptIn(ExperimentalMaterial3Api::class)

package com.studiocar.studio.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.studiocar.studio.ui.theme.*
import com.studiocar.studio.ui.components.*
import com.studiocar.studio.ui.components.AIProvidersBottomSheet
import com.studiocar.studio.ui.viewmodels.EditorViewModel
import com.studiocar.studio.utils.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studiocar.studio.data.models.*
import com.studiocar.studio.ui.components.CaptionDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var showBgSheet by remember { mutableStateOf(false) }
    var showFloorSheet by remember { mutableStateOf(false) }
    var showStudioSheet by remember { mutableStateOf(false) }
    var showCaptionDialog by remember { mutableStateOf(false) }
    var showMetaExpanded by remember { mutableStateOf(false) }

    val originalBitmap by viewModel.originalBitmap.collectAsState()
    val resultBitmap by viewModel.resultBitmap.collectAsState()
    val processingStage by viewModel.processingStage.collectAsState()
    val options by viewModel.options.collectAsState()
    val customBackgrounds by viewModel.customBackgrounds.collectAsState(initial = emptySet())
    val vinInfo by viewModel.vinInfo.collectAsState()
    val generatedCaption by viewModel.generatedCaption.collectAsState()
    
    val adjustedBitmap by viewModel.adjustedBitmap.collectAsState()
    val adjustments by viewModel.adjustments.collectAsState()
    
    var showAdjustments by remember { mutableStateOf(false) }

    val bgLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { 
            viewModel.addCustomBackground(context, it)
            showBgSheet = false 
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            StudioAppBar(
                title = "Editor AI",
                subtitle = if(options.isDealershipMode) "Plano B2B Ativo" else "Versão Gratuita",
                showBackButton = true,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToHistory) { Icon(Icons.Default.History, null, tint = Color.White) }
                    IconButton(onClick = { viewModel.setShowAiProvidersSheet(true) }) {
                        Icon(Icons.Default.Settings, "Configurações de IA", tint = StudioCyan)
                    }
                }
            )
        },
        bottomBar = {
            EditorBottomBar(
                isProcessing = processingStage != EditorViewModel.ProcessingStage.IDLE,
                hasResult = resultBitmap != null,
                onGenerate = { viewModel.processImage(context) },
                onCaption = { 
                    showCaptionDialog = true
                    viewModel.generateAiCaption()
                },
                onSave = { 
                    resultBitmap?.let { 
                        scope.launch { viewModel.saveToGallery(context, it) }
                    }
                }
            )
        }
    ) { padding ->
        val showAiSheet by viewModel.showAiProvidersSheet.collectAsState()
        if (showAiSheet) {
            AIProvidersBottomSheet(
                onDismiss = { viewModel.setShowAiProvidersSheet(false) },
                settingsManager = SettingsManager(context),
                aiProviderManager = viewModel.getAiProviderManager(context)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            PreviewWindow(
                original = originalBitmap,
                result = adjustedBitmap ?: resultBitmap,
                stage = processingStage
            )

            MetadataPanel(
                vinInfo = vinInfo,
                isExpanded = showMetaExpanded
            ) { showMetaExpanded = !showMetaExpanded }

            FeaturesPanel(options = options) {
                viewModel.updateOptions(it)
            }

            StudioControls(
                currentBg = options.background,
                currentFloor = options.floor,
                currentStudio = options.selectedStudioScene,
                onSelectBg = { showBgSheet = true },
                onSelectFloor = { showFloorSheet = true },
                onSelectStudio = { showStudioSheet = true }
            )

                AdvancedAdjustmentsPanel(
                    adjustments = adjustments,
                    isExpanded = showAdjustments,
                    onToggle = { showAdjustments = !showAdjustments },
                    onUpdateAdjustments = { viewModel.updateAdjustments(it) },
                    onAutoEnhance = { viewModel.autoEnhance() },
                    onReset = { viewModel.resetAdjustments() }
                )

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showBgSheet) {
            ModalBottomSheet(onDismissRequest = { showBgSheet = false }, containerColor = StudioSurface) {
                SelectionList(
                    title = "Cenários StudioCar",
                    items = CarBackground.entries,
                    customItems = customBackgrounds.toList(),
                    selected = options.background,
                    onSelect = { 
                        viewModel.updateOptions(options.copy(background = it as CarBackground))
                        showBgSheet = false
                    },
                    onAddCustom = { bgLauncher.launch("image/*") }
                )
            }
        }

        if (showFloorSheet) {
            ModalBottomSheet(onDismissRequest = { showFloorSheet = false }, containerColor = StudioSurface) {
                SelectionList(
                    title = "Pisos Profissionais",
                    items = CarFloor.entries,
                    selected = options.floor,
                    onSelect = { 
                        viewModel.updateOptions(options.copy(floor = it as CarFloor))
                        showFloorSheet = false
                    }
                )
            }
        }

        if (showStudioSheet) {
            ModalBottomSheet(
                onDismissRequest = { showStudioSheet = false },
                containerColor = StudioBlack,
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
            ) {
                StudioSceneSelectionGrid(
                    selectedScene = options.selectedStudioScene,
                    onSelect = {
                        viewModel.selectStudioScene(it)
                        showStudioSheet = false
                    }
                )
            }
        }

        if (showCaptionDialog) {
            CaptionDialog(
                caption = generatedCaption,
                onDismiss = { showCaptionDialog = false },
                onCopy = { showCaptionDialog = false }
            )
        }
    }
}

@Composable
fun PreviewWindow(
    original: Bitmap?, 
    result: Bitmap?, 
    stage: EditorViewModel.ProcessingStage
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(StudioSurface)
            .border(1.dp, BorderColor, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = result ?: original,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "preview"
        ) { displayBitmap ->
            if (displayBitmap != null) {
                Image(
                    bitmap = displayBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        AnimatedVisibility(
            visible = stage != EditorViewModel.ProcessingStage.IDLE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                StudioPulseLoading(
                    message = when(stage) {
                        EditorViewModel.ProcessingStage.SEGMENTING -> "RECONHECENDO VEÍCULO..."
                        EditorViewModel.ProcessingStage.POLISHING -> "POLIMENTO IA FINAL..."
                        else -> "IA PROCESSANDO..."
                    }
                )
            }
        }
    }
}

@Composable
fun MetadataPanel(vinInfo: com.studiocar.studio.data.models.EditedCar?, isExpanded: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable(onClick = onToggle),
        color = StudioSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, null, tint = StudioCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = vinInfo?.let { "${it.carBrand} ${it.carModel}" } ?: "Veículo não identificado",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(if(isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.Gray)
            }
            if (isExpanded && vinInfo != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetaItem("VIN", vinInfo.vinCode ?: "N/A")
                    MetaItem("COR", vinInfo.carColor ?: "N/A")
                    MetaItem("ANO", vinInfo.carYear ?: "N/A")
                }
            }
        }
    }
}

@Composable
private fun MetaItem(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Black)
    }
}

@Composable
fun FeaturesPanel(options: com.studiocar.studio.data.models.EditOptions, onOptionToggle: (com.studiocar.studio.data.models.EditOptions) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("REFINAMENTO IA", color = StudioCyan, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FeatureChip("SAM 2 ULTRA", options.isSam2UltraEnabled, { onOptionToggle(options.copy(isSam2UltraEnabled = it)) })
            FeatureChip("REFRAÇÃO+", options.advancedGlassRefraction, { onOptionToggle(options.copy(advancedGlassRefraction = it)) })
            FeatureChip("SOMBRAS", options.autoShadows, { onOptionToggle(options.copy(autoShadows = it)) })
            FeatureChip("LIMPEZA", options.removeUnwantedObjects, { onOptionToggle(options.copy(removeUnwantedObjects = it)) })
        }
    }
}

@Composable
fun FeatureChip(label: String, active: Boolean, onToggle: (Boolean) -> Unit) {
    FilterChip(
        selected = active,
        onClick = { onToggle(!active) },
        label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = StudioCyan,
            selectedLabelColor = StudioBlack,
            containerColor = StudioSurfaceVariant,
            labelColor = Color.Gray
        ),
        border = null
    )
}

@Composable
fun StudioControls(
    currentBg: CarBackground, 
    currentFloor: CarFloor, 
    currentStudio: com.studiocar.studio.data.models.StudioScene?,
    onSelectBg: () -> Unit, 
    onSelectFloor: () -> Unit,
    onSelectStudio: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        OptionCard(
            title = "ESTÚDIO COMPLETO",
            selected = currentStudio?.name ?: "Selecionar Cena Professional",
            icon = Icons.Default.AutoAwesome,
            modifier = Modifier.fillMaxWidth(),
            onClick = onSelectStudio,
            isPremium = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OptionCard(title = "AMBIENTE", selected = currentBg.description, icon = Icons.Default.Landscape, modifier = Modifier.weight(1f), onClick = onSelectBg)
            OptionCard(title = "PISO", selected = currentFloor.description, icon = Icons.Default.Layers, modifier = Modifier.weight(1f), onClick = onSelectFloor)
        }
    }
}

@Composable
fun EditorBottomBar(isProcessing: Boolean, hasResult: Boolean, onGenerate: () -> Unit, onCaption: () -> Unit, onSave: () -> Unit) {
    Surface(
        color = StudioSurface,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasResult) {
                IconButton(onClick = onCaption, modifier = Modifier.size(56.dp).clip(CircleShape).background(StudioSurfaceVariant)) {
                    Icon(Icons.Default.AutoAwesome, null, tint = StudioCyan)
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("SALVAR 4K", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            } else {
                Button(
                    onClick = onGenerate,
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = StudioBlack),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = StudioBlack)
                    else Text("GERAR ESTÚDIO IA", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun OptionCard(title: String, selected: String, icon: ImageVector, modifier: Modifier = Modifier, isPremium: Boolean = false, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        color = StudioSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if(isPremium) StudioCyan.copy(alpha = 0.3f) else BorderColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if(isPremium) StudioCyan else Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = if(isPremium) StudioCyan else Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                Text(selected, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, maxLines = 1)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.DarkGray)
        }
    }
}

@Composable
fun StudioSceneSelectionGrid(selectedScene: com.studiocar.studio.data.models.StudioScene?, onSelect: (com.studiocar.studio.data.models.StudioScene) -> Unit) {
    var selectedCategory by remember { mutableStateOf(com.studiocar.studio.data.models.SceneCategory.SHOWROOM) }
    val scenes = remember(selectedCategory) { com.studiocar.studio.data.StudioScenes.allScenes.filter { it.category == selectedCategory } }

    Column(modifier = Modifier.fillMaxHeight(0.85f).padding(24.dp)) {
        Text("CENÁRIOS PREMIUM", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            com.studiocar.studio.data.models.SceneCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(scenes) { scene ->
                StudioSceneCard(scene = scene, isSelected = selectedScene?.id == scene.id, onClick = { onSelect(scene) })
            }
        }
    }
}

@Composable
fun StudioSceneCard(scene: com.studiocar.studio.data.models.StudioScene, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(StudioSurfaceVariant)
            .border(2.dp, if (isSelected) StudioCyan else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = "file:///android_asset/${scene.imageAsset}",
            contentDescription = scene.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(scene.name, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(if(scene.isPremium) "PREMIUM" else "FREE", color = if(scene.isPremium) StudioCyan else Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun AdvancedAdjustmentsPanel(adjustments: ImageAdjustments, isExpanded: Boolean, onToggle: () -> Unit, onUpdateAdjustments: (ImageAdjustments) -> Unit, onAutoEnhance: () -> Unit, onReset: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle), verticalAlignment = Alignment.CenterVertically) {
            Text("AJUSTES DE IMAGEM", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.Gray)
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onAutoEnhance, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = StudioCyan.copy(alpha = 0.1f), contentColor = StudioCyan)) {
                        Text("AUTO", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = onReset, modifier = Modifier.weight(1f)) {
                        Text("RESET", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                AdjustmentSlider("Brilho", adjustments.brightness, -50f, 50f) { onUpdateAdjustments(adjustments.copy(brightness = it)) }
                AdjustmentSlider("Contraste", adjustments.contrast, 0.5f, 1.5f) { onUpdateAdjustments(adjustments.copy(contrast = it)) }
                AdjustmentSlider("Saturação", adjustments.saturation, 0f, 2f) { onUpdateAdjustments(adjustments.copy(saturation = it)) }
            }
        }
    }
}

@Composable
fun AdjustmentSlider(label: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(String.format(java.util.Locale.getDefault(), "%.1f", value), color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = min..max, colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = StudioCyan))
    }
}
