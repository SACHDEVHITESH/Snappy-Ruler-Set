package com.example.snappyrulerset.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color


sealed class Shape {

    data class FreePath(
        val points: MutableList<Offset>,
        val color: Color = Color.Black,
        val strokeWidth: Float = 3f
    ) : Shape()


    data class LineSeg(
        val start: Offset,
        val end: Offset,
        val color: Color = Color.Black,
        val strokeWidth: Float = 3f
    ) : Shape()

    data class CircleSeg(
        val center: Offset,
        val radius: Float,
        val color: Color = Color.Black,
        val strokeWidth: Float = 3f
    ) : Shape()
}