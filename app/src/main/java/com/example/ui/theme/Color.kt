package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Futuristic Iron Man / JARVIS Palette
val VoidBlack = Color(0xFF040711)
val DeepSpace = Color(0xFF090E1E)
val CyberSurface = Color(0xFF0E162D)
val CyberSurfaceElevated = Color(0xFF142042)
val CyberBorder = Color(0xFF1D325E)

// Holographic Accents
val HolographicCyan = Color(0xFF00F5D4)
val ElectricBlue = Color(0xFF00E5FF)
val NeonCyan = Color(0xFF38E1FF)
val ArcReactorCore = Color(0xFF70F3FF)
val HighEnergyBlue = Color(0xFF0072FF)

// Functional Status Accents
val NeonAmber = Color(0xFFFFB703)
val NeonOrange = Color(0xFFFB8500)
val EmergencyRed = Color(0xFFFF2E63)
val SafeGreen = Color(0xFF00F59B)

// Text Tones
val TextWhite = Color(0xFFF0F6FC)
val TextMuted = Color(0xFF8B9CB5)
val TextCyan = Color(0xFF90F0FF)

// Glassmorphism overlays
val GlassOverlay = Color(0x2A00E5FF)
val GlassSurface = Color(0xB30A1124)
val GlassBorder = Color(0x4000F5D4)

// Glowing Gradients
val CoreGlowBrush = Brush.radialGradient(
    colors = listOf(
        ArcReactorCore,
        ElectricBlue.copy(alpha = 0.7f),
        HolographicCyan.copy(alpha = 0.2f),
        Color.Transparent
    )
)

val CyberBorderGradient = Brush.linearGradient(
    colors = listOf(
        ElectricBlue.copy(alpha = 0.8f),
        HolographicCyan.copy(alpha = 0.4f),
        Color(0xFF0044AA).copy(alpha = 0.2f)
    )
)
