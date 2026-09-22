package org.bombusmod.compose

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import org.bombusmod.android.service.XmppService
import org.bombusmod.compose.theme.MyTheme
import ui.VirtualListController

/**
 * Standalone Compose Activity for native UI testing via adb.
 * The main launcher is BombusModActivity.
 */
class ComposeHostActivity : ComponentActivity() {

    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as XmppService.LocalBinder
            Client.StaticData.getInstance().service = binder.getService()
            serviceBound = true
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VirtualListController.getInstance().setActive(true)

        val intent = Intent(this, XmppService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        setContent {
            MyTheme {
                ScreenHost()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VirtualListController.getInstance().setActive(false)
        if (serviceBound) {
            unbindService(serviceConnection)
        }
    }
}
