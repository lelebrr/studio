package com.studiocar.studio.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import com.studiocar.studio.ui.theme.*
import com.studiocar.studio.ui.components.*
import com.studiocar.studio.ui.components.AIProvidersBottomSheet
import com.studiocar.studio.ui.viewmodels.EditorViewModel
import com.studiocar.studio.utils.*
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.geometry.Offset
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
    val sheetState = rememberModalBottomSheetState()
    
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
    
    // Novas flows de ajuste
    val adjustments by viewModel.adjustments.collectAsState()
    val lightStyle by viewModel.lightStyle.collectAsState()
    val adjustedBitmap by viewModel.adjustedBitmap.collectAsState()
    
    var showAdjustments by remember { mutableStateOf(false) }

    val bgLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { viewModel.addCustomBackground(context, it) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0F0F0F)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                EditorTopBar(
                    isElite = options.isDealershipMode,
                    onBack = onBack,
                    onHistory = onNavigateToHistory,
                    viewModel = viewModel
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
            // --- AI PROVIDERS SHEET ---
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
                // 1. Preview Window (#3)
                PreviewWindow(
                    original = originalBitmap,
                    result = adjustedBitmap ?: resultBitmap,
                    stage = processingStage
                )

                // 2. Metadata Quick View (#2, #21)
                MetadataPanel(
                    vinInfo = vinInfo,
                    isExpanded = showMetaExpanded
                ) { showMetaExpanded = !showMetaExpanded }

                // 3. AI Feature Toggles (#9, #10, #13, #15, #18, #SAM2)
                FeaturesPanel(options = options) {
                    viewModel.updateOptions(it)
                }

                // 4. Studio Controls (#4, #5)
                StudioControls(
                    currentBg = options.background,
                    currentFloor = options.floor,
                    currentStudio = options.selectedStudioScene,
                    onSelectBg = { showBgSheet = true },
                    onSelectFloor = { showFloorSheet = true }
                ) { showStudioSheet = true }

                // 5. Advanced Adjustments (#2026)
                if (resultBitmap != null) {
                    AdvancedAdjustmentsPanel(
                        adjustments = adjustments,
                        lightStyle = lightStyle,
                        isExpanded = showAdjustments,
                        onToggle = { showAdjustments = !showAdjustments },
                        onUpdateAdjustments = { viewModel.updateAdjustments(it) },
                        onSelectLight = { viewModel.setLightStyle(it) },
                        onAutoEnhance = { viewModel.autoEnhance() },
                        onReset = { viewModel.resetAdjustments() }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Selection Sheets
        if (showBgSheet) {
            ModalBottomSheet(onDismissRequest = { showBgSheet = false }, containerColor = Color(0xFF151515)) {
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
            ModalBottomSheet(onDismissRequest = { showFloorSheet = false }, containerColor = Color(0xFF151515)) {
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
                containerColor = Color(0xFF0F0F0F),
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

        // AI Caption Dialog (#11)
        if (showCaptionDialog) {
            CaptionDialog(
                caption = generatedCaption,
                onDismiss = { showCaptionDialog = false },
                onCopy = { 
                    // Clipboard logic
                    showCaptionDialog = false
                }
            )
        }
    }
}

@Composable
fun PreviewWindow(
    original: Bitmap?, 
    result: Bitmap?, 
    stage: EditorViewModel.ProcessingStage,
    promptPoints: List<Pair<Offset, Boolean>> = emptyList(),
    onPointAdd: (Offset, Boolean) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .premiumEntrance() // Entrada elegante
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset -> onPointAdd(offset, true) },
                    onLongPress = { offset -> onPointAdd(offset, false) }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Transição suave entre original e resultado
        AnimatedContent(
            targetState = result ?: original,
            transitionSpec = {
                (fadeIn(animationSpec = StudioCarAnimations.PremiumTween) + scaleIn(initialScale = 0.95f))
                    .togetherWith(fadeOut(animationSpec = StudioCarAnimations.PremiumTween))
            },
            label = "preview_transition"
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

        // Desenha os pontos de prompt na tela
        promptPoints.forEach { (offset, isPositive) ->
            Box(
                modifier = Modifier
                    .offset(offset.x.dp, offset.y.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isPositive) StudioCyan else Color.Red)
            )
        }

        // Loading Premium
        AnimatedVisibility(
            visible = stage != EditorViewModel.ProcessingStage.IDLE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StudioPulseLoading(
                        message = when(stage) {
                            EditorViewModel.ProcessingStage.SEGMENTING -> "RECONHECENDO VEÍCULO..."
                            EditorViewModel.ProcessingStage.SAM2_REFINING -> "SAM 2 REFINANDO..."
                            EditorViewModel.ProcessingStage.GLASS_REFINEMENT -> "RECONSTRUINDO VIDROS..."
                            EditorViewModel.ProcessingStage.POLISHING -> "FLUX: POLIMENTO E REFLEXOS..."
                            EditorViewModel.ProcessingStage.DONE -> "CONCLUÍDO!"
                            else -> "IA PROCESSANDO..."
                        }
                    )
                    
                    val currentProvider by viewModel.currentProviderName.collectAsState()
                    if (currentProvider != null && stage != EditorViewModel.ProcessingStage.IDLE) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "USANDO: ${currentProvider?.uppercase(java.util.Locale.ROOT)}",
                            color = Color.Cyan.copy(alpha = 0.6f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetadataPanel(vinInfo: com.studiocar.studio.data.models.EditedCar?, isExpanded: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable(onClick = onToggle),
        color = Color(0xFF151515),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = vinInfo?.let { "${it.carBrand} ${it.carModel} (${it.carYear})" } ?: "Veículo não identificado",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(if(isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.Gray)
            }
            if (isExpanded && vinInfo != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MetaItem("VIN", vinInfo.vinCode ?: "N/A")
                    MetaItem("COR", vinInfo.carColor ?: "N/A")
                    MetaItem("CATEGORIA", "Premium")
                }
            }
        }
    }
}

@Composable
private fun MetaItem(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FeaturesPanel(options: com.studiocar.studio.data.models.EditOptions, onOptionToggle: (com.studiocar.studio.data.models.EditOptions) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("REFINAMENTO INTELIGENTE (IA)", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureChip("SAM 2 ULTRA", options.isSam2UltraEnabled, { onOptionToggle(options.copy(isSam2UltraEnabled = it)) })
            FeatureChip("PREMIUM", options.isUltraQuality, { onOptionToggle(options.copy(isUltraQuality = it)) })
            FeatureChip("SOMBRAS", options.autoShadows, { onOptionToggle(options.copy(autoShadows = it)) })
            FeatureChip("REFRAÇÃO+", options.advancedGlassRefraction, { onOptionToggle(options.copy(advancedGlassRefraction = it)) })
            FeatureChip("LIMPEZA", options.removeUnwantedObjects, { onOptionToggle(options.copy(removeUnwantedObjects = it)) })
            FeatureChip("NIGHT", options.nightMode, { onOptionToggle(options.copy(nightMode = it)) })
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
            selectedContainerColor = Color.Cyan,
            selectedLabelColor = Color.Black,
            containerColor = Color(0xFF222222),
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
        // New Premium Selector
        OptionCard(
            title = "ESTÚDIOS COMPLETOS (IA)",
            selected = currentStudio?.name ?: "Nenhum estúdio selecionado",
            icon = Icons.Default.AutoAwesome,
            modifier = Modifier.fillMaxWidth(),
            onClick = onSelectStudio,
            isPremium = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OptionCard(title = "CENÁRIO", selected = currentBg.description, icon = Icons.Default.Landscape, modifier = Modifier.weight(1f), onClick = onSelectBg)
            OptionCard(title = "PISO", selected = currentFloor.description, icon = Icons.Default.Layers, modifier = Modifier.weight(1f), onClick = onSelectFloor)
        }
    }
}

@Composable
fun EditorTopBar(isElite: Boolean, onBack: () -> Unit, onHistory: () -> Unit, viewModel: EditorViewModel) {
    CenterAlignedTopAppBar(
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(isElite) Icon(Icons.Default.Stars, null, tint = Color.Cyan, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                Text("STUDIOCAR PRO", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
        actions = {
            IconButton(onClick = onHistory) { Icon(Icons.Default.History, null, tint = Color.White) }
            IconButton(onClick = { viewModel.setShowAiProvidersSheet(true) }) {
                Icon(Icons.Default.Settings, "Configurações de IA", tint = Color.Cyan)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent, titleContentColor = Color.White)
    )
}

@Composable
fun EditorBottomBar(isProcessing: Boolean, hasResult: Boolean, onGenerate: () -> Unit, onCaption: () -> Unit, onSave: () -> Unit) {
    Surface(
        color = Color(0xFF121212),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasResult) {
                IconButton(onClick = onCaption, modifier = Modifier.size(52.dp).clip(CircleShape).background(Color(0xFF222222))) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.Cyan)
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SALVAR 4K", fontWeight = FontWeight.Black)
                }
            } else {
                Button(
                    onClick = onGenerate,
                    enabled = !isProcessing,
                    modifier = Modifier.weight(1f).height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isProcessing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                    else Text("GERAR ESTÚDIO IA", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
@Composable
fun OptionCard(
    title: String,
    selected: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPremium: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(72.dp)
            .pulseSelection(isPremium && selected != "Nenhum estúdio selecionado"), // Glow sutil se for premium
        color = Color(0xFF151515),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isPremium) StudioCyan.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPremium) Color.Cyan.copy(alpha = 0.1f) else Color(0xFF222222)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPremium) Color.Cyan else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isPremium) Color.Cyan else Color.Gray,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = selected,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun StudioSceneSelectionGrid(
    selectedScene: com.studiocar.studio.data.models.StudioScene?,
    onSelect: (com.studiocar.studio.data.models.StudioScene) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(com.studiocar.studio.data.models.SceneCategory.SHOWROOM) }
    val scenes = remember(selectedCategory) {
        com.studiocar.studio.data.StudioScenes.allScenes.filter { it.category == selectedCategory }
    }

    Column(modifier = Modifier.fillMaxHeight(0.85f).padding(16.dp)) {
        Text(
            text = "ESTÚDIOS PROFISSIONAIS",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Category Tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            com.studiocar.studio.data.models.SceneCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category.label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color.Cyan,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF151515),
                        labelColor = Color.Gray
                    ),
                    border = null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(scenes) { scene ->
                StudioSceneCard(
                    scene = scene,
                    isSelected = selectedScene?.id == scene.id,
                    onClick = { onSelect(scene) }
                )
            }
        }
    }
}

@Composable
fun StudioSceneCard(
    scene: com.studiocar.studio.data.models.StudioScene,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .pulseSelection(isSelected) // Pulso ao selecionar cenário
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF151515))
            .border(
                width = 2.dp,
                color = if (isSelected) StudioCyan else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color(0xFF222222))) {
            AsyncImage(
                model = "file:///android_asset/${scene.imageAsset}",
                contentDescription = scene.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            if (scene.isRecommended) {
                    Surface(
                        color = Color.Cyan,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            "TOP",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
            }
        }
        
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = scene.name,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = if(scene.isPremium) "PREMIUM" else "FREE",
                color = if(scene.isPremium) Color.Cyan else Color.Gray,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
@Composable
fun AdvancedAdjustmentsPanel(
    adjustments: ImageAdjustments,
    lightStyle: DirectionalLightStyle?,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onUpdateAdjustments: (ImageAdjustments) -> Unit,
    onSelectLight: (DirectionalLightStyle?) -> Unit,
    onAutoEnhance: () -> Unit,
    onReset: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "AJUSTES AVANÇADOS",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                null,
                tint = Color.Gray
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                // Main Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAutoEnhance,
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan.copy(alpha = 0.1f), contentColor = Color.Cyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AutoFixHigh, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AUTO AJUSTAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1f).height(40.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("RESETAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Sliders
                AdjustmentSlider("Brilho", adjustments.brightness, -50f, 50f) { onUpdateAdjustments(adjustments.copy(brightness = it)) }
                AdjustmentSlider("Contraste", adjustments.contrast, 0.5f, 1.5f) { onUpdateAdjustments(adjustments.copy(contrast = it)) }
                AdjustmentSlider("Saturação", adjustments.saturation, 0f, 2f) { onUpdateAdjustments(adjustments.copy(saturation = it)) }
                AdjustmentSlider("Exposição", adjustments.exposure, -2f, 2f) { onUpdateAdjustments(adjustments.copy(exposure = it)) }
                AdjustmentSlider("Temperatura", adjustments.temperature, -100f, 100f) { onUpdateAdjustments(adjustments.copy(temperature = it)) }
                AdjustmentSlider("Nitidez", adjustments.sharpen, 0f, 100f) { onUpdateAdjustments(adjustments.copy(sharpen = it)) }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Directional Light
                Text("ILUMINAÇÃO DIRECIONAL", color = Color.White.copy(alpha = 0.3f), fontSize = 9.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        LightChip("Nenhuma", lightStyle == null) { onSelectLight(null) }
                    }
                    items(DirectionalLightStyle.entries) { style ->
                        LightChip(style.label, lightStyle == style) { onSelectLight(style) }
                    }
                }
            }
        }
    }
}

@Composable
fun AdjustmentSlider(label: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = StudioCarAnimations.ResponsiveSpring,
        label = "slider_value"
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(
                String.format("%.1f", animatedValue),
                color = StudioCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = StudioCyan,
                inactiveTrackColor = Color.DarkGray
            )
        )
    }
}

@Composable
fun LightChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) Color.Cyan else Color(0xFF222222),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
            Text(label, color = if (selected) Color.Black else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
