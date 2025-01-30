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

package com.blacksquircle.ui.feature.shortcuts.ui.fragment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.domain.model.KeyGroup
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun ShortcutsScreen(viewModel: ShortcutsViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    ShortcutsScreen(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onRestoreClicked = viewModel::onRestoreClicked,
        onKeyAssigned = viewModel::onKeyAssigned,
    )
}

@Composable
private fun ShortcutsScreen(
    viewState: ShortcutsState,
    onBackClicked: () -> Unit,
    onRestoreClicked: () -> Unit,
    onKeyAssigned: (Keybinding) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_keybindings_title),
                backIcon = UiR.drawable.ic_back,
                onBackClicked = onBackClicked,
                menuItems = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            painter = painterResource(UiR.drawable.ic_overflow),
                            contentDescription = null
                        )
                        PopupMenu(
                            expanded = expanded,
                            onDismiss = { expanded = false }
                        ) {
                            PopupMenuItem(
                                title = stringResource(R.string.action_restore),
                                onClick = {
                                    onRestoreClicked()
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            viewState.shortcuts.forEach { (keyGroup, keybindings) ->
                item(
                    key = keyGroup.name,
                    contentType = 1
                ) {
                    // Don't draw divider for the first group
                    if (keyGroup != KeyGroup.FILE) {
                        HorizontalDivider()
                    }
                    val title = when (keyGroup) {
                        KeyGroup.FILE -> stringResource(R.string.pref_category_file_keybindings)
                        KeyGroup.EDITOR -> stringResource(R.string.pref_category_editor_keybindings)
                        KeyGroup.TOOLS -> stringResource(R.string.pref_category_tools_keybindings)
                    }
                    PreferenceGroup(title)
                }
                items(
                    items = keybindings,
                    key = { it.shortcut.key },
                    contentType = { 2 }
                ) { keybinding ->
                    // TODO KeybindingPreference
                    Preference(
                        title = when (keybinding.shortcut) {
                            Shortcut.NEW -> stringResource(R.string.shortcut_new_file)
                            Shortcut.OPEN -> stringResource(R.string.shortcut_open_file)
                            Shortcut.SAVE -> stringResource(R.string.shortcut_save_file)
                            Shortcut.SAVE_AS -> stringResource(R.string.shortcut_save_as)
                            Shortcut.CLOSE -> stringResource(R.string.shortcut_close_file)
                            Shortcut.CUT -> stringResource(android.R.string.cut)
                            Shortcut.COPY -> stringResource(android.R.string.copy)
                            Shortcut.PASTE -> stringResource(android.R.string.paste)
                            Shortcut.SELECT_ALL -> stringResource(android.R.string.selectAll)
                            Shortcut.SELECT_LINE -> stringResource(R.string.shortcut_select_line)
                            Shortcut.DELETE_LINE -> stringResource(R.string.shortcut_delete_line)
                            Shortcut.DUPLICATE_LINE -> stringResource(R.string.shortcut_duplicate_line)
                            Shortcut.TOGGLE_CASE -> stringResource(R.string.shortcut_toggle_case)
                            Shortcut.PREV_WORD -> stringResource(R.string.shortcut_prev_word)
                            Shortcut.NEXT_WORD -> stringResource(R.string.shortcut_next_word)
                            Shortcut.START_OF_LINE -> stringResource(R.string.shortcut_start_of_line)
                            Shortcut.END_OF_LINE -> stringResource(R.string.shortcut_end_of_line)
                            Shortcut.UNDO -> stringResource(R.string.shortcut_undo)
                            Shortcut.REDO -> stringResource(R.string.shortcut_redo)
                            Shortcut.FIND -> stringResource(R.string.shortcut_find)
                            Shortcut.REPLACE -> stringResource(R.string.shortcut_replace)
                            Shortcut.GOTO_LINE -> stringResource(R.string.shortcut_goto_line)
                            Shortcut.FORCE_SYNTAX -> stringResource(R.string.shortcut_force_syntax)
                            Shortcut.INSERT_COLOR -> stringResource(R.string.shortcut_insert_color)
                        },
                        subtitle = keybindingResource(keybinding),
                    )
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
private fun keybindingResource(keybinding: Keybinding): String {
    return StringBuilder().apply {
        if (keybinding.key == '\u0000') {
            append(stringResource(R.string.shortcut_none))
        } else {
            if (keybinding.isCtrl) append(stringResource(UiR.string.common_ctrl) + " + ")
            if (keybinding.isShift) append(stringResource(UiR.string.common_shift) + " + ")
            if (keybinding.isAlt) append(stringResource(UiR.string.common_alt) + " + ")
            append(keybinding.key)
        }
    }.toString()
}

@Preview
@Composable
private fun ShortcutsScreenPreview() {
    SquircleTheme {
        ShortcutsScreen(
            viewState = ShortcutsState(
                shortcuts = Shortcut.entries
                    .map(::Keybinding)
                    .groupBy { it.shortcut.group }
            ),
            onBackClicked = {},
            onRestoreClicked = {},
            onKeyAssigned = {},
        )
    }
}