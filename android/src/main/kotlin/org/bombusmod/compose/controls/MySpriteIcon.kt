package org.bombusmod.compose.controls

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
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

private const val COLS = 8

@Composable
fun MySpriteIcon(imageIndex: Int, iconSize: Dp = 28.dp, modifier: Modifier = Modifier) {
    if (imageIndex < 0) return
    val ctx = LocalContext.current

    val cell = remember(imageIndex) {
        try {
            ctx.assets.open("images/skin.png").use { stream ->
                val src = BitmapFactory.decodeStream(stream) ?: return@remember null
                val cw = src.width / COLS
                if (cw <= 0) return@remember null
                val col = imageIndex % COLS
                val row = imageIndex / COLS
                val argb = Bitmap.createBitmap(cw, cw, Bitmap.Config.ARGB_8888)
                Canvas(argb).drawBitmap(src, (-col * cw).toFloat(), (-row * cw).toFloat(), Paint())
                src.recycle()
                argb.asImageBitmap()
            }
        } catch (_: Exception) { null }
    } ?: return

    Image(bitmap = cell, contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.size(iconSize))
}
