package com.example.snappyrulerset.util

import androidx.compose.ui.geometry.Offset
import kotlin.math.*


object Geometry {
    fun distance(a: Offset, b: Offset) = hypot(a.x - b.x, a.y - b.y)


    fun angleBetween(a: Offset, vertex: Offset, b: Offset): Float {
        val v1 = a - vertex
        val v2 = b - vertex
        val ang1 = atan2(v1.y, v1.x)
        val ang2 = atan2(v2.y, v2.x)
        var deg = Math.toDegrees((ang2 - ang1).toDouble()).toFloat()
        if (deg < 0) deg += 360f
        return deg
    }


    fun projectPointOntoLine(a: Offset, b: Offset, p: Offset): Offset {
        val ap = p - a
        val ab = b - a
        val ab2 = ab.x * ab.x + ab.y * ab.y
        if (ab2 == 0f) return a
        val t = ((ap.x * ab.x) + (ap.y * ab.y)) / ab2
        return Offset(a.x + ab.x * t, a.y + ab.y * t)
    }


    fun midpoint(a: Offset, b: Offset) = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f)


    fun toRadians(deg: Float) = Math.toRadians(deg.toDouble()).toFloat()


    fun rotatePoint(p: Offset, center: Offset, degrees: Float): Offset {
        val rad = toRadians(degrees)
        val s = sin(rad)
        val c = cos(rad)
        val x = p.x - center.x
        val y = p.y - center.y
        return Offset(center.x + (x * c - y * s), center.y + (x * s + y * c))
    }
}