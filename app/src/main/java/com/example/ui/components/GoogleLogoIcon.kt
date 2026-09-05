package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Clean, standard Google "G" four-color vector mark.
 * Colors adhere to official Google Branding Guidelines:
 * Red: #EA4335, Yellow: #FBBC05, Green: #34A853, Blue: #4285F4.
 */
@Composable
fun GoogleLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = w * 0.22f
        val center = Offset(w / 2f, h / 2f)

        // Arc bounding box
        val arcTopLeft = Offset(stroke / 2f, stroke / 2f)
        val arcSize = Size(w - stroke, h - stroke)

        // Red segment (top arc)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 210f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )

        // Yellow segment (left arc)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 135f,
            sweepAngle = 75f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )

        // Green segment (bottom arc)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 30f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )

        // Blue segment (right arc)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 0f,
            sweepAngle = 30f,
            useCenter = false,
            topLeft = arcTopLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )

        // Blue horizontal crossbar to center
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x, center.y),
            end = Offset(w - stroke * 0.4f, center.y),
            strokeWidth = stroke,
            cap = StrokeCap.Square
        )
    }
}
