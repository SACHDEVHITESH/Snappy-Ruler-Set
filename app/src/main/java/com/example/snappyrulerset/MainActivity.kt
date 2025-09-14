package com.example.snappyrulerset

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.snappyrulerset.ui.SnappyRulerScreen
import com.example.snappyrulerset.viewmodel.DrawingViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    val vm: DrawingViewModel = viewModel()
                    val ctx = LocalContext.current
                    LaunchedEffect(Unit) { vm.setDensityDpi(ctx.resources.displayMetrics.densityDpi) }
                    SnappyRulerScreen(vm)
                }
            }
        }
    }
}