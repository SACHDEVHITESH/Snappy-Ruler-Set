package com.example.snappyrulerset.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.core.content.FileProvider
import com.example.snappyrulerset.viewmodel.DrawingViewModel
import java.io.File
import java.io.FileOutputStream

fun toCanvasCoords(screen: Offset, vm: DrawingViewModel): Offset {
    return (screen - vm.canvasOffset) / vm.canvasScale
}

fun shareBitmap(context: Context, bmp: Bitmap) {
    try {
        val file = File(context.cacheDir, "snappy_export_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Drawing"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
