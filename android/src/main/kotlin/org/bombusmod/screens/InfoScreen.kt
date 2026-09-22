package org.bombusmod.screens

import android.content.Context
import Client.StaticData
import ui.NativeScreenCommand
import ui.NativeScreenItem
import ui.NativeScreenModel
import ui.VirtualListController

/**
 * Native Compose version of Info/InfoWindow.java — the "About" screen.
 */
class InfoScreen(context: Context) {

    private val model = NativeScreenModel()

    init {
        val vi = StaticData.getInstance().versionInfo
        val version = try {
            val pi = context.packageManager.getPackageInfo(context.packageName, 0)
            pi.versionName ?: "Unknown"
        } catch (e: Exception) { "Unknown" }

        // App name + version
        model.addPar(NativeScreenItem().apply {
            setAsMultiline("${vi.name}\n$version\n${Client.Config.getOs()}\nMobile Jabber client")
        })

        // Copyright
        model.addPar(NativeScreenItem().apply {
            setAsMultiline("Copyright (c) 2005-2020\nEugene Stahov (evgs),\nDaniel Apatin (ad)\n\nDistributed under GNU Public License (GPL) v2.0")
        })

        // Website link
        val url = vi.url ?: "https://bombusmod.github.io/BombusMod"
        model.addPar(NativeScreenItem().apply {
            setAsLink(url)
            description = url
        })

        model.addPar(NativeScreenItem().apply { setAsSpacer(20) })

        // Special thanks
        model.addPar(NativeScreenItem().apply {
            setAsMultiline("Special thanks\nAdvice, aspro, BrennendeR_Komet, 6yp4uk, den_po, Disabler, fregl24, G.L.Fire, gimlet, lgs, m, MaSy, Muxa, NoNameZ, radiance, Sash, spine, spirtamne, Tasha, TiLan, Totktonada, van, vitalyster, voffk, westsibe, zet.\n\nWithout you none of this would not have been!")
        })

        model.addPar(NativeScreenItem().apply { setAsSpacer(20) })

        // Memory
        System.gc()
        val freeMem = Runtime.getRuntime().freeMemory() shr 10
        val totalMem = Runtime.getRuntime().totalMemory() shr 10
        model.addPar(NativeScreenItem().apply {
            setAsMultiline("Memory\nFree: ${freeMem}K\nTotal: ${totalMem}K")
        })

        model.addPar(NativeScreenItem().apply { setAsSpacer(10) })

        // Abilities (build features)
        model.addPar(NativeScreenItem().apply {
            setAsMultiline("Abilities\n${getAbilities()}")
        })

        // Commands
        model.addCommand("ok", "OK", NativeScreenCommand.OK, -1)
    }

    fun getModel() = model

    fun show() {
        val ctrl = VirtualListController.getInstance()
        ctrl.setCaption("About")
        ctrl.setModel(model)
        ctrl.setClickListListener(null) // read-only, no selection needed
        ctrl.notifyUpdate()
    }

    private fun getAbilities(): String {
        val list = mutableListOf<String>()
        if (true) list.add("CLIPBOARD")
        if (true) list.add("HTTPCONNECT")
        if (true) list.add("IMPORT_EXPORT")
        if (true) list.add("LAST_MESSAGES")
        if (true) list.add("LOGROTATE")
        if (true) list.add("NICK_COLORS")
        if (true) list.add("NOMMEDIA")
        if (true) list.add("PEP")
        if (true) list.add("PEP_ACTIVITY")
        if (true) list.add("PRIVACY")
        if (true) list.add("SERVICE_DISCOVERY")
        if (true) list.add("SMILES")
        if (true) list.add("STATS")
        if (true) list.add("TEMPLATES")
        if (true) list.add("TLS")
        if (true) list.add("USER_KEYS")
        if (true) list.add("USE_ROTATOR")
        if (true) list.add("FILE_IO")
        if (true) list.add("FILE_TRANSFER")
        if (true) list.add("GRADIENT")
        if (true) list.add("HISTORY")
        if (true) list.add("ANI_SMILES")
        if (true) list.add("ARCHIVE")
        if (true) list.add("CAPTCHA")
        if (true) list.add("CLIENTS_ICONS")
        if (true) list.add("COLOR_TUNE")
        if (true) list.add("CONSOLE")
        if (StaticData.Debug) list.add("DEBUG")
        if (StaticData.NonSaslAuth) list.add("NON_SASL_AUTH")
        if (true) list.add("PEP_TUNE")
        if (true) list.add("DETRANSLIT")
        if (true) list.add("JUICK")
        if (StaticData.XmlDebug) list.add("XML_STREAM_DEBUG")
        return list.joinToString(", ")
    }
}
