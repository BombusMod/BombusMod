package org.bombusmod.compose.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import Colors.ColorTheme

// Default colors matching ColorTheme.init() defaults.
// Hardcoded until the ColorTheme singleton lifecycle issue is resolved.
// Same values: bar=#AD1010, list=#FFFFFF, etc.

internal val BAR_BGND       = Color(0xFFAD1010.toInt())  // dark red
internal val BAR_BGND_BOT   = Color(0xFF730000.toInt())  // darker red
internal val BAR_INK        = Color.White
internal val LIST_BGND      = Color.White
internal val LIST_BGND_EVEN = Color(0xFFE8F0F0.toInt())  // pale blue-gray
internal val LIST_INK       = Color.Black
internal val SECOND_LINE    = Color(0xFFA0A0A0.toInt())  // gray
internal val CURSOR_BGND    = Color(0xFF010101.toInt())  // near-black
internal val CURSOR_OUTLINE = Color(0xFF1EA5C5.toInt())  // cyan
internal val CONTROL_ITEM   = Color(0xFF1EA5C5.toInt())  // cyan
internal val MSG_HIGHLIGHT  = Color(0xFF7540B0.toInt())  // purple

@Composable
fun MyTheme(content: @Composable () -> Unit) {
    ColorTheme.getInstance()

    val colorScheme = lightColorScheme(
        primary = BAR_BGND,
        onPrimary = BAR_INK,
        primaryContainer = BAR_BGND,
        onPrimaryContainer = BAR_INK,
        secondary = CONTROL_ITEM,
        secondaryContainer = CURSOR_BGND,
        onSecondaryContainer = CURSOR_OUTLINE,
        background = LIST_BGND,
        surface = LIST_BGND,
        surfaceVariant = LIST_BGND_EVEN,
        onBackground = LIST_INK,
        onSurface = LIST_INK,
        onSurfaceVariant = SECOND_LINE,
        outline = CURSOR_OUTLINE
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MyTypography,
        content = content
    )
}

// Keep MyColors bridge working — reads from hardcoded defaults for now
object MyColors {
    val BAR_BGND get() = org.bombusmod.compose.theme.BAR_BGND
    val BAR_INK get() = org.bombusmod.compose.theme.BAR_INK
    val LIST_BGND get() = org.bombusmod.compose.theme.LIST_BGND
    val LIST_BGND_EVEN get() = org.bombusmod.compose.theme.LIST_BGND_EVEN
    val LIST_INK get() = org.bombusmod.compose.theme.LIST_INK
    val SECOND_LINE get() = org.bombusmod.compose.theme.SECOND_LINE
    val CURSOR_BGND get() = org.bombusmod.compose.theme.CURSOR_BGND
    val CURSOR_OUTLINE get() = org.bombusmod.compose.theme.CURSOR_OUTLINE
    val CONTROL_ITEM get() = org.bombusmod.compose.theme.CONTROL_ITEM
    val MSG_HIGHLIGHT get() = org.bombusmod.compose.theme.MSG_HIGHLIGHT
}
