package com.studio.tline.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.studio.tline.ui.screens.*
import com.studio.tline.ui.viewmodels.EditorViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: EditorViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                editorViewModel = viewModel,
                onNavigateToEditor = { navController.navigate("editor") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAbout = { navController.navigate("about") }
            )
        }
        
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        
        composable("editor") {
            EditorScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
