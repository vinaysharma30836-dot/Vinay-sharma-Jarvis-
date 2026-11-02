package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = VoidBlack,
    primaryContainer = CyberSurfaceElevated,
    onPrimaryContainer = HolographicCyan,
    secondary = HolographicCyan,
    onSecondary = VoidBlack,
    secondaryContainer = CyberSurface,
    onSecondaryContainer = TextWhite,
    tertiary = NeonCyan,
    onTertiary = VoidBlack,
    background = VoidBlack,
    onBackground = TextWhite,
    surface = DeepSpace,
    onSurface = TextWhite,
    surfaceVariant = CyberSurface,
    onSurfaceVariant = TextMuted,
    outline = CyberBorder,
    outlineVariant = GlassBorder,
    error = EmergencyRed,
    onError = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = HighEnergyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E8FF),
    onPrimaryContainer = VoidBlack,
    secondary = Color(0xFF009688),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCEFAF4),
    onSecondaryContainer = VoidBlack,
    tertiary = Color(0xFF007A99),
    onTertiary = Color.White,
    background = Color(0xFFF3F7FC),
    onBackground = Color(0xFF090E1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF090E1E),
    surfaceVariant = Color(0xFFE2EAF5),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFB0C4DE),
    outlineVariant = Color(0xFFD0E0F0),
    error = EmergencyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Futuristic dark mode as default per user request
    dynamicColor: Boolean = false, // Keep immersive holographic cyan/blue branding consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
