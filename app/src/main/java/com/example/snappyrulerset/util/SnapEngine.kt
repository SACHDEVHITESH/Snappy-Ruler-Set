package com.example.snappyrulerset.util

import androidx.compose.ui.geometry.Offset
import com.example.snappyrulerset.models.Shape
import kotlin.math.*


class SnapEngine(private var pixelsPerMm: Float = 6.3f) {
    private val angleSnaps = listOf(0f, 30f, 45f, 60f, 90f, 120f, 135f, 150f, 180f)
    var gridSpacingMm = 5f

    fun updatePixelsPerMm(ppmm: Float) { pixelsPerMm = ppmm }
    fun gridSpacingPx() = gridSpacingMm * pixelsPerMm


    fun snapAngle(rawDeg: Float, thresholdDeg: Float = 6f): Float? {
        var best: Float? = null
        var bestDiff = Float.MAX_VALUE
        for (candidate in angleSnaps) {
            val diff = abs(normAngleDifference(rawDeg, candidate))
            if (diff < bestDiff) { bestDiff = diff; best = candidate }
        }
        return if (best != null && bestDiff <= thresholdDeg) best else null
    }


    private fun normAngleDifference(a: Float, b: Float): Float {
        var d = (a - b) % 360f
        if (d > 180) d -= 360f
        if (d < -180) d += 360f
        return d
    }


    fun snapPoint(
        p: Offset,
        existingSegments: List<Shape.LineSeg>,
        existingCircles: List<Shape.CircleSeg>,
        zoom: Float,
        snapEnabled: Boolean
    ): Pair<Offset, String?> {
        if (!snapEnabled) return Pair(p, null)
        val baseRadiusPx = 18f
        val radius = baseRadiusPx / zoom


        val grid = gridSpacingPx()
        val gx = (p.x / grid).roundToInt() * grid
        val gy = (p.y / grid).roundToInt() * grid
        val gp = Offset(gx, gy)
        if (Geometry.distance(p, gp) <= radius) return Pair(gp, "grid")


        var bestPoint: Offset? = null
        var bestDist = Float.MAX_VALUE
        for (s in existingSegments) {
            val endpoints = listOf(s.start, s.end, Geometry.midpoint(s.start, s.end))
            for (pt in endpoints) {
                val d = Geometry.distance(p, pt)
                if (d < bestDist) { bestDist = d; bestPoint = pt }
            }
        }
        if (bestPoint != null && bestDist <= radius) return Pair(bestPoint, "point")


        for (c in existingCircles) {
            if (Geometry.distance(p, c.center) <= radius) return Pair(c.center, "center")
        }


        return Pair(p, null)
    }
}