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
                onNavigateToEditor = { navController.navigate("editor") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable("test") {
            TestScreen(onBack = { navController.popBackStack() }, viewModel = viewModel)
        }
        
        composable("editor") {
            EditorScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}



