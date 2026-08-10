package org.bombusmod.compose

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import org.bombusmod.android.service.XmppService
import ui.NativeScreenCommand
import ui.NativeScreenModel
import ui.VirtualListController

/**
 * Compose-based Activity that hosts the native UI migration.
 *
 * Relies on BombusModActivity for MicroEmulator initialization and MIDlet lifecycle.
 * This Activity hosts only the Compose rendering layer — the XMPP core runs
 * in the XmppService (background) and BombusModActivity (MIDlet host).
 *
 * A feature flag (Config.useNativeUI) controls which Activity launches.
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

        // Activate native UI mode
        val controller = VirtualListController.getInstance()
        controller.setActive(true)

        // ===== TEST: populate sample model =====
        val testModel = NativeScreenModel().apply {
            setHeader("Phase 1 Test")
            addItem("NativeScreenModel loaded", true)
            addItem("VirtualListController active", true)
            addParam("Build", "SUCCESS")
            addParam("Compose", "Material3")
            addInfoMessage("This is a native Compose screen")
            addItem("Item 5 — selectable", true)
            addItem("Item 6 — also selectable", true)
            addCommand("ok", "OK", NativeScreenCommand.OK, -1)
            addCommand("back", "Back", NativeScreenCommand.BACK, -1)
        }
        controller.setCaption("BombusMod — Native UI Test")
        controller.setModel(testModel)
        // ===== END TEST =====

        // Bind to XMPP service
        val intent = Intent(this, XmppService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Set Compose content
        setContent {
            MaterialTheme {
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
