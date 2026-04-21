package com.studio.tline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studio.tline.ui.screens.*
import com.studio.tline.ui.theme.TLineStudioTheme
import com.studio.tline.ui.viewmodels.EditorViewModel
import com.studio.tline.utils.SettingsManager
import kotlinx.coroutines.launch

/**
 * MainActivity V1.0.0 - Versão Final de Produção.
 */
class MainActivity : ComponentActivity() {

    private val editorViewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        installSplashScreen()
        enableEdgeToEdge()
        
        val settingsManager = SettingsManager(this)

        setContent {
            TLineStudioTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()

                val apiKey by settingsManager.apiKey.collectAsState(initial = null)

                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainScreen(
                            editorViewModel = editorViewModel,
                            onNavigateToEditor = { 
                                if (apiKey.isNullOrBlank()) {
                                    navController.navigate("settings")
                                } else {
                                    navController.navigate("editor")
                                }
                            },
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToAbout = { /* Navegação para About se desejado futuramente */ }
                        )
                    }
                    composable("editor") {
                        EditorScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = editorViewModel
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            onBack = { navController.popBackStack() },
                            settingsManager = settingsManager
                        )
                    }
                }
            }
        }
    }
}