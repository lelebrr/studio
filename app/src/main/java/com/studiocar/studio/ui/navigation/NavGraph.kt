package com.studiocar.studio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.studiocar.studio.ui.screens.*
import com.studiocar.studio.ui.viewmodels.EditorViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: EditorViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "camera"
    ) {
        composable("camera") {
            CameraScreen(
                onNavigateToEditor = { navController.navigate("editor") },
                onNavigateToBatchEditor = { navController.navigate("batch") }
            )
        }
        
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCamera = { navController.navigate("camera") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToHistory = { navController.navigate("history") }
            )
        }
        
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                settingsManager = com.studiocar.studio.utils.SettingsManager(androidx.compose.ui.platform.LocalContext.current),
                onNavigateToAbout = { navController.navigate("about") }
            )
        }

        composable("test") {
            TestScreen(onBack = { navController.popBackStack() }, viewModel = viewModel)
        }
        
        composable("editor") {
            EditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToHistory = { navController.navigate("history") }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToTour = { /* navController.navigate("tour") */ }
            )
        }
    }
}



