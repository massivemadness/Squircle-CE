/*
 * Copyright 2025 Squircle CE contributors.
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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.ds.radio.RadioStyleDefaults

@Composable
fun ListPreference(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true,
    entries: Array<String> = emptyArray(),
    entryValues: Array<String> = emptyArray(),
    entryNameAsSubtitle: Boolean = false,
    selectedValue: String = "",
    onValueSelected: (String) -> Unit = {},
    dialogShown: Boolean = false,
    dialogTitle: String? = null,
) {
    var showDialog by rememberSaveable { mutableStateOf(dialogShown) }
    val displaySubtitle = remember(entries, entryValues, entryNameAsSubtitle) {
        if (entryNameAsSubtitle) {
            val entryIndex = entryValues.indexOf(selectedValue)
            if (entryIndex > -1) {
                entries[entryIndex]
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
        onClick = { showDialog = true },
        modifier = modifier,
    )
    if (showDialog) {
        AlertDialog(
            title = dialogTitle ?: title,
            verticalScroll = false,
            horizontalPadding = false,
            content = {
                val selectedIndex = entryValues.indexOf(selectedValue)
                val lazyListState = rememberLazyListState(
                    initialFirstVisibleItemIndex = selectedIndex
                )
                LazyColumn(state = lazyListState) {
                    itemsIndexed(entryValues) { index, value ->
                        val interactionSource = remember { MutableInteractionSource() }
                        Box(
                            modifier = Modifier
                                .debounceClickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(),
                                    onClick = { onValueSelected(value) }
                                )
                                .padding(horizontal = 24.dp)
                        ) {
                            Radio(
                                title = entries[index],
                                checked = index == selectedIndex,
                                onClick = {
                                    showDialog = false
                                    onValueSelected(value)
                                },
                                radioStyle = RadioStyleDefaults.Primary.copy(
                                    textStyle = SquircleTheme.typography.text18Regular,
                                ),
                                interactionSource = interactionSource,
                                indication = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                        }
                    }
                }
            },
            dismissButton = stringResource(android.R.string.cancel),
            onDismissClicked = { showDialog = false },
            onDismiss = { showDialog = false },
        )
    }
}

@PreviewLightDark
@Composable
private fun ListPreferencePreview() {
    PreviewBackground {
        ListPreference(
            title = "Application Theme",
            subtitle = "Configure the application theme",
            entries = arrayOf("Light", "Dark", "System default"),
            entryValues = arrayOf("light", "dark", "system"),
            selectedValue = "light",
            dialogShown = true,
        )
    }
}