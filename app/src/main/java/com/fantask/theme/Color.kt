package com.fantask.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Duolingo Light Theme Greens (calibrated, balanced, non-blinding)
val DuoLightGreen = Color(0xFF43A047) // Rich green with comfortable contrast
val DuoLightGreenShadow = Color(0xFF2E7D32)
val DuoLightGreenContainer = Color(0xFFE8F5E9)
val OnDuoLightGreen = Color(0xFFFFFFFF)

// Duolingo Night Mode Greens (darker forest green for sharp contrast with white text on dark surfaces)
val DuoDarkGreen = Color(0xFF2E7D32) // Deeper green, eliminates neon glare against #131F24, 4.8:1 contrast with white text
val DuoDarkGreenShadow = Color(0xFF1B5E20)
val DuoDarkGreenContainer = Color(0xFF1B3B22)
val OnDuoDarkGreen = Color(0xFFFFFFFF)
val DuoDarkGreenText = Color(0xFF81C784) // Soft mint green for text/icons on dark surfaces

/**
 * Adaptive DuoGreen that automatically switches to a deeper, darker green
 * when dark theme is active, ensuring text is clearly visible and readable.
 */
val DuoGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DuoDarkBackground) DuoDarkGreen else DuoLightGreen

val DuoGreenShadow: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DuoDarkBackground) DuoDarkGreenShadow else DuoLightGreenShadow

val DuoGreenContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DuoDarkBackground) DuoDarkGreenContainer else DuoLightGreenContainer

val OnDuoGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DuoDarkBackground) OnDuoDarkGreen else OnDuoLightGreen

val DuoGreenText: Color
    @Composable
    @ReadOnlyComposable
    get() = if (MaterialTheme.colorScheme.background == DuoDarkBackground) DuoDarkGreenText else DuoLightGreen

val DuoGold = Color(0xFFFFC800)
val DuoGoldShadow = Color(0xFFE5A500)
val DuoGoldContainer = Color(0xFFFFF5CC)
val OnDuoGold = Color(0xFF4B4B4B)

val DuoFlame = Color(0xFFFF9600)
val DuoFlameShadow = Color(0xFFE57300)
val DuoFlameContainer = Color(0xFFFFEAD1)

val DuoBlue = Color(0xFF1CB0F6)
val DuoBlueShadow = Color(0xFF1899D6)
val DuoBlueContainer = Color(0xFFDDF4FF)

val DuoPurple = Color(0xFFCE82FF)
val DuoPurpleShadow = Color(0xFFA55FE5)
val DuoPurpleContainer = Color(0xFFF5E5FF)

val DuoRed = Color(0xFFFF4B4B)
val DuoRedShadow = Color(0xFFEA2B2B)
val DuoRedContainer = Color(0xFFFFDCDC)

// Duolingo Light Theme
val DuoLightBackground = Color(0xFFF7F7F7)
val DuoLightSurface = Color(0xFFFFFFFF)
val DuoLightSurfaceVariant = Color(0xFFEFEFEF)
val DuoLightBorder = Color(0xFFE5E5E5)
val DuoLightTextPrimary = Color(0xFF3C3C3C)
val DuoLightTextSecondary = Color(0xFF777777)

// Duolingo Night Mode
val DuoDarkBackground = Color(0xFF131F24)
val DuoDarkSurface = Color(0xFF18282F)
val DuoDarkSurfaceVariant = Color(0xFF20313A)
val DuoDarkBorder = Color(0xFF2B3D47)
val DuoDarkTextPrimary = Color(0xFFFFFFFF)
val DuoDarkTextSecondary = Color(0xFFAFAFAF)

// Legacy alias mappings for difficulty chips & gamification
val PointsGold = DuoGold
val StreakFlame = DuoFlame
val MediumBlue = DuoBlue
val HardPurple = DuoPurple
val SuperHardRed = DuoRed
val EasyGreen: Color
    @Composable
    @ReadOnlyComposable
    get() = DuoGreen
