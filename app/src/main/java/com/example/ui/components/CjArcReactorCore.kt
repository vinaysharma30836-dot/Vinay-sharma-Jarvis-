package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ArcReactorCore
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.HolographicCyan
import com.example.ui.theme.NeonCyan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class CjCoreState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    EMERGENCY_STOP
}

@Composable
fun CjArcReactorCore(
    modifier: Modifier = Modifier,
    state: CjCoreState = CjCoreState.IDLE,
    audioAmplitude: Float = 0f,
    size: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CoreInfiniteTransition")

    // Continuous slow/fast rotation
    val rotationFast by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CjCoreState.THINKING -> 2000
                    CjCoreState.SPEAKING -> 4000
                    CjCoreState.LISTENING -> 3000
                    else -> 8000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationFast"
    )

    val rotationReverse by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CjCoreState.THINKING -> 2800
                    else -> 10000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationReverse"
    )

    // Pulsing Core Breathing
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    CjCoreState.LISTENING -> 700
                    CjCoreState.THINKING -> 500
                    CjCoreState.SPEAKING -> 850
                    else -> 1600
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val primaryColor = when (state) {
        CjCoreState.EMERGENCY_STOP -> EmergencyRed
        CjCoreState.LISTENING -> HolographicCyan
        CjCoreState.THINKING -> NeonCyan
        CjCoreState.SPEAKING -> ElectricBlue
        CjCoreState.IDLE -> ElectricBlue
    }

    val secondaryColor = when (state) {
        CjCoreState.EMERGENCY_STOP -> Color(0xFFFF85A0)
        CjCoreState.LISTENING -> Color(0xFF00FFC4)
        CjCoreState.THINKING -> Color(0xFF6AE2FF)
        CjCoreState.SPEAKING -> ArcReactorCore
        CjCoreState.IDLE -> HolographicCyan
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = this.size.minDimension / 2f * 0.9f
            val dynamicBoost = (audioAmplitude * 0.25f).coerceIn(0f, 0.3f)
            val currentRadius = baseRadius * (pulseScale + dynamicBoost)

            // Outer Atmospheric Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f),
                        secondaryColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.3f
                ),
                radius = currentRadius * 1.3f,
                center = center
            )

            // Outer Thin Track Ring
            drawCircle(
                color = primaryColor.copy(alpha = 0.4f),
                radius = currentRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Rotating Outer Segmentation Arc Ticks
            rotate(rotationFast, pivot = center) {
                val tickCount = 12
                val tickRadius = currentRadius
                for (i in 0 until tickCount) {
                    val angle = (i * (360f / tickCount)) * (PI / 180f)
                    val startX = center.x + (tickRadius - 8.dp.toPx()) * cos(angle).toFloat()
                    val startY = center.y + (tickRadius - 8.dp.toPx()) * sin(angle).toFloat()
                    val endX = center.x + (tickRadius + 4.dp.toPx()) * cos(angle).toFloat()
                    val endY = center.y + (tickRadius + 4.dp.toPx()) * sin(angle).toFloat()

                    drawLine(
                        color = if (i % 3 == 0) secondaryColor else primaryColor.copy(alpha = 0.6f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }
            }

            // Middle Counter-Rotating Geodesic Ring
            rotate(rotationReverse, pivot = center) {
                val midRadius = currentRadius * 0.72f
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.5f),
                    radius = midRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // 6 Concentric Hexagonal Orbits
                val hexCount = 6
                for (i in 0 until hexCount) {
                    val angle = (i * (360f / hexCount)) * (PI / 180f)
                    val nodeX = center.x + midRadius * cos(angle).toFloat()
                    val nodeY = center.y + midRadius * sin(angle).toFloat()
                    drawCircle(
                        color = primaryColor,
                        radius = 3.5.dp.toPx(),
                        center = Offset(nodeX, nodeY)
                    )
                }
            }

            // Inner Core Arc Reactor Chamber
            val innerRadius = currentRadius * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ArcReactorCore,
                        primaryColor,
                        secondaryColor.copy(alpha = 0.8f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = innerRadius
                ),
                radius = innerRadius,
                center = center
            )

            // High-Energy Center Dot
            drawCircle(
                color = Color.White,
                radius = innerRadius * 0.35f,
                center = center
            )
        }
    }
}
