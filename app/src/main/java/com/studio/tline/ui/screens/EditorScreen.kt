package com.studio.tline.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.studio.tline.R
import com.studio.tline.models.EditOptions
import com.studio.tline.ui.theme.TLineStudioTheme
import com.studio.tline.ui.viewmodels.EditorViewModel
import com.studio.tline.utils.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    onBack: () -> Unit,
    viewModel: EditorViewModel
) {
    val context = LocalContext.current
    val settingsManager = SettingsManager(context)
    val originalBitmap by viewModel.originalBitmap.collectAsState(initial = null)
    val resultBitmap by viewModel.resultBitmap.collectAsState(initial = null)
    val processingStage by viewModel.processingStage.collectAsState(initial = EditorViewModel.ProcessingStage.IDLE)
    val options by viewModel.options.collectAsState(initial = EditOptions())

    TLineStudioTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Editor de Imagem") })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (originalBitmap != null) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Imagem Original",
                        modifier = Modifier
                            .size(300.dp)
                            .aspectRatio(1f)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    Text(text = "Nenhuma imagem selecionada")
                }

                if (processingStage != EditorViewModel.ProcessingStage.IDLE) {
                    LinearProgressIndicator()
                }

                if (resultBitmap != null) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Imagem Processada",
                        modifier = Modifier
                            .size(300.dp)
                            .aspectRatio(1f)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    Text(text = "Processamento em andamento ou concluído")
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        viewModel.setOriginalImage(null)
                        viewModel.updateOptions(EditOptions())
                        onBack()
                    }) {
                        Text("Voltar")
                    }

                    Button(onClick = {
                        if (processingStage == EditorViewModel.ProcessingStage.IDLE) {
                            viewModel.processImage(context)
                        }
                    }) {
                        Text("Processar")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EditorScreenPreview() {
    TLineStudioTheme {
        EditorScreen(onBack = {}, viewModel = EditorViewModel())
    }
}