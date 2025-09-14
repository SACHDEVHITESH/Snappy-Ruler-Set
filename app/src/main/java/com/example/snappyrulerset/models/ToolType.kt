package com.example.snappyrulerset.models

sealed class ToolType {
    object Ruler : ToolType()
    object SetSquare45 : ToolType()
    object SetSquare30_60 : ToolType()
    object Protractor : ToolType()
    object Compass : ToolType()

    object Unknown : ToolType()
}