package com.example.snappyrulerset.util

import androidx.compose.ui.geometry.Offset
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GeometryTest {

    @Test
    fun distance_isCorrect() {
        val a = Offset(0f, 0f)
        val b = Offset(3f, 4f)
        assertThat(Geometry.distance(a, b)).isWithin(0.001f).of(5f)
    }

    @Test
    fun midpoint_isCorrect() {
        val a = Offset(0f, 0f)
        val b = Offset(4f, 2f)
        val mid = Geometry.midpoint(a, b)
        assertThat(mid).isEqualTo(Offset(2f, 1f))
    }
}