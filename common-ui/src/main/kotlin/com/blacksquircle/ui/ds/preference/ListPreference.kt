/*
 * Copyright 2023 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.ds.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog

@Composable
fun ListPreference(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    entries: Array<String> = emptyArray(),
    entryValues: Array<String> = emptyArray(),
    entryNameAsSubtitle: Boolean = false,
    selectedValue: String = "",
    onValueSelected: (String) -> Unit = {},
) {
    var dialogShown by rememberSaveable { mutableStateOf(false) }
    val displaySubtitle = remember(entries, entryValues, entryNameAsSubtitle) {
        if (entryNameAsSubtitle) {
            val valueIndex = entryValues.indexOf(selectedValue)
            if (valueIndex > -1) {
                entries[valueIndex]
            } else {
                subtitle.toString()
            }
        } else {
            subtitle
        }
    }
    Preference(
        title = title,
        subtitle = displaySubtitle,
        enabled = enabled,
        onClick = { dialogShown = true },
    )
    if (dialogShown) {
        AlertDialog(
            title = title,
            content = {
                Column {
                    entryValues.forEachIndexed { index, value ->
                        SelectableItem(
                            entryName = entries[index],
                            entryValue = value,
                            selected = value == selectedValue,
                            onSelect = { selectedValue ->
                                dialogShown = false
                                onValueSelected(selectedValue)
                            },
                        )
                    }
                }
            },
            dismissButton = stringResource(android.R.string.cancel),
            onDismissClicked = { dialogShown = false },
            onDismiss = { dialogShown = false },
        )
    }
}

@Composable
private fun SelectableItem(
    entryName: String,
    entryValue: String,
    selected: Boolean,
    onSelect: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { onSelect(entryValue) }
    ) {
        RadioButton(
            selected = selected,
            onClick = { onSelect(entryValue) },
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = entryName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp
        )
    }
}

@Preview
@Composable
private fun SelectableItemCheckedPreview() {
    SquircleTheme {
        SelectableItem(
            entryName = "Entry name",
            entryValue = "Entry value",
            selected = true,
            onSelect = {}
        )
    }
}

@Preview
@Composable
private fun SelectableItemUncheckedPreview() {
    SquircleTheme {
        SelectableItem(
            entryName = "Entry name",
            entryValue = "Entry value",
            selected = false,
            onSelect = {}
        )
    }
}