package com.studio.tline.ui.navigation

/**
 * Definições de rotas para o NavHost.
 */
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Editor : Screen("editor")
    
    // Sub-rotas da Main (Tabs)
    object Camera : Screen("camera")
    object Gallery : Screen("gallery")
    object History : Screen("history")
}
