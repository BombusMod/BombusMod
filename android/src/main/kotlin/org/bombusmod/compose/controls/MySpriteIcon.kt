package org.bombusmod.compose.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val PLACEHOLDER = Color(0xFF1EA5C5.toInt()) // BombusMod cyan

@Composable
fun MySpriteIcon(imageIndex: Int, iconSize: Dp = 28.dp, modifier: Modifier = Modifier) {
    if (imageIndex < 0) return
    // TODO: load from skin.png sprite sheet
    androidx.compose.foundation.Canvas(modifier = modifier.size(iconSize)) {
        drawCircle(PLACEHOLDER, radius = size.minDimension / 3)
    }
}
