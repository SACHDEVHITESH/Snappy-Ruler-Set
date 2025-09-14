package com.example.snappyrulerset.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.drawToBitmap
import com.example.snappyrulerset.viewmodel.DrawingViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share

@Composable
fun SnappyRulerScreen(viewModel: DrawingViewModel) {
    val vm = viewModel
    val scope = rememberCoroutineScope()
    val view = LocalView.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(vm)
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            vm.canvasScale *= zoom
                            vm.canvasOffset += pan
                            vm.canvasRotation += Math.toDegrees(rotation.toDouble()).toFloat()
                        }
                    }
                    .pointerInput(vm.currentToolId) {
                        if (vm.currentToolId == null) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val loc = toCanvasCoords(offset, vm)
                                    vm.startFreehand(loc)
                                },
                                onDrag = { change, _ ->
                                    val loc = toCanvasCoords(change.position, vm)
                                    vm.continueFreehand(loc)
                                },
                                onDragEnd = { vm.endFreehand() },
                                onDragCancel = { vm.endFreehand() }
                            )
                        } else {
                            detectTransformGestures { _, pan, _, rotation ->
                                val tool = vm.tools.find { it.id == vm.currentToolId }
                                if (tool != null) {
                                    vm.setToolPosition(tool.id, tool.position + pan)
                                    val deg = Math.toDegrees(rotation.toDouble()).toFloat()
                                    if (abs(deg) > 0.5f) {
                                        vm.setToolRotation(
                                            tool.id,
                                            (tool.rotation + deg) % 360f
                                        )
                                    }
                                }
                            }
                        }
                    }
                    .onSizeChanged { newSize ->
                        vm.updateCanvasSize(newSize)
                    }
            ) {
                drawBackgroundGrid(vm)
                drawShapes(vm)
                drawTools(vm)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = { vm.undo() }) {
                    Icon(Icons.Filled.Undo, contentDescription = "Undo")
                }

                Spacer(Modifier.height(8.dp))

                FloatingActionButton(onClick = { vm.redo() }) {
                    Icon(Icons.Filled.Redo, contentDescription = "Redo")
                }

                Spacer(Modifier.height(8.dp))

                FloatingActionButton(onClick = {
                    scope.launch {
                        val bmp = view.drawToBitmap()
                        shareBitmap(view.context, bmp)
                    }
                }) {
                    Icon(Icons.Filled.Share, contentDescription = "Export/Share")
                }

                Spacer(Modifier.height(8.dp))

                IconButton(onClick = { vm.resetCanvasTransform() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset View")
                }

                Spacer(Modifier.height(8.dp))

                IconButton(onClick = { vm.clearShapes() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear")
                }

                Spacer(Modifier.height(8.dp))

                IconButton(onClick = { vm.snapEnabled = !vm.snapEnabled }) {
                    Icon(
                        if (vm.snapEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Snap"
                    )
                }
            }

            PrecisionHud(
                vm,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }

        ToolSelector(
            vm,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
        )
    }
}

@Composable
private fun TopAppBar(vm: DrawingViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Snappy Ruler Set",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Snap:")
            Switch(
                checked = vm.snapEnabled,
                onCheckedChange = { vm.snapEnabled = it }
            )
            Spacer(Modifier.width(8.dp))
            Text("Zoom: ${String.format("%.2fx", vm.canvasScale)}")
        }
    }
}

@Composable
fun PrecisionHud(vm: DrawingViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xAA000000), shape = MaterialTheme.shapes.small)
            .padding(6.dp)
    ) {
        Text("Scale: ${String.format("%.2f", vm.canvasScale)}", color = Color.White, fontSize = MaterialTheme.typography.bodySmall.fontSize)
        Text("Snap: ${if (vm.snapEnabled) "ON" else "OFF"}", color = Color.White, fontSize = MaterialTheme.typography.bodySmall.fontSize)

        if (vm.lastSnapType != null) {
            val snapLabel = when (vm.lastSnapType) {
                "grid" -> "Grid"
                "point" -> "Line Point"
                "center" -> "Circle Center"
                else -> "None"
            }
            Text("Snapped to: $snapLabel", color = Color.White, fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }

        Text("Tool: ${vm.currentToolId ?: "Freehand"}", color = Color.White, fontSize = MaterialTheme.typography.bodySmall.fontSize)

        if (vm.canvasSize.width > 0 && vm.canvasSize.height > 0) {
            Text("Canvas: ${vm.canvasSize.width}x${vm.canvasSize.height}", color = Color.White, fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}