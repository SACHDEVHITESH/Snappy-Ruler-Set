package com.example.snappyrulerset.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.*


object Geometry {
    fun distance(a: Offset, b: Offset) = hypot(a.x - b.x, a.y - b.y)
    fun midpoint(a: Offset, b: Offset) = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f)

}