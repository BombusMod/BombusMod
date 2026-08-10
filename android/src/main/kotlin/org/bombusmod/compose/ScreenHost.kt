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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.bombusmod.BombusModActivity
import ui.NativeScreenCommand
import ui.NativeScreenModel
import ui.VirtualListController

/**
 * Top-level composable that observes the VirtualListController and renders
 * the current screen — either a legacy J2ME CanvasView or native Compose content.
 */
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
                title = { Text(caption) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    val commands = model?.commands ?: emptyList()
                    if (commands.isNotEmpty()) {
                        Box {
                            IconButton(onClick = { showMenu = !showMenu }) {
                                Text(
                                    "⋮",  // vertical ellipsis
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                commands.filter { it.type == NativeScreenCommand.SCREEN }
                                    .forEach { cmd ->
                                        DropdownMenuItem(
                                            text = { Text(cmd.label) },
                                            onClick = {
                                                showMenu = false
                                                controller.buildOptionsMenu?.onOptionsItemSelected(
                                                    null, cmd.key
                                                )
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
                MyCommandBar(commands = commands, controller = controller)
            }
        }
    ) { padding ->
        if (model != null && model.elements.isNotEmpty()) {
            MyList(
                model = model,
                controller = controller,
                modifier = Modifier.padding(padding)
            )
        } else {
            // No model set — show legacy canvas fallback
            val context = LocalContext.current
            val canvasView = remember {
                (context as? BombusModActivity)?.getContentView()
            }
            LegacyCanvasWrapper(canvasViewProvider = { canvasView })
        }
    }
}

/**
 * Compose equivalent of IconTextElement-based list rendering.
 */
@Composable
fun MyList(
    model: NativeScreenModel,
    controller: VirtualListController,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        itemsIndexed(model.elements) { index, item ->
            MyListItem(
                item = item,
                isSelected = false,
                onClick = {
                    controller.clickListListener?.itemSelected(null, index)
                }
            )
        }
    }
}

/**
 * Single list item row — replaces IconTextElement.drawItem().
 */
@Composable
fun MyListItem(
    item: ui.NativeScreenItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected)
            MaterialTheme.colorScheme.secondaryContainer
        else
            MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.imageIndex >= 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                ) {
                    Text("●")
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                if (item.label != null) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (item.description != null) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Bottom command bar — replaces MainBar softkey system.
 */
@Composable
fun MyCommandBar(
    commands: List<NativeScreenCommand>,
    controller: VirtualListController
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val okCmd = commands.firstOrNull { it.type == NativeScreenCommand.OK }
            TextButton(onClick = {
                controller.buildOptionsMenu?.onOptionsItemSelected(null, okCmd?.key)
            }) {
                Text(okCmd?.label ?: "OK")
            }

            Text(
                text = "",
                style = MaterialTheme.typography.labelSmall
            )

            val backCmd = commands.firstOrNull {
                it.type == NativeScreenCommand.BACK || it.type == NativeScreenCommand.CANCEL
            }
            TextButton(onClick = {
                if (backCmd != null) {
                    controller.buildOptionsMenu?.onOptionsItemSelected(null, backCmd.key)
                } else {
                    controller.back()
                }
            }) {
                Text(backCmd?.label ?: "Back")
            }
        }
    }
}
