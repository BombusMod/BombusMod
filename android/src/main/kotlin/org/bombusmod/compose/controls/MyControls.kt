package org.bombusmod.compose.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.bombusmod.compose.theme.MyColors

/** Larger corner radius matching defform's rounded-rect style */
private val FieldShape = RoundedCornerShape(12.dp)

// ─── Display controls ────────────────────────────────────────────

/** Read-only text line — replaces SimpleString */
@Composable
fun MyTextLine(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MyColors.LIST_INK,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

/** Clickable link text — replaces LinkString */
@Composable
fun MyLinkText(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MyColors.CONTROL_ITEM,
        style = MaterialTheme.typography.bodyLarge.copy(
            textDecoration = TextDecoration.Underline
        ),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable(onClick = onClick)
    )
}

/** Multi-line read-only text — replaces MultiLine */
@Composable
fun MyMultilineText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MyColors.LIST_INK,
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/** Vertical spacer — replaces SpacerItem */
@Composable
fun MySpacer(heightDp: Int = 8, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(heightDp.dp))
}

/** Section header — replaces ItemsGroup header */
@Composable
fun MyHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MyColors.LIST_INK,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

// ─── Input controls ──────────────────────────────────────────────

/** Text input — replaces TextInput + EditBox */
@Composable
fun MyTextField(
    caption: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        if (caption.isNotEmpty()) {
            Text(
                text = caption,
                color = MyColors.LIST_INK,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            shape = FieldShape,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MyColors.CONTROL_ITEM,
                unfocusedBorderColor = MyColors.SECOND_LINE
            )
        )
    }
}

/** Numeric input — replaces NumberInput */
@Composable
fun MyNumberInput(
    caption: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE
) {
    var text by remember(value) {
        mutableStateOf(value.toString())
    }
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        if (caption.isNotEmpty()) {
            Text(
                text = caption,
                color = MyColors.LIST_INK,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                val filtered = newText.filter { it.isDigit() || it == '-' }
                text = filtered
                filtered.toIntOrNull()?.let { v ->
                    onValueChange(v.coerceIn(min, max))
                }
            },
            singleLine = true,
            shape = FieldShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MyColors.CONTROL_ITEM,
                unfocusedBorderColor = MyColors.SECOND_LINE
            )
        )
    }
}

/** Password input — replaces PasswordInput */
@Composable
fun MyPasswordInput(
    caption: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        if (caption.isNotEmpty()) {
            Text(
                text = caption,
                color = MyColors.LIST_INK,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            shape = FieldShape,
            visualTransformation = if (visible) VisualTransformation.None
                else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { visible = !visible }) {
                    Text(if (visible) "Hide" else "Show")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MyColors.CONTROL_ITEM,
                unfocusedBorderColor = MyColors.SECOND_LINE
            )
        )
    }
}

// ─── Selection controls ─────────────────────────────────────────

/** Checkbox — replaces CheckBox */
@Composable
fun MyCheckBox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MyColors.CONTROL_ITEM
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = MyColors.LIST_INK,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/** Dropdown selector — replaces DropChoiceBox + DropListBox */
@Composable
fun MyDropdown(
    caption: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        if (caption.isNotEmpty()) {
            Text(
                text = caption,
                color = MyColors.LIST_INK,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Box {
            OutlinedTextField(
                value = if (selectedIndex in options.indices) options[selectedIndex] else "",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                shape = FieldShape,
                trailingIcon = {
                    TextButton(onClick = { expanded = true }) {
                        Text("▾")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MyColors.CONTROL_ITEM,
                    unfocusedBorderColor = MyColors.SECOND_LINE
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/** Slider — replaces TrackItem */
@Composable
fun MySlider(
    caption: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        if (caption.isNotEmpty()) {
            Text(
                text = "$caption: ${value.toInt()}",
                color = MyColors.LIST_INK,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MyColors.CONTROL_ITEM,
                activeTrackColor = MyColors.CONTROL_ITEM
            )
        )
    }
}
