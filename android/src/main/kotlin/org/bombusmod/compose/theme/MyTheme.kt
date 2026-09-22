package org.bombusmod.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import Colors.ColorTheme

/** Convert ColorTheme int (0xRRGGBB) to Compose Color (0xAARRGGBB) */
private fun themeColor(colorId: Int): Color {
    return Color(ColorTheme.getColor(colorId) or 0xFF000000.toInt())
}

/** Lazy theme colors resolved from J2ME ColorTheme singleton at composition time */
object MyColors {
    // These are lazy so they work even before ColorTheme.init()
    val BAR_BGND get() = themeColor(ColorTheme.BAR_BGND)
    val BAR_BGND_BOT get() = themeColor(ColorTheme.BAR_BGND_BOTTOM)
    val BAR_INK get() = themeColor(ColorTheme.BAR_INK)
    val LIST_BGND get() = themeColor(ColorTheme.LIST_BGND)
    val LIST_BGND_EVEN get() = themeColor(ColorTheme.LIST_BGND_EVEN)
    val LIST_INK get() = themeColor(ColorTheme.LIST_INK)
    val SECOND_LINE get() = themeColor(ColorTheme.SECOND_LINE)
    val CURSOR_BGND get() = themeColor(ColorTheme.CURSOR_BGND)
    val CURSOR_OUTLINE get() = themeColor(ColorTheme.CURSOR_OUTLINE)
    val CONTROL_ITEM get() = themeColor(ColorTheme.CONTROL_ITEM)
    val MSG_HIGHLIGHT get() = themeColor(ColorTheme.MSG_HIGHLIGHT)
}

@Composable
fun MyTheme(content: @Composable () -> Unit) {
    // Ensure ColorTheme singleton is initialized (no-op if already done)
    ColorTheme.getInstance()

    // Resolve at composition time so theme changes propagate on recomposition
    val barBgnd = remember { themeColor(ColorTheme.BAR_BGND) }
    val barInk = remember { themeColor(ColorTheme.BAR_INK) }
    val listBgnd = remember { themeColor(ColorTheme.LIST_BGND) }
    val listBgndEven = remember { themeColor(ColorTheme.LIST_BGND_EVEN) }
    val listInk = remember { themeColor(ColorTheme.LIST_INK) }
    val secondLine = remember { themeColor(ColorTheme.SECOND_LINE) }
    val controlItem = remember { themeColor(ColorTheme.CONTROL_ITEM) }
    val cursorBgnd = remember { themeColor(ColorTheme.CURSOR_BGND) }
    val cursorOutline = remember { themeColor(ColorTheme.CURSOR_OUTLINE) }

    val colorScheme = lightColorScheme(
        primary = barBgnd,
        onPrimary = barInk,
        primaryContainer = barBgnd,
        onPrimaryContainer = barInk,
        secondary = controlItem,
        secondaryContainer = cursorBgnd,
        onSecondaryContainer = cursorOutline,
        background = listBgnd,
        surface = listBgnd,
        surfaceVariant = listBgndEven,
        onBackground = listInk,
        onSurface = listInk,
        onSurfaceVariant = secondLine,
        outline = cursorOutline
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyTypography,
        content = content
    )
}
