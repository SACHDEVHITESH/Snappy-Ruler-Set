package com.example.snappyrulerset.ui

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.snappyrulerset.models.Shape
import com.example.snappyrulerset.models.Tool
import com.example.snappyrulerset.models.ToolType
import com.example.snappyrulerset.viewmodel.DrawingViewModel

/* ---------------- Background Grid ---------------- */

fun DrawScope.drawBackgroundGrid(vm: DrawingViewModel) {
    val gridPx = vm.snapEngine.gridSpacingPx()
    if (gridPx > 6f) {
        val w = size.width
        val h = size.height
        val cols = (w / gridPx).toInt() + 2
        for (i in 0 until cols) {
            val x = i * gridPx
            drawLine(Color(0xFFEAEAEA), Offset(x, 0f), Offset(x, h), 1f)
        }
        val rows = (h / gridPx).toInt() + 2
        for (i in 0 until rows) {
            val y = i * gridPx
            drawLine(Color(0xFFEAEAEA), Offset(0f, y), Offset(w, y), 1f)
        }
    }
}

/* ---------------- Shapes ---------------- */

fun DrawScope.drawShapes(vm: DrawingViewModel) {
    for (s in vm.shapes) {
        when (s) {
            is Shape.FreePath -> {
                if (s.points.size > 1) {
                    val path = Path().apply {
                        moveTo(s.points[0].x, s.points[0].y)
                        for (i in 1 until s.points.size) {
                            lineTo(s.points[i].x, s.points[i].y)
                        }
                    }
                    drawPath(path, s.color, style = Stroke(s.strokeWidth))
                }
            }
            is Shape.LineSeg -> drawLine(s.color, s.start, s.end, s.strokeWidth)
            is Shape.CircleSeg -> drawCircle(
                s.color,
                s.radius,
                s.center,
                style = Stroke(s.strokeWidth)
            )
        }
    }
}

/* ---------------- Tools ---------------- */

fun DrawScope.drawTools(vm: DrawingViewModel) {
    for (t in vm.tools) {
        when (t.type) {
            is ToolType.Ruler -> drawRuler(vm, t)
            is ToolType.SetSquare45 -> drawSetSquare(vm, t, 45f)
            is ToolType.SetSquare30_60 -> drawSetSquare(vm, t, 30f)
            is ToolType.Protractor -> drawProtractor(vm, t)
            is ToolType.Compass -> drawCompass(vm, t)
            ToolType.Unknown -> {} // Exhaustive branch
        }
    }
}

/* ---------------- Tool Renderers ---------------- */

private fun DrawScope.drawRuler(vm: DrawingViewModel, tool: Tool) {
    val halfLen = 200f
    val p1 = vm.rotatePoint(Offset(tool.position.x - halfLen, tool.position.y), tool.position, tool.rotation)
    val p2 = vm.rotatePoint(Offset(tool.position.x + halfLen, tool.position.y), tool.position, tool.rotation)

    drawLine(Color.Blue, p1, p2, strokeWidth = 6f)

    if (vm.currentToolId == tool.id) {
        vm.addShape(Shape.LineSeg(p1, p2))
    }
}

private fun DrawScope.drawSetSquare(vm: DrawingViewModel, tool: Tool, angle: Float) {
    val size = 200f
    val p1 = tool.position
    val p2 = vm.rotatePoint(Offset(p1.x + size, p1.y), p1, tool.rotation)
    val p3 = vm.rotatePoint(Offset(p1.x, p1.y - size), p1, tool.rotation)

    val path = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }
    drawPath(path, Color.Red, style = Stroke(4f))
}

private fun DrawScope.drawProtractor(vm: DrawingViewModel, tool: Tool) {
    drawCircle(Color.Green, radius = 150f, center = tool.position, style = Stroke(4f))

    val lines = vm.shapes.filterIsInstance<Shape.LineSeg>()
    if (lines.size >= 2) {
        val angle = vm.angleBetween(
            lines[lines.size - 2].end,
            tool.position,
            lines.last().end
        )
        drawContext.canvas.nativeCanvas.drawText(
            "∠ ${"%.1f".format(angle)}°",
            tool.position.x,
            tool.position.y - 160f,
            Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 32f
            }
        )
    }
}

private fun DrawScope.drawCompass(vm: DrawingViewModel, tool: Tool) {
    val legLength = 200f
    val p1 = tool.position
    val p2 = vm.rotatePoint(Offset(p1.x, p1.y - legLength), p1, tool.rotation)
    val p3 = vm.rotatePoint(Offset(p1.x + legLength, p1.y), p1, tool.rotation)

    drawLine(Color.DarkGray, p1, p2, strokeWidth = 4f)
    drawLine(Color.DarkGray, p1, p3, strokeWidth = 4f)

    drawCircle(Color.Magenta, radius = legLength, center = p1, style = Stroke(2f))

    if (vm.currentToolId == tool.id) {
        vm.addShape(Shape.CircleSeg(center = p1, radius = legLength))
    }
}
