@file:Suppress("DEPRECATION")

package org.bombusmod.compose

import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import Menu.MenuCommand
import Client.Group
import Client.Contact
import ui.IconTextElement
import org.bombusmod.compose.controls.*
import org.bombusmod.compose.theme.MyColors
import ui.*
import ui.controls.form.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHost(legacyView: View? = null) {
    val controller = VirtualListController.getInstance()

    var updateVersion by remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val listener = object : VirtualListController.OnUpdateListener {
            override fun update() { updateVersion++ }
            override fun back() {
                if (!controller.popStack()) controller.onCancel?.run()
            }
            override fun getCurrItem() = 0
            override fun setCurrentItemIndex(i: Int, sel: Boolean) {}
        }
        controller.setUpdateListener(listener)
        onDispose { controller.setUpdateListener(null) }
    }

    val list = controller.currentList
    @Suppress("UNUSED_EXPRESSION") updateVersion
    val caption = controller.caption

    if (list == null || list.getItemCount() == 0) {
        if (legacyView != null) AndroidView(factory = { legacyView }, modifier = Modifier.fillMaxSize())
        return
    }

    // Reconnect overlay
    val rw = VirtualCanvas.getInstance().rw
    val showReconnect = rw != null && rw.isActive()

    var showMenu by remember { mutableStateOf(false) }
    val menuCommands = remember(list, updateVersion) {
        controller.getMenuCommands().filter { it.map == MenuCommand.SCREEN }
    }

    Scaffold(
        topBar = {
            val barIcon = list?.mainbar?.let { mb ->
                var idx = -1
                val count = (mb as java.util.Vector<*>).size
                for (i in 0 until count) {
                    when (val el = (mb as java.util.Vector<*>).elementAt(i)) {
                        is java.lang.Integer -> { idx = el.toInt(); break }
                        is IconTextElement -> { val ii = el.getImageIndex(); if (ii >= 0) { idx = ii; break } }
                    }
                }
                idx
            } ?: -1

            TopAppBar(
                title = { Text(caption, color = MyColors.BAR_INK) },
                navigationIcon = {
                    if (barIcon >= 0) MySpriteIcon(imageIndex = barIcon, iconSize = 24.dp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MyColors.BAR_BGND,
                    titleContentColor = MyColors.BAR_INK
                ),
                actions = {
                    if (menuCommands.isNotEmpty()) {
                        Box {
                            TextButton(onClick = { showMenu = true }) { Text("Menu", color = MyColors.BAR_INK) }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                menuCommands.forEach { cmd ->
                                    DropdownMenuItem(text = { Text(cmd.name) }, onClick = {
                                        showMenu = false
                                        if (list is DefForm) (list as DefForm).menuAction(cmd, list)
                                    })
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = MyColors.BAR_BGND) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { if (list is DefForm) (list as DefForm).touchLeftPressed() }) {
                        Text(controller.getLeftCommand(), color = MyColors.BAR_INK)
                    }
                    TextButton(onClick = { if (list is DefForm) (list as DefForm).touchRightPressed() }) {
                        Text(controller.getRightCommand(), color = MyColors.BAR_INK)
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (showReconnect && rw != null) {
                val progress = if (rw.getTimeout() > 0) rw.getPos().toFloat() / rw.getTimeout() else 0f
                Surface(color = MyColors.LIST_BGND_EVEN, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Reconnecting...", color = MyColors.LIST_INK, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth(),
                            color = MyColors.CONTROL_ITEM,
                            trackColor = MyColors.SECOND_LINE
                        )
                    }
                }
            }

            val count = list.getItemCount()
            val items = remember(count) { (0 until count).map { list.getItemRef(it) } }
            LazyColumn(Modifier.fillMaxWidth().weight(1f).imePadding()) {
                itemsIndexed(items) { index, el ->
                    key("item_$index") { RenderVirtualElement(el, index, controller) }
                }
            }
        }
    }
}

@Composable
private fun RenderVirtualElement(el: VirtualElement?, index: Int, controller: VirtualListController) {
    if (el == null) return
    val list = controller.currentList ?: return

    when {
        el is CheckBox -> {
            var checked by remember("cb_$index", el.getValue()) { mutableStateOf(el.getValue()) }
            MyCheckBox(label = el.toString(), checked = checked,
                onCheckedChange = { c -> checked = c; el.setValue(c) })
        }
        el is DropChoiceBox -> {
            val size = el.size()
            val options = (0 until size).map { i -> val saved = el.getSelectedIndex(); el.setSelectedIndex(i); val t = el.toString(); el.setSelectedIndex(saved); t }
            var sel by remember("dc_$index", el.getSelectedIndex()) { mutableStateOf(el.getSelectedIndex()) }
            MyDropdown(caption = el.toString(), options = options, selectedIndex = sel,
                onSelect = { s -> sel = s; el.setSelectedIndex(s) })
        }
        el is TextInput -> {
            var text by remember("ti_$index", el.getValue()) { mutableStateOf(el.getValue()) }
            MyTextField(caption = el.caption ?: "", value = text, onValueChange = { t -> text = t; el.setValue(t) })
        }
        el is PasswordInput -> {
            var pass by remember("pw_$index", el.getText()) { mutableStateOf(el.getText()) }
            MyPasswordInput(caption = "", value = pass, onValueChange = { p -> pass = p; el.setValue(p) })
        }
        el is NumberInput -> {
            var num by remember("ni_$index", el.getValue()) { mutableStateOf(el.getValue().toIntOrNull() ?: 0) }
            MyNumberInput(caption = el.toString(), value = num, onValueChange = { n -> num = n; el.setValue(n.toString()) })
        }
        el is TrackItem -> {
            var v by remember("sl_$index", el.getValue()) { mutableStateOf(el.getValue().toFloat()) }
            MySlider(caption = "", value = v, onValueChange = { s -> v = s; el.setValue(s.toInt()) })
        }
        el is SimpleString -> MyTextLine(text = el.toString())
        el is LinkString -> MyLinkText(text = el.toString(), onClick = { el.onSelect(); controller.notifyUpdate() })
        el is MultiLine -> MyMultilineText(text = el.getValue())
        el is SpacerItem -> MySpacer(heightDp = (el.getVHeight() / 3).coerceAtLeast(4))
        el is ItemsGroup.ItemsGroupHeader -> MyHeader(text = el.name ?: el.toString())
        el is ImageItem -> MyTextLine(text = el.altText ?: "[image]")
        el is KeyInput -> MyTextLine(text = el.toString())

        el is Group -> {
            Surface(color = MyColors.LIST_BGND_EVEN, modifier = Modifier.fillMaxWidth().clickable {
                el.onSelect()
                Client.StaticData.getInstance().roster.reEnumRoster()
                controller.notifyUpdate()
            }) {
                Row(Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    MySpriteIcon(imageIndex = el.getImageIndex(), iconSize = 24.dp, modifier = Modifier.padding(end = 4.dp))
                    Text(el.toString(), color = MyColors.LIST_INK, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    if (el.unreadMessages > 0) {
                        Spacer(Modifier.weight(1f))
                        Surface(color = MyColors.CONTROL_ITEM, shape = MaterialTheme.shapes.small) {
                            Text(" ${el.unreadMessages} ", color = MyColors.BAR_INK, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        el is Contact -> {
            val unread = el.getNewMsgsCount()
            Surface(color = MyColors.LIST_BGND, modifier = Modifier.fillMaxWidth().clickable {
                el.onSelect()
                controller.notifyUpdate()
            }) {
                Row(Modifier.padding(start = 4.dp, top = 6.dp, bottom = 6.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    MySpriteIcon(imageIndex = el.getImageIndex(), iconSize = 28.dp, modifier = Modifier.padding(end = 4.dp))
                    Spacer(Modifier.width(6.dp))
                    Column(Modifier.weight(1f)) {
                        Text(el.toString(), color = MyColors.LIST_INK, fontSize = 16.sp)
                        if (el.statusString != null && el.statusString.isNotEmpty())
                            Text(el.statusString, color = MyColors.SECOND_LINE, fontSize = 14.sp)
                    }
                    if (unread > 0) {
                        Surface(color = MyColors.CONTROL_ITEM, shape = MaterialTheme.shapes.small) {
                            Text(" $unread ", color = MyColors.BAR_INK, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        else -> {
            val selectable = el.isSelectable()
            val label = el.toString()
            val desc = el.getTipString()
            val imageIndex = if (el is IconTextElement) el.getImageIndex() else -1
            Surface(color = MyColors.LIST_BGND, modifier = Modifier.fillMaxWidth().clickable(enabled = selectable) {
                el.onSelect()
                controller.notifyUpdate()
            }) {
                Row(Modifier.padding(start = 4.dp, top = 6.dp, bottom = 6.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (imageIndex >= 0) {
                        MySpriteIcon(imageIndex = imageIndex, iconSize = 28.dp, modifier = Modifier.padding(end = 4.dp))
                        Spacer(Modifier.width(6.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(label ?: "", color = MyColors.LIST_INK, fontSize = 16.sp)
                        if (!desc.isNullOrEmpty()) Text(desc, color = MyColors.SECOND_LINE, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
