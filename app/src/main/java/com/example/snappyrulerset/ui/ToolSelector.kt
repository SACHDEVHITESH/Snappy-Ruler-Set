package com.example.snappyrulerset.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.snappyrulerset.models.ToolType
import com.example.snappyrulerset.viewmodel.DrawingViewModel

@Composable
fun ToolSelector(vm: DrawingViewModel, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(vm.tools) { t ->
            val label = when (t.type) {
                is ToolType.Ruler -> "Ruler"
                is ToolType.SetSquare45 -> "Set45"
                is ToolType.SetSquare30_60 -> "Set30/60"
                is ToolType.Protractor -> "Protractor"
                is ToolType.Compass -> "Compass"
                else -> "Tool"
            }

            Button(
                onClick = { vm.selectTool(t.id) },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Extra item for freehand pencil mode
        item {
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Button(onClick = { vm.selectTool(null) }) {
                Text("✏️ Draw", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
