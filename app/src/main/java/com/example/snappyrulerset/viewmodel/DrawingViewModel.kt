package com.example.snappyrulerset.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import com.example.snappyrulerset.models.Shape
import com.example.snappyrulerset.models.Tool
import com.example.snappyrulerset.models.ToolType
import com.example.snappyrulerset.util.SnapEngine
import java.util.Stack
import kotlin.math.*

class DrawingViewModel : ViewModel() {
    var canvasOffset by mutableStateOf(Offset.Zero)
    var canvasScale by mutableFloatStateOf(1f)
    var canvasRotation by mutableFloatStateOf(0f)

    var canvasSize by mutableStateOf(IntSize.Zero)
        private set

    fun resetCanvasTransform() {
        canvasOffset = Offset.Zero
        canvasScale = 1f
        canvasRotation = 0f
    }

    var shapes by mutableStateOf<List<Shape>>(emptyList())
        private set

    private val undoStack: Stack<List<Shape>> = Stack()
    private val redoStack: Stack<List<Shape>> = Stack()

    private fun pushUndo() {
        undoStack.push(shapes.toList())
        redoStack.clear()
    }

    fun addShape(shape: Shape) {
        pushUndo()
        shapes = shapes + shape
    }

    var lastSnapType by mutableStateOf<String?>(null)
        private set

    fun startFreehand(start: Offset) {
        pushUndo()
        val (p, snapType) = snapEngine.snapPoint(
            start,
            shapes.filterIsInstance<Shape.LineSeg>(),
            shapes.filterIsInstance<Shape.CircleSeg>(),
            canvasScale,
            snapEnabled
        )
        lastSnapType = snapType
        shapes = shapes + Shape.FreePath(mutableListOf(p))
    }

    fun continueFreehand(point: Offset) {
        var (p, snapType) = snapEngine.snapPoint(
            point,
            shapes.filterIsInstance<Shape.LineSeg>(),
            shapes.filterIsInstance<Shape.CircleSeg>(),
            canvasScale,
            snapEnabled
        )

        val ruler = tools.find { it.id == currentToolId && it.type is ToolType.Ruler }
        if (snapEnabled && ruler != null) {
            val half = 200f
            val p1 = rotatePoint(
                Offset(ruler.position.x - half, ruler.position.y),
                ruler.position,
                ruler.rotation
            )
            val p2 = rotatePoint(
                Offset(ruler.position.x + half, ruler.position.y),
                ruler.position,
                ruler.rotation
            )
            p = projectPointOntoLine(p1, p2, p)
            snapType = "ruler-edge"
        }

        lastSnapType = snapType
        val last = shapes.lastOrNull()
        if (last is Shape.FreePath) {
            last.points.add(p)
            shapes = shapes.toList()
        }
    }

    fun endFreehand() {
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.push(shapes)
            shapes = undoStack.pop()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.push(shapes)
            shapes = redoStack.pop()
        }
    }

    fun clearShapes() {
        pushUndo()
        shapes = emptyList()
    }

    var tools by mutableStateOf(
        listOf(
            Tool("ruler", ToolType.Ruler, Offset(300f, 300f)),
            Tool("set45", ToolType.SetSquare45, Offset(600f, 300f)),
            Tool("set3060", ToolType.SetSquare30_60, Offset(900f, 300f)),
            Tool("protractor", ToolType.Protractor, Offset(400f, 800f)),
            Tool("compass", ToolType.Compass, Offset(700f, 800f))
        )
    )
        private set

    var currentToolId by mutableStateOf<String?>(null)

    fun selectTool(id: String?) {
        currentToolId = id
    }

    fun setToolPosition(id: String, pos: Offset) {
        tools = tools.map { if (it.id == id) it.copy(position = pos) else it }
    }

    fun setToolRotation(id: String, deg: Float) {
        val snapped = if (snapEnabled) snapEngine.snapAngle(deg) ?: deg else deg
        tools = tools.map { if (it.id == id) it.copy(rotation = snapped) else it }
    }

    var snapEnabled by mutableStateOf(true)
    val snapEngine = SnapEngine()

    var pixelsPerMm by mutableFloatStateOf(6.3f)
        private set

    fun setDensityDpi(dpi: Int) {
        val ppmm = dpi / 25.4f
        pixelsPerMm = ppmm
        snapEngine.updatePixelsPerMm(ppmm)
    }

     fun rotatePoint(p: Offset, center: Offset, degrees: Float): Offset {
        val rad = Math.toRadians(degrees.toDouble())
        val s = sin(rad)
        val c = cos(rad)
        val x = p.x - center.x
        val y = p.y - center.y
        return Offset(center.x + (x * c - y * s).toFloat(), center.y + (x * s + y * c).toFloat())
    }

     fun projectPointOntoLine(a: Offset, b: Offset, p: Offset): Offset {
        val ap = p - a
        val ab = b - a
        val ab2 = ab.x * ab.x + ab.y * ab.y
        if (ab2 == 0f) return a
        val t = ((ap.x * ab.x) + (ap.y * ab.y)) / ab2
        return Offset(a.x + ab.x * t, a.y + ab.y * t)
    }

    fun angleBetween(a: Offset, vertex: Offset, b: Offset): Float {
        val v1 = a - vertex
        val v2 = b - vertex
        val ang1 = atan2(v1.y, v1.x)
        val ang2 = atan2(v2.y, v2.x)
        var deg = Math.toDegrees((ang2 - ang1).toDouble()).toFloat()
        if (deg < 0) deg += 360f
        return deg
    }

    fun updateCanvasSize(newSize: IntSize) {
        canvasSize = newSize
    }
}
