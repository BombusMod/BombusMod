package org.bombusmod.compose

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import org.bombusmod.compose.theme.MyTheme
import ui.VirtualListController

object NativeUiBridge {

    @JvmStatic
    fun init(activity: AppCompatActivity) {
        VirtualListController.getInstance().setActive(true)
        WindowCompat.getInsetsController(
            activity.window, activity.window.decorView
        ).isAppearanceLightStatusBars = false
    }

    @JvmStatic
    fun wrap(activity: AppCompatActivity, legacyView: View?): View {
        return ComposeView(activity).apply {
            setContent { MyTheme { ScreenHost(legacyView = legacyView) } }
        }
    }
}
