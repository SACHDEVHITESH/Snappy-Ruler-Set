package com.example.snappyrulerset.models

import androidx.compose.ui.geometry.Offset

data class Tool(
    val id: String,
    val type: ToolType,
    val position: Offset = Offset.Zero,
    val rotation: Float = 0f,
    val lengthPx: Float = 300f,
    val visible: Boolean = true
)
