package org.bombusmod.compose

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Wraps the legacy MicroEmulator CanvasView inside a Compose container.
 *
 * When a legacy (unmigrated) screen is shown, this composable hosts
 * the existing J2ME canvas rendering pipeline unchanged. The CanvasView
 * is provided by BombusModActivity (or created on-demand) and renders
 * the VirtualList's paint() output via AndroidDisplayGraphics.
 *
 * Usage in ScreenHost:
 * ```
 * if (screenIsLegacy) {
 *     LegacyCanvasWrapper(canvasViewProvider = { BombusModActivity.getInstance().getCanvasView() })
 * }
 * ```
 */
@Composable
fun LegacyCanvasWrapper(
    canvasViewProvider: () -> View?
) {
    AndroidView(
        factory = { context ->
            val canvasView = canvasViewProvider()
            if (canvasView != null) {
                canvasView
            } else {
                // Fallback: create an empty view with legacy note
                android.widget.TextView(context).apply {
                    text = "Legacy screen — CanvasView not available"
                }
            }
        },
        update = { view ->
            // Request focus so hardware keys work
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
        }
    )
}
