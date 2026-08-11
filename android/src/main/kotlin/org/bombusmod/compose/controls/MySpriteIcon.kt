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

private const val PHYS_COLS = 8

// Lazy-loaded transport skins (key: filename, value: bitmap)
private val transportSkins = mutableMapOf<String, Bitmap>()

@Composable
fun MySpriteIcon(imageIndex: Int, iconSize: Dp = 28.dp, modifier: Modifier = Modifier) {
    if (imageIndex < 0) return

    // Transport skins (index > 66000): extract skin ID from high byte
    if (imageIndex > 66000) {
        val skinId = imageIndex ushr 24
        val cellIndex = imageIndex and 0xff
        // Transport skins are loaded by RosterIcons in order; we map filenames manually
        // visitors.png is loaded for "muc#vis"
        TransportCell(skinId, cellIndex, iconSize, modifier)
        return
    }

    SpriteCell(imageIndex, iconSize, modifier)
}

@Composable
private fun TransportCell(skinId: Int, cellIndex: Int, iconSize: Dp, modifier: Modifier) {
    val ctx = LocalContext.current
    // skinId 1 = visitors.png (first transport loaded)
    val filename = when (skinId) {
        1 -> "images/visitors.png"
        else -> "images/skin.png" // fallback to main skin with low byte
    }
    val actualIndex = if (skinId > 1) cellIndex else cellIndex

    val sheet = remember(filename) {
        try {
            ctx.assets.open(filename).use { BitmapFactory.decodeStream(it) }
        } catch (_: Exception) { null }
    } ?: return

    val cellSize = sheet.width / PHYS_COLS
    if (cellSize <= 0) return
    val col = actualIndex and 0x0f
    val row = actualIndex shr 4

    val cell = remember(filename, actualIndex) {
        val argb = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888)
        AndroidCanvas(argb).drawBitmap(sheet, (-col * cellSize).toFloat(), (-row * cellSize).toFloat(), Paint())
        argb.asImageBitmap()
    }

    Image(bitmap = cell, contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.size(iconSize))
}

@Composable
private fun SpriteCell(imageIndex: Int, iconSize: Dp, modifier: Modifier) {
    val ctx = LocalContext.current

    val cell = remember(imageIndex) {
        try {
            ctx.assets.open("images/skin.png").use { stream ->
                val src = BitmapFactory.decodeStream(stream) ?: return@remember null
                val cellSize = src.width / PHYS_COLS
                if (cellSize <= 0) return@remember null
                val col = imageIndex and 0x0f
                val row = imageIndex shr 4
                val argb = Bitmap.createBitmap(cellSize, cellSize, Bitmap.Config.ARGB_8888)
                AndroidCanvas(argb).drawBitmap(src, (-col * cellSize).toFloat(), (-row * cellSize).toFloat(), Paint())
                src.recycle()
                argb.asImageBitmap()
            }
        } catch (_: Exception) { null }
    } ?: return

    Image(bitmap = cell, contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier.size(iconSize))
}
