package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ArcReactorCore
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HolographicCyan

@Composable
fun AudioWaveformVisualizer(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    amplitude: Float = 0f,
    barCount: Int = 18,
    maxHeight: Dp = 40.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnim")

    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barAnim1"
    )

    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barAnim2"
    )

    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barAnim3"
    )

    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barGradients = Brush.verticalGradient(
            colors = listOf(
                ArcReactorCore,
                ElectricBlue,
                HolographicCyan
            )
        )

        for (i in 0 until barCount) {
            val baseAnim = when (i % 3) {
                0 -> anim1
                1 -> anim2
                else -> anim3
            }
            // Combine with actual live amplitude
            val heightFraction = if (isActive) {
                (baseAnim * 0.5f + amplitude * 0.7f).coerceIn(0.12f, 1.0f)
            } else {
                0.08f
            }

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .fillMaxHeight(fraction = heightFraction)
                    .background(
                        brush = if (isActive) barGradients else Brush.verticalGradient(
                            listOf(Color(0xFF223A60), Color(0xFF132238))
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
