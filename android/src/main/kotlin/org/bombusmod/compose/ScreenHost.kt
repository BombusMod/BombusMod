@file:Suppress("DEPRECATION")

package org.bombusmod.compose

import android.view.View
import androidx.compose.foundation.background
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
import org.bombusmod.compose.controls.*
import org.bombusmod.compose.theme.MyColors
import ui.NativeScreenCommand
import ui.NativeScreenItem
import ui.NativeScreenModel
import ui.VirtualListController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHost(legacyView: View? = null) {
    val controller = VirtualListController.getInstance()

    var updateVersion by remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        val listener = object : VirtualListController.OnUpdateListener {
            override fun update() { updateVersion++ }
            override fun back() {}
            override fun getCurrItem() = 0
            override fun setCurrentItemIndex(i: Int, sel: Boolean) {}
        }
        controller.setUpdateListener(listener)
        onDispose { controller.setUpdateListener(null) }
    }

    val model = controller.model
    @Suppress("UNUSED_EXPRESSION") updateVersion
    val caption = controller.caption ?: "BombusMod"

    if (model == null || model.elements.isEmpty()) {
        if (legacyView != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                val statusBarDp = WindowInsets.statusBars
                    .asPaddingValues().calculateTopPadding()
                Surface(
                    color = MyColors.BAR_BGND,
                    modifier = Modifier.fillMaxWidth().height(statusBarDp)
                ) {}
                AndroidView(
                    factory = { legacyView },
                    modifier = Modifier.fillMaxSize().statusBarsPadding()
                )
            }
        }
        return
    }

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(caption, color = MyColors.BAR_INK) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MyColors.BAR_BGND,
                    titleContentColor = MyColors.BAR_INK
                ),
                actions = {
                    val commands = model?.commands ?: emptyList()
                    val screenCmds = commands.filter { it.type == NativeScreenCommand.SCREEN }
                    if (screenCmds.isNotEmpty()) {
                        Box {
                            TextButton(onClick = { showMenu = true }) {
                                Text("Menu", color = MyColors.BAR_INK)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                screenCmds.forEach { cmd ->
                                    DropdownMenuItem(
                                        text = { Text(cmd.label) },
                                        onClick = {
                                            showMenu = false
                                            controller.buildOptionsMenu
                                                ?.onOptionsItemSelected(null, cmd.key)
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
            val commands = model?.commands ?: emptyList()
            if (commands.isNotEmpty()) {
                Surface(color = MyColors.BAR_BGND) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val okCmd = commands.firstOrNull { it.type == NativeScreenCommand.OK }
                        TextButton(onClick = { controller.onDismiss?.run() }) {
                            Text(okCmd?.label ?: "OK", color = MyColors.BAR_INK)
                        }
                        val backCmd = commands.firstOrNull {
                            it.type == NativeScreenCommand.BACK
                                || it.type == NativeScreenCommand.CANCEL
                        }
                        TextButton(onClick = { controller.onDismiss?.run() }) {
                            Text(backCmd?.label ?: "Back", color = MyColors.BAR_INK)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
        ) {
            itemsIndexed(model.elements) { index, item ->
                RenderItem(item, index, controller)
            }
        }
    }
}

@Composable
private fun RenderItem(item: NativeScreenItem, index: Int, controller: VirtualListController) {
    when (item.controlType) {
        NativeScreenItem.TYPE_CHECKBOX -> {
            var checked by remember(item.key, item.checked) { mutableStateOf(item.checked) }
            MyCheckBox(label = item.label ?: "", checked = checked,
                onCheckedChange = { c -> checked = c; item.checked = c
                    controller.clickListListener?.itemSelected(null, index) })
        }
        NativeScreenItem.TYPE_INPUT -> {
            var text by remember(item.key, item.textValue) { mutableStateOf(item.textValue ?: "") }
            MyTextField(caption = item.label ?: "", value = text,
                onValueChange = { t -> text = t; item.textValue = t })
        }
        NativeScreenItem.TYPE_NUMBER -> {
            var num by remember(item.key, item.intValue) { mutableStateOf(item.intValue) }
            MyNumberInput(caption = item.label ?: "", value = num,
                onValueChange = { n -> num = n; item.intValue = n })
        }
        NativeScreenItem.TYPE_PASSWORD -> {
            var pass by remember(item.key, item.textValue) { mutableStateOf(item.textValue ?: "") }
            MyPasswordInput(caption = item.label ?: "", value = pass,
                onValueChange = { p -> pass = p; item.textValue = p })
        }
        NativeScreenItem.TYPE_DROPDOWN -> {
            val options = item.options ?: emptyArray()
            var sel by remember(item.key, item.intValue) { mutableStateOf(item.intValue) }
            MyDropdown(caption = item.label ?: "", options = options.toList(),
                selectedIndex = sel, onSelect = { s -> sel = s; item.intValue = s })
        }
        NativeScreenItem.TYPE_SLIDER -> {
            var v by remember(item.key, item.floatValue) { mutableStateOf(item.floatValue) }
            MySlider(caption = item.label ?: "", value = v,
                onValueChange = { s -> v = s; item.floatValue = s },
                valueRange = item.sliderMin..item.sliderMax)
        }
        NativeScreenItem.TYPE_HEADER -> MyHeader(text = item.label ?: "")
        NativeScreenItem.TYPE_LINK -> MyLinkText(text = item.label ?: "",
            onClick = { controller.clickListListener?.itemSelected(null, index) })
        NativeScreenItem.TYPE_MULTILINE ->
            MyMultilineText(text = item.description ?: item.label ?: "")
        NativeScreenItem.TYPE_SPACER ->
            MySpacer(heightDp = item.intValue.coerceAtLeast(4))
        NativeScreenItem.TYPE_IMAGE -> MyTextLine(text = "[image:${item.imageIndex}]")
        else -> {
            Surface(color = MyColors.LIST_BGND, modifier = Modifier.fillMaxWidth()
                .clickable(enabled = item.selectable,
                    onClick = { controller.clickListListener?.itemSelected(null, index) })) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (item.label != null) Text(item.label, color = MyColors.LIST_INK,
                            style = MaterialTheme.typography.bodyLarge)
                        if (item.description != null) Text(item.description,
                            color = MyColors.SECOND_LINE, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
