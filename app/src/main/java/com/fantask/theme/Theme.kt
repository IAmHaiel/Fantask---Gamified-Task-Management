package com.fantask.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DuolingoNightColorScheme = darkColorScheme(
    primary = DuoDarkGreen,
    onPrimary = OnDuoDarkGreen,
    primaryContainer = DuoDarkGreenContainer,
    onPrimaryContainer = DuoDarkGreenText,
    secondary = DuoBlue,
    onSecondary = Color.White,
    secondaryContainer = DuoDarkSurfaceVariant,
    onSecondaryContainer = DuoBlue,
    tertiary = DuoGold,
    background = DuoDarkBackground,
    surface = DuoDarkSurface,
    surfaceVariant = DuoDarkSurfaceVariant,
    onBackground = DuoDarkTextPrimary,
    onSurface = DuoDarkTextPrimary,
    onSurfaceVariant = DuoDarkTextSecondary,
    outline = DuoDarkBorder
)

private val DuolingoLightColorScheme = lightColorScheme(
    primary = DuoLightGreen,
    onPrimary = OnDuoLightGreen,
    primaryContainer = DuoLightGreenContainer,
    onPrimaryContainer = DuoLightGreenShadow,
    secondary = DuoBlue,
    onSecondary = Color.White,
    secondaryContainer = DuoBlueContainer,
    onSecondaryContainer = DuoBlueShadow,
    tertiary = DuoGold,
    background = DuoLightBackground,
    surface = DuoLightSurface,
    surfaceVariant = DuoLightSurfaceVariant,
    onBackground = DuoLightTextPrimary,
    onSurface = DuoLightTextPrimary,
    onSurfaceVariant = DuoLightTextSecondary,
    outline = DuoLightBorder
)

@Composable
fun FantaskTheme(
    themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    dynamicColor: Boolean = false, // Keep consistent Duolingo branding
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode.uppercase()) {
        "LIGHT" -> false
        "DARK" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DuolingoNightColorScheme else DuolingoLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
