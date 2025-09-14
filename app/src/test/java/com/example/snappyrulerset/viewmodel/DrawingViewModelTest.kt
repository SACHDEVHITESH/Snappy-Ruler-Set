package com.example.snappyrulerset.viewmodel

import androidx.compose.ui.geometry.Offset
import com.example.snappyrulerset.models.Shape
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DrawingViewModelTest {

    private val vm = DrawingViewModel()

    @Test
    fun rotatePoint_90deg_isCorrect() {
        val center = Offset(0f, 0f)
        val p = Offset(1f, 0f)
        val rotated = vm.rotatePoint(p, center, 90f)
        assertThat(rotated.x).isWithin(0.001f).of(0f)
        assertThat(rotated.y).isWithin(0.001f).of(1f)
    }

    @Test
    fun projectPointOntoLine_isCorrect() {
        val a = Offset(0f, 0f)
        val b = Offset(10f, 0f)
        val p = Offset(5f, 5f)
        val proj = vm.projectPointOntoLine(a, b, p)
        assertThat(proj).isEqualTo(Offset(5f, 0f))
    }

    @Test
    fun angleBetween_threePoints_isCorrect() {
        val a = Offset(1f, 0f)
        val vertex = Offset(0f, 0f)
        val b = Offset(0f, 1f)
        val angle = vm.angleBetween(a, vertex, b)
        assertThat(angle).isWithin(0.1f).of(90f)
    }

    @Test
    fun undoRedo_restoresState() {
        vm.addShape(Shape.LineSeg(Offset(0f, 0f), Offset(1f, 1f)))
        assertThat(vm.shapes).hasSize(1)

        vm.undo()
        assertThat(vm.shapes).isEmpty()

        vm.redo()
        assertThat(vm.shapes).hasSize(1)
    }
}
