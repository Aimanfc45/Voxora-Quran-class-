package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.GoldPrimary

@Composable
fun SubtleIslamicPattern(
    modifier: Modifier = Modifier,
    patternColor: Color = GoldPrimary.copy(alpha = 0.08f)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val step = 90f

        var x = 0f
        while (x < w + step) {
            var y = 0f
            while (y < h + step) {
                // Draw 8-point geometric star motif
                val radius = 24f
                val path = Path().apply {
                    // Outer square rotated 45
                    moveTo(x, y - radius)
                    lineTo(x + radius, y)
                    lineTo(x, y + radius)
                    lineTo(x - radius, y)
                    close()
                }
                drawPath(
                    path = path,
                    color = patternColor,
                    style = Stroke(width = 1.2f)
                )

                // Inner diamond
                val innerRadius = 16f
                val innerPath = Path().apply {
                    moveTo(x - innerRadius, y - innerRadius)
                    lineTo(x + innerRadius, y - innerRadius)
                    lineTo(x + innerRadius, y + innerRadius)
                    lineTo(x - innerRadius, y + innerRadius)
                    close()
                }
                drawPath(
                    path = innerPath,
                    color = patternColor,
                    style = Stroke(width = 0.8f)
                )

                // Connecting lines
                drawLine(
                    color = patternColor,
                    start = Offset(x, y - radius),
                    end = Offset(x, y + radius),
                    strokeWidth = 0.6f
                )
                drawLine(
                    color = patternColor,
                    start = Offset(x - radius, y),
                    end = Offset(x + radius, y),
                    strokeWidth = 0.6f
                )

                y += step
            }
            x += step
        }
    }
}
