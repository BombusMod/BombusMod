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
import ui.NativeScreenCommand
import ui.NativeScreenItem
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

        // ===== TEST: showcase all controls =====
        val testModel = NativeScreenModel().apply {
            // Header
            val h = NativeScreenItem().apply { setAsHeader("My* Control Composables") }
            addPar(h)
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Info text
            addPar(NativeScreenItem().apply { setAsMultiline("Phase 3 — all form controls rendering natively via Jetpack Compose. Each control maps to a J2ME defform equivalent.") })
            addPar(NativeScreenItem().apply { setAsSpacer(8) })

            // CheckBox
            addPar(NativeScreenItem().apply { setAsHeader("Toggle") })
            addPar(NativeScreenItem().apply { setAsCheckBox("Enable notifications", true) })
            addPar(NativeScreenItem().apply { setAsCheckBox("Show offline contacts", false) })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Text input
            addPar(NativeScreenItem().apply { setAsHeader("Text Input") })
            addPar(NativeScreenItem().apply { setAsInput("nick", "Nickname", "user123") })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Number input
            addPar(NativeScreenItem().apply { setAsHeader("Number") })
            addPar(NativeScreenItem().apply { setAsNumber("port", "Port", 5222) })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Password
            addPar(NativeScreenItem().apply { setAsHeader("Password") })
            addPar(NativeScreenItem().apply { setAsPassword("pass", "Password") })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Dropdown
            addPar(NativeScreenItem().apply { setAsHeader("Dropdown") })
            addPar(NativeScreenItem().apply { setAsDropdown("sort", "Sort by",
                arrayOf("Status", "Name", "Online"), 0) })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Slider
            addPar(NativeScreenItem().apply { setAsHeader("Slider") })
            addPar(NativeScreenItem().apply { setAsSlider("font", "Font size", 16f, 8f, 32f) })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Link
            addPar(NativeScreenItem().apply { setAsHeader("Link") })
            addPar(NativeScreenItem().apply {
                setAsLink("BombusMod on GitHub")
                description = "https://github.com/BombusMod/BombusMod"
            })
            addPar(NativeScreenItem().apply {
                setAsLink("Project website")
                description = "https://bombusmod.github.io/BombusMod"
            })
            addPar(NativeScreenItem().apply { setAsSpacer(4) })

            // Simple text
            addPar(NativeScreenItem().apply { setAsHeader("Text Rows") })
            addItem("Simple selectable item", true)
            addParam("Status", "Online")
            addInfoMessage("Info message (non-selectable)")
            addPar(NativeScreenItem().apply { setAsSpacer(8) })

            addCommand("ok", "OK", NativeScreenCommand.OK, -1)
            addCommand("back", "Back", NativeScreenCommand.BACK, -1)
        }
        controller.setCaption("Controls Test")
        controller.setModel(testModel)
        // ===== END TEST =====

        // Bind to XMPP service
        val intent = Intent(this, XmppService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        // Edge-to-edge: bars draw behind system status/nav bars
        enableEdgeToEdge()
        // White status bar icons (dark red background)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        // Set Compose content
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
