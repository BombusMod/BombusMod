@file:Suppress("DEPRECATION")

package org.bombusmod.compose.controls

import android.graphics.BitmapFactory
import kotlin.math.roundToInt
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun MySpriteIcon(imageIndex: Int, iconSize: Dp = 28.dp, modifier: Modifier = Modifier) {
    if (imageIndex < 0) return
    val ctx = LocalContext.current
    val bmp = remember {
        try { BitmapFactory.decodeStream(ctx.assets.open("images/skin.png")) }
        catch (e: Exception) { null }
    } ?: return
    val cols = 8
    val cw = bmp.width / cols
    val col = imageIndex % cols
    val row = imageIndex / cols
    Canvas(modifier = modifier.size(iconSize)) {
        drawImage(bmp.asImageBitmap(),
            srcOffset = IntOffset(col * cw, row * cw),
            srcSize = IntSize(cw, cw),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt()))
    }
}
