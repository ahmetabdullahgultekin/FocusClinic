package com.focusclinic.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private const val CONFETTI_COUNT = 60
private const val ANIMATION_DURATION_MS = 2500

@Composable
fun CelebrationOverlay(
    visible: Boolean,
    message: String,
    emoji: String = "\uD83C\uDF89",
    onDismissed: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(visible) {
        if (visible) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ANIMATION_DURATION_MS,
                    easing = LinearEasing,
                ),
            )
            onDismissed()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)) + scaleIn(tween(300)),
        exit = fadeOut(tween(300)),
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            ConfettiCanvas(progress = progress.value)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp),
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.displayLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ConfettiCanvas(progress: Float) {
    val confetti = remember { generateConfetti() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        confetti.forEach { particle ->
            val x = particle.startX * canvasWidth +
                particle.driftX * canvasWidth * progress
            val y = particle.startY * canvasHeight * -0.2f +
                canvasHeight * progress * (0.8f + particle.speed * 0.4f)
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val particleSize = particle.size * (1f - progress * 0.3f)

            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particleSize,
                center = Offset(x, y),
            )
        }
    }
}

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val driftX: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
)

private val CONFETTI_COLORS = listOf(
    Color(0xFFFF6B6B),  // red
    Color(0xFF4ECDC4),  // teal
    Color(0xFFFFE66D),  // yellow
    Color(0xFF95E1D3),  // mint
    Color(0xFFF38181),  // coral
    Color(0xFF7C83FD),  // purple
    Color(0xFFFFC107),  // amber
    Color(0xFF00BCD4),  // cyan
)

private fun generateConfetti(): List<ConfettiParticle> {
    val random = Random(42)
    return List(CONFETTI_COUNT) {
        ConfettiParticle(
            startX = random.nextFloat(),
            startY = random.nextFloat(),
            driftX = (random.nextFloat() - 0.5f) * 0.4f,
            speed = 0.5f + random.nextFloat() * 0.5f,
            size = 3f + random.nextFloat() * 6f,
            color = CONFETTI_COLORS[random.nextInt(CONFETTI_COLORS.size)],
        )
    }
}
