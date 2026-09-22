package org.bombusmod.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography mapping from BombusMod's FontCache sizes to Compose TextStyle.
 *
 * FontCache provides three sizes (small, medium/roster, large) × two weights
 * (plain, bold). The actual pixel sizes come from Android dimension resources
 * (small_font_size, medium_font_size, large_font_size) configured in
 * BombusModActivity's EmulatorContext init.
 *
 * Default values: small=12sp, medium=16sp, large=20sp — overridden at runtime
 * by the Config FONT_SIZE_* values from SharedPreferences.
 */

/** Font sizes in sp — updated at runtime from Config */
object MyFontSizes {
    var small: Float = 12f
    var medium: Float = 16f
    var large: Float = 20f
}

/** Standard defform typography: 3 sizes × 2 weights */
val MyTypography = Typography(
    // Small
    bodySmall = TextStyle(
        fontSize = MyFontSizes.small.sp,
        fontWeight = FontWeight.Normal
    ),
    labelSmall = TextStyle(
        fontSize = MyFontSizes.small.sp,
        fontWeight = FontWeight.Bold
    ),

    // Medium (default / roster)
    bodyMedium = TextStyle(
        fontSize = MyFontSizes.medium.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyLarge = TextStyle(
        fontSize = MyFontSizes.medium.sp,
        fontWeight = FontWeight.Bold
    ),

    // Large
    titleSmall = TextStyle(
        fontSize = MyFontSizes.large.sp,
        fontWeight = FontWeight.Normal
    ),
    titleMedium = TextStyle(
        fontSize = MyFontSizes.large.sp,
        fontWeight = FontWeight.Bold
    )
)
