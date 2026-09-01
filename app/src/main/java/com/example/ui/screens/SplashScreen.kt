package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SubtleIslamicPattern
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation transitions
    val transitionState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) {
        transitionState.targetState = true
        // Keep splash short & snappy (~1350ms total) for state synchronization
        delay(1350)
        onSplashFinished()
    }

    val transition = rememberTransition(transitionState, label = "splash_transition")

    val logoScale by transition.animateFloat(
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "logo_scale"
    ) { state ->
        if (state) 1f else 0.7f
    }

    val logoAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 650, easing = FastOutSlowInEasing)
        },
        label = "logo_alpha"
    ) { state ->
        if (state) 1f else 0f
    }

    val textAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 700, delayMillis = 250, easing = LinearOutSlowInEasing)
        },
        label = "text_alpha"
    ) { state ->
        if (state) 1f else 0f
    }

    val taglineAlpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 600, delayMillis = 400, easing = LinearOutSlowInEasing)
        },
        label = "tagline_alpha"
    ) { state ->
        if (state) 1f else 0f
    }

    // Infinite breathing pulse for gold aura
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen"),
        color = DeepEmerald950
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Emerald900.copy(alpha = 0.85f),
                            DeepEmerald950
                        ),
                        radius = 1200f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Subtle Islamic Geometric Pattern overlay
            SubtleIslamicPattern(
                modifier = Modifier.fillMaxSize(),
                patternColor = GoldPrimary.copy(alpha = 0.07f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Animated Logo Emblem
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(150.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                ) {
                    // Outer Pulsing Gold Aura Ring
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .border(1.5.dp, Gold400.copy(alpha = pulseAlpha), CircleShape)
                    )

                    // Middle Emerald-Gold Layered Circle
                    Box(
                        modifier = Modifier
                            .size(116.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Emerald800,
                                        Emerald950
                                    )
                                )
                            )
                            .border(2.5.dp, Gold500, CircleShape)
                    )

                    // Inner Emblem Graphic (Golden Quran Book & Mihrab Motif)
                    Canvas(modifier = Modifier.size(64.dp)) {
                        val w = size.width
                        val h = size.height

                        // Draw stylized open Quran pages & star
                        val leftPage = Path().apply {
                            moveTo(w * 0.5f, h * 0.72f)
                            cubicTo(w * 0.35f, h * 0.75f, w * 0.15f, h * 0.68f, w * 0.12f, h * 0.32f)
                            cubicTo(w * 0.25f, h * 0.28f, w * 0.42f, h * 0.32f, w * 0.5f, h * 0.38f)
                            close()
                        }
                        drawPath(
                            path = leftPage,
                            color = Gold400
                        )

                        val rightPage = Path().apply {
                            moveTo(w * 0.5f, h * 0.72f)
                            cubicTo(w * 0.65f, h * 0.75f, w * 0.85f, h * 0.68f, w * 0.88f, h * 0.32f)
                            cubicTo(w * 0.75f, h * 0.28f, w * 0.58f, h * 0.32f, w * 0.5f, h * 0.38f)
                            close()
                        }
                        drawPath(
                            path = rightPage,
                            color = Gold300
                        )

                        // Central spine stroke
                        drawLine(
                            color = DeepEmerald950,
                            start = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f),
                            end = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.75f),
                            strokeWidth = 3f
                        )

                        // Islamic 8-point gold star on top
                        val starPath = Path().apply {
                            val cx = w * 0.5f
                            val cy = h * 0.22f
                            val r1 = 8f
                            val r2 = 4f
                            for (i in 0 until 16) {
                                val r = if (i % 2 == 0) r1 else r2
                                val angle = (i * Math.PI / 8) - (Math.PI / 2)
                                val px = cx + (r * Math.cos(angle)).toFloat()
                                val py = cy + (r * Math.sin(angle)).toFloat()
                                if (i == 0) moveTo(px, py) else lineTo(px, py)
                            }
                            close()
                        }
                        drawPath(
                            path = starPath,
                            color = Gold400
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Name
                Text(
                    text = "VOXORA QURAN",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        color = Gold400,
                        fontFamily = FontFamily.SansSerif
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(textAlpha)
                        .testTag("splash_brand_title")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Arabic Calligraphy accent
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold300.copy(alpha = 0.85f),
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(taglineAlpha)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tagline
                Text(
                    text = "Learn. Recite. Grow.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.8.sp,
                        color = Emerald200.copy(alpha = 0.9f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .alpha(taglineAlpha)
                        .testTag("splash_tagline")
                )
            }

            // Bottom loading indicator & copyright
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp)
                    .alpha(taglineAlpha),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val dotAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = index * 180, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dot_alpha_$index"
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Gold500.copy(alpha = dotAlpha))
                        )
                    }
                }
            }
        }
    }
}
