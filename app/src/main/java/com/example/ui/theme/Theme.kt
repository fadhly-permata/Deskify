package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MacOSBlueDark,
    secondary = MacOSGraphite,
    tertiary = TrafficYellow,
    background = Color(0xFF141414),
    surface = MacOSDarkGlass,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = MacOSDarkTextPrimary,
    onSurface = MacOSDarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = MacOSBlue,
    secondary = MacOSGraphite,
    tertiary = TrafficYellow,
    background = Color(0xFFF2F2F7),
    surface = MacOSLightGlass,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = MacOSLightTextPrimary,
    onSurface = MacOSLightTextPrimary
)

@Composable
fun MacOSLauncherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
