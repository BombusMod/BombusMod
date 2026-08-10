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
import androidx.compose.ui.unit.dp
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
                if (!controller.popStack()) {
                    controller.onCancel?.run()
                }
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


    // Reconnect overlay — check every recomposition
    val rw = ui.VirtualCanvas.getInstance().rw
    val showReconnect = rw != null && rw.isActive()

    if (list == null || list.getItemCount() == 0) {
        if (legacyView != null) {
            AndroidView(factory = { legacyView }, modifier = Modifier.fillMaxSize())
        }
        return
    }

    var showMenu by remember { mutableStateOf(false) }

    // SCREEN-type menu commands from DefForm
    val menuCommands = remember(list, updateVersion) {
        controller.getMenuCommands().filter { it.map == MenuCommand.SCREEN }
    }

    Scaffold(
        topBar = {
            // Read first icon from mainbar ComplexString elements
            val barIcon = list?.mainbar?.let { mb ->
                var idx = -1
                val count = (mb as java.util.Vector<*>).size
                for (i in 0 until count) {
                    val el = (mb as java.util.Vector<*>).elementAt(i)
                    when {
                        el is java.lang.Integer -> { idx = el.toInt(); break }
                        el is IconTextElement -> { idx = el.getImageIndex(); if (idx >= 0) break }
                    }
                }
                idx
            } ?: -1

            TopAppBar(
                title = { Text(caption, color = MyColors.BAR_INK) },
                navigationIcon = {
                    if (barIcon >= 0) {
                        MySpriteIcon(imageIndex = barIcon, iconSize = 24.dp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MyColors.BAR_BGND,
                    titleContentColor = MyColors.BAR_INK
                ),
                actions = {
                    if (menuCommands.isNotEmpty()) {
                        Box {
                            TextButton(onClick = { showMenu = true }) {
                                Text("Menu", color = MyColors.BAR_INK)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                menuCommands.forEach { cmd ->
                                    DropdownMenuItem(
                                        text = { Text(cmd.name) },
                                        onClick = {
                                            showMenu = false
                                            if (list is DefForm) {
                                                (list as DefForm).menuAction(cmd, list)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(color = MyColors.BAR_BGND) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left button
                    val leftLabel = controller.getLeftCommand()
                    TextButton(onClick = {
                        if (list is DefForm) {
                            (list as DefForm).touchLeftPressed()
                        }
                    }) {
                        Text(leftLabel, color = MyColors.BAR_INK)
                    }
                    // Right button
                    val rightLabel = controller.getRightCommand()
                    TextButton(onClick = {
                        if (list is DefForm) {
                            (list as DefForm).touchRightPressed()
                        }
                    }) {
                        Text(rightLabel, color = MyColors.BAR_INK)
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Reconnect progress bar
            if (showReconnect && rw != null) {
                val progress = if (rw.timeout > 0) rw.pos.toFloat() / rw.timeout else 0f
                Surface(
                    color = MyColors.LIST_BGND_EVEN,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Reconnecting...",
                            color = MyColors.LIST_INK,
                            style = MaterialTheme.typography.bodyLarge
                        )
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
            // Main list
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f).imePadding()) {
                val itemCount = list.getItemCount()
                items(itemCount) { index ->
                    key("item_$index") {
                        RenderVirtualElement(list.getItemRef(index), index, controller)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderVirtualElement(
    el: VirtualElement?,
    index: Int,
    controller: VirtualListController
) {
    if (el == null) return
    val list = controller.currentList ?: return

    when {
        // ─── CheckBox ──────────────────────────────────
        el is CheckBox -> {
            var checked by remember("cb_$index", el.getValue()) { mutableStateOf(el.getValue()) }
            MyCheckBox(
                label = el.toString(),
                checked = checked,
                onCheckedChange = { c ->
                    checked = c
                    el.setValue(c)
                }
            )
        }

        // ─── DropChoiceBox ────────────────────────────
        el is DropChoiceBox -> {
            val size = el.size()
            val options = (0 until size).map { i ->
                val saved = el.getSelectedIndex()
                el.setSelectedIndex(i)
                val text = el.toString()
                el.setSelectedIndex(saved)
                text
            }
            var sel by remember("dc_$index", el.getSelectedIndex()) {
                mutableStateOf(el.getSelectedIndex())
            }
            MyDropdown(
                caption = el.toString(),
                options = options,
                selectedIndex = sel,
                onSelect = { s ->
                    sel = s
                    el.setSelectedIndex(s)
                }
            )
        }

        // ─── TextInput ────────────────────────────────
        el is TextInput -> {
            var text by remember("ti_$index", el.getValue()) { mutableStateOf(el.getValue()) }
            MyTextField(
                caption = el.caption ?: "",
                value = text,
                onValueChange = { t -> text = t; el.setValue(t) }
            )
        }

        // ─── PasswordInput ────────────────────────────
        el is PasswordInput -> {
            var pass by remember("pw_$index", el.getText()) { mutableStateOf(el.getText()) }
            MyPasswordInput(
                caption = "",
                value = pass,
                onValueChange = { p -> pass = p; el.setValue(p) }
            )
        }

        // ─── NumberInput ──────────────────────────────
        el is NumberInput -> {
            var num by remember("ni_$index", el.getValue()) {
                mutableStateOf(el.getValue().toIntOrNull() ?: 0)
            }
            MyNumberInput(
                caption = el.toString(),
                value = num,
                onValueChange = { n ->
                    num = n
                    el.setValue(n.toString())
                }
            )
        }

        // ─── TrackItem (slider) ───────────────────────
        el is TrackItem -> {
            var v by remember("sl_$index", el.getValue()) { mutableStateOf(el.getValue().toFloat()) }
            MySlider(
                caption = "",
                value = v,
                onValueChange = { s -> v = s; el.setValue(s.toInt()) }
            )
        }

        // ─── SimpleString ─────────────────────────────
        el is SimpleString -> {
            MyTextLine(text = el.toString())
        }

        // ─── LinkString ───────────────────────────────
        el is LinkString -> {
            MyLinkText(
                text = el.toString(),
                onClick = {
                    el.onSelect()
                    controller.notifyUpdate()
                }
            )
        }

        // ─── MultiLine ────────────────────────────────
        el is MultiLine -> {
            MyMultilineText(text = el.getValue())
        }

        // ─── SpacerItem ───────────────────────────────
        el is SpacerItem -> {
            MySpacer(heightDp = (el.getVHeight() / 3).coerceAtLeast(4))
        }

        // ─── ItemsGroupHeader (section header) ──────────
        el is ItemsGroup.ItemsGroupHeader -> {
            MyHeader(text = el.name ?: el.toString())
        }

        // ─── ImageItem ────────────────────────────────
        el is ImageItem -> {
            MyTextLine(text = el.altText ?: "[image]")
        }

        // ─── KeyInput ─────────────────────────────────
        el is KeyInput -> {
            MyTextLine(text = el.toString())
        }

        // ─── Roster: Group header ────────────────────
        el is Client.Group -> {
            Surface(
                color = MyColors.LIST_BGND_EVEN,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        el.onSelect()
                        // Rebuild roster items to reflect collapsed state
                        Client.StaticData.getInstance().roster.reEnumRoster()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MySpriteIcon(
                        imageIndex = el.getImageIndex(),
                        iconSize = 24.dp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        el.toString(),
                        color = MyColors.LIST_INK,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (el.unreadMessages > 0) {
                        Spacer(Modifier.weight(1f))
                        Surface(
                            color = MyColors.CONTROL_ITEM,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                " ${el.unreadMessages} ",
                                color = MyColors.BAR_INK,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        // ─── Roster: Contact ──────────────────────────
        el is Client.Contact -> {
            val unread = el.getNewMsgsCount()
            Surface(
                color = MyColors.LIST_BGND,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        el.onSelect()
                        controller.notifyUpdate()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 6.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MySpriteIcon(
                        imageIndex = el.getImageIndex(),
                        iconSize = 28.dp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            el.toString(),
                            color = MyColors.LIST_INK,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (el.statusString != null && el.statusString.isNotEmpty()) {
                            Text(
                                el.statusString,
                                color = MyColors.SECOND_LINE,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (unread > 0) {
                        Surface(
                            color = MyColors.CONTROL_ITEM,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                " $unread ",
                                color = MyColors.BAR_INK,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        // ─── Default: selectable row with optional icon ─
        else -> {
            val selectable = el.isSelectable()
            val label = el.toString()
            val desc = el.getTipString()
            val imageIndex = if (el is IconTextElement)
                (el as IconTextElement).getImageIndex() else -1

            Surface(
                color = MyColors.LIST_BGND,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = selectable) {
                        el.onSelect()
                        controller.notifyUpdate()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 4.dp, top = 6.dp, bottom = 6.dp, end = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (imageIndex >= 0) {
                        MySpriteIcon(
                            imageIndex = imageIndex,
                            iconSize = 28.dp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            label ?: "",
                            color = MyColors.LIST_INK,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (!desc.isNullOrEmpty()) {
                            Text(
                                desc,
                                color = MyColors.SECOND_LINE,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
