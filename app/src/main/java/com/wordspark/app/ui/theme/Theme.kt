package com.wordspark.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = WordSparkOrangeDark,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkOnSurface,
    secondary = WordSparkGold,
    onSecondary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface,
)

private val LightColorScheme = lightColorScheme(
    primary = WordSparkOrange,
    onPrimary = LightBackground,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightOnSurface,
    secondary = WordSparkGold,
    onSecondary = LightBackground,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface,
)

@Composable
fun WordSparkTheme(
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
