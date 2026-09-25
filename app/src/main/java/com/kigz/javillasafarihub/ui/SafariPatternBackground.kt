package com.kigz.javillasafarihub.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

enum class SafariPattern { ZEBRA, LEOPARD, GIRAFFE, ELEPHANT, SAVANNAH }

@Composable
fun SafariPatternBackground(pattern: SafariPattern) {
    val base = Color.Transparent
    Canvas(Modifier.fillMaxSize()) {
        when (pattern) {
            SafariPattern.ZEBRA -> {
                val stripe = 34f
                var x = -size.height
                while (x < size.width + size.height) {
                    drawLine(Color(0x18000000), Offset(x, 0f), Offset(x + size.height, size.height), strokeWidth = stripe)
                    drawLine(Color(0x0CFFFFFF), Offset(x + stripe, 0f), Offset(x + stripe + size.height, size.height), strokeWidth = 7f)
                    x += 85f
                }
            }
            SafariPattern.LEOPARD -> {
                val step = 78f
                var y = 20f
                while (y < size.height) {
                    var x = 20f
                    while (x < size.width) {
                        val j = sin((x + y) / 90f) * 12f
                        drawCircle(Color(0x18000000), radius = 18f, center = Offset(x + j, y), style = Stroke(width = 8f))
                        drawCircle(Color(0x12000000), radius = 5f, center = Offset(x + j, y))
                        x += step
                    }
                    y += step
                }
            }
            SafariPattern.GIRAFFE -> {
                val step = 105f
                var y = -20f
                while (y < size.height + step) {
                    var x = -20f
                    while (x < size.width + step) {
                        val points = listOf(
                            Offset(x, y), Offset(x + 45f, y - 12f),
                            Offset(x + 72f, y + 22f), Offset(x + 30f, y + 58f),
                            Offset(x - 8f, y + 35f)
                        )
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(points[0].x, points[0].y)
                            points.drop(1).forEach { lineTo(it.x, it.y) }
                            close()
                        }
                        drawPath(path, Color(0x14000000))
                        x += step
                    }
                    y += 78f
                }
            }
            SafariPattern.ELEPHANT -> {
                val step = 70f
                var y = 0f
                while (y < size.height) {
                    drawLine(Color(0x14000000), Offset(0f, y), Offset(size.width, y + 22f), strokeWidth = 2f)
                    drawLine(Color(0x0E000000), Offset(0f, y + 35f), Offset(size.width, y + 8f), strokeWidth = 2f)
                    y += step
                }
            }
            SafariPattern.SAVANNAH -> {
                // Subtle organic contour lines.
                var y = 45f
                while (y < size.height) {
                    drawOval(Color(0x12000000), topLeft = Offset(-40f, y), size = androidx.compose.ui.geometry.Size(size.width + 80f, 80f), style = Stroke(width = 2f))
                    y += 110f
                }
            }
        }
    }
}
