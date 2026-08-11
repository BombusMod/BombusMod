package org.bombusmod.compose.controls

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val PHYS_COLS = 8  // ICONS_IN_ROW

@Composable
fun MySpriteIcon(imageIndex: Int, iconSize: Dp = 28.dp, modifier: Modifier = Modifier) {
    if (imageIndex < 0) return
    val ctx = LocalContext.current

    val cell = remember(imageIndex) {
        try {
            ctx.assets.open("images/skin.png").use { stream ->
                val src = BitmapFactory.decodeStream(stream) ?: return@remember null
                val cellSize = src.width / PHYS_COLS
                if (cellSize <= 0) return@remember null

                // Exact J2ME formula from ImageList.drawImage:
                //   int iy = y - height * (index >> 4);
                //   int ix = x - width * (index & 0x0f);
                val col = imageIndex and 0x0f
                val row = imageIndex shr 4

                val argb = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888)
                val c = AndroidCanvas(argb)
                // Draw source shifted so cell (col,row) lands at (0,0)
                c.drawBitmap(src, (-col * cellSize).toFloat(), (-row * cellSize).toFloat(), Paint())
                src.recycle()
                argb.asImageBitmap()
            }
        } catch (_: Exception) { null }
    } ?: return

    Image(bitmap = cell, contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.size(iconSize))
}
