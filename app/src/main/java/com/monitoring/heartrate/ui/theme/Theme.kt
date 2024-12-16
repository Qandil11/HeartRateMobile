package com.monitoring.heartrate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.monitoring.heartrate.ui.ui.theme.Typography

private val LightColors = lightColorScheme(
    primary = Color(0xFFD32F2F), // Slightly deeper red
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF690000),
    secondary = Color(0xFF7B1FA2), // Purple shade for secondary
    onSecondary = Color.White,
    tertiary = Color(0xFF388E3C), // Green tone for tertiary actions
    onTertiary = Color.White,
    background = Color(0xFFFCFDFE),
    onBackground = Color(0xFF1B1B1B),
    surface = Color(0xFFFFFBFA),
    onSurface = Color(0xFF1B1B1B),
    error = Color(0xFFD50000),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF5252), // Lighter red for dark theme
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFD32F2F),
    onPrimaryContainer = Color(0xFFFFCDD2),
    secondary = Color(0xFFBA68C8), // Purple light shade
    onSecondary = Color.Black,
    tertiary = Color(0xFF81C784), // Softer green for actions
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun HeartMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Turned off for unique theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}
