package com.studiocar.studio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EliteColorScheme = darkColorScheme(
    primary = StudioCyan,
    onPrimary = StudioBlack,
    primaryContainer = StudioMetallicBlue,
    onPrimaryContainer = Color.White,
    secondary = StudioGold,
    onSecondary = StudioBlack,
    tertiary = SuccessGreen,
    background = DarkBackground,
    onBackground = Color.White,
    surface = StudioSurface,
    onSurface = Color.White,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = Color.Gray,
    outline = BorderColor,
    error = ErrorRed
)

@Composable
fun StudioCarTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = EliteColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            activity?.window?.let { window ->
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.background.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun android.content.Context.findActivity(): Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
