@file:Suppress("DEPRECATION")

package org.bombusmod.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bombusmod.compose.theme.MyColors
import ui.NativeScreenCommand
import ui.NativeScreenModel
import ui.VirtualListController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHost() {
    val controller = VirtualListController.getInstance()
    val model = controller.model
    val caption = controller.caption ?: "BombusMod"

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
                        // Left: OK
                        val okCmd = commands.firstOrNull { it.type == NativeScreenCommand.OK }
                        TextButton(onClick = {
                            controller.buildOptionsMenu
                                ?.onOptionsItemSelected(null, okCmd?.key)
                        }) {
                            Text(okCmd?.label ?: "OK", color = MyColors.BAR_INK)
                        }
                        // Right: Back
                        val backCmd = commands.firstOrNull {
                            it.type == NativeScreenCommand.BACK
                                || it.type == NativeScreenCommand.CANCEL
                        }
                        TextButton(onClick = {
                            if (backCmd != null)
                                controller.buildOptionsMenu
                                    ?.onOptionsItemSelected(null, backCmd.key)
                            else controller.back()
                        }) {
                            Text(backCmd?.label ?: "Back", color = MyColors.BAR_INK)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (model != null && model.elements.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                itemsIndexed(model.elements) { index, item ->
                    Surface(
                        color = MyColors.LIST_BGND,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                controller.clickListListener
                                    ?.itemSelected(null, index)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (item.label != null) {
                                    Text(
                                        text = item.label,
                                        color = MyColors.LIST_INK,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                if (item.description != null) {
                                    Text(
                                        text = item.description,
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
    }
}
