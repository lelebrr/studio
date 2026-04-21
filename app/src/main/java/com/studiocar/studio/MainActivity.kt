package com.studiocar.studio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studiocar.studio.ui.screens.*
import com.studiocar.studio.ui.theme.StudioCarTheme
import com.studiocar.studio.ui.viewmodels.EditorViewModel
import com.studiocar.studio.utils.SettingsManager

/**
 * MainActivity V2.1.1 - StudioCar Elite Professional Suite.
 * Core orchestrator for the B2B professional photography workflow.
 */
@Suppress("unused")
class MainActivity : ComponentActivity() {

    private val editorViewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        installSplashScreen()
        enableEdgeToEdge()
        
        val settingsManager = SettingsManager(this)

        setContent {
            StudioCarTheme {
                val navController = rememberNavController()
                
                // Load settings on startup
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    editorViewModel.loadSettings(context)
                }

                NavGraph(
                    navController = navController,
                    viewModel = editorViewModel,
                    settingsManager = settingsManager
                )
            }
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: EditorViewModel,
    settingsManager: SettingsManager
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToHistory = { navController.navigate("history") }
            )
        }

        composable("camera") {
            CameraScreen(
                viewModel = viewModel,
                onNavigateToEditor = { navController.navigate("editor") },
                onNavigateToBatchEditor = { navController.navigate("batch") }
            )
        }
        
        composable("editor") {
            EditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate("history") }
            )
        }
        
        composable("batch") {
            BatchEditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("tour") {
            VirtualTourScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onNavigateToTour = { navController.navigate("tour") },
                viewModel = viewModel
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                settingsManager = settingsManager,
                onNavigateToAbout = { navController.navigate("about") }
            )
        }

        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
