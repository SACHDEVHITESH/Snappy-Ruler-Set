package com.example.snappyrulerset.util

import androidx.compose.ui.geometry.Offset
import com.example.snappyrulerset.models.Shape
import org.junit.Assert.assertEquals
import org.junit.Test

class SnapEngineTest {

    private val snap = SnapEngine()

    @Test
    fun snapPoint_snapsToGrid() {
        // Near 63, which is exactly a grid multiple
        val p = Offset(62.8f, 62.7f)
        val (snapped, type) = snap.snapPoint(p, emptyList(), emptyList(), 1f, true)

        assertEquals("grid", type)
        assertEquals(63f, snapped.x, 0.5f)
        assertEquals(63f, snapped.y, 0.5f)
    }
}
