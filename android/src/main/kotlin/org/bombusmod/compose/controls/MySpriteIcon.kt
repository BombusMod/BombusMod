package org.bombusmod.compose.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val colors = listOf(
    Color(0xFF1EA5C5.toInt()), // cyan - default
    Color(0xFF4CAF50.toInt()), // green - expanded
    Color(0xFFFF9800.toInt()), // orange - collapsed
    Color(0xFFF44336.toInt()), // red - error
    Color(0xFF9C27B0.toInt()), // purple - MUC
)

@Composable
fun MySpriteIcon(imageIndex: Int, iconSize: Dp = 28.dp, modifier: Modifier = Modifier) {
    if (imageIndex < 0) return
    // Use imageIndex to pick distinct color — visible differenciation
    val color = colors[imageIndex % colors.size]
    Box(
        modifier = modifier
            .size(iconSize)
            .clip(CircleShape)
            .background(color)
    )
}
