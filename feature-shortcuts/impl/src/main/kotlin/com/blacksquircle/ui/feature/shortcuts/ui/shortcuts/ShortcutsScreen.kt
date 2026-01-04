/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.shortcuts.ui.shortcuts

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.effect.ResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.api.model.KeyGroup
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.internal.ShortcutsComponent
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.compose.Combination
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_SAVE = "KEY_SAVE"
internal const val KEY_RESOLVE = "KEY_RESOLVE"

@Composable
internal fun ShortcutsScreen(
    viewModel: ShortcutsViewModel = daggerViewModel { context ->
        val component = ShortcutsComponent.buildOrGet(context)
        ShortcutsViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ShortcutsScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onRestoreClicked = viewModel::onRestoreClicked,
        onKeyClicked = viewModel::onKeyClicked,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
            }
        }
    }

    ResultEffect<Keybinding>(KEY_SAVE) { keybinding ->
        viewModel.onSaveClicked(keybinding)
    }
    ResultEffect<Boolean>(KEY_RESOLVE) { reassign ->
        viewModel.onResolveClicked(reassign)
    }
}

@Composable
private fun ShortcutsScreen(
    viewState: ShortcutsViewState,
    onBackClicked: () -> Unit = {},
    onRestoreClicked: () -> Unit = {},
    onKeyClicked: (Keybinding) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.shortcuts_toolbar_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
                navigationActions = {
                    var expanded by rememberSaveable { mutableStateOf(false) }
                    IconButton(
                        iconResId = UiR.drawable.ic_dots_vertical,
                        iconButtonSize = IconButtonSizeDefaults.L,
                        onClick = { expanded = !expanded },
                        anchor = {
                            PopupMenu(
                                expanded = expanded,
                                onDismiss = { expanded = false },
                                verticalOffset = (-56).dp,
                            ) {
                                PopupMenuItem(
                                    title = stringResource(R.string.shortcuts_menu_restore),
                                    onClick = { onRestoreClicked(); expanded = false },
                                )
                            }
                        }
                    )
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            viewState.shortcuts.forEach { (keyGroup, keybindings) ->
                item(
                    key = keyGroup.name,
                    contentType = { 1 }
                ) {
                    val title = when (keyGroup) {
                        KeyGroup.FILE -> stringResource(R.string.shortcuts_category_file)
                        KeyGroup.EDITOR -> stringResource(R.string.shortcuts_category_editor)
                        KeyGroup.TOOLS -> stringResource(R.string.shortcuts_category_tools)
                    }
                    PreferenceGroup(title)
                }
                items(
                    items = keybindings,
                    key = { it.shortcut.key },
                    contentType = { 2 }
                ) { keybinding ->
                    Preference(
                        title = when (keybinding.shortcut) {
                            Shortcut.NEW -> stringResource(R.string.shortcuts_new_file_title)
                            Shortcut.OPEN -> stringResource(R.string.shortcuts_open_file_title)
                            Shortcut.SAVE -> stringResource(R.string.shortcuts_save_file_title)
                            Shortcut.SAVE_AS -> stringResource(R.string.shortcuts_save_file_as_title)
                            Shortcut.CLOSE -> stringResource(R.string.shortcuts_close_file_title)
                            Shortcut.CUT -> stringResource(android.R.string.cut)
                            Shortcut.COPY -> stringResource(android.R.string.copy)
                            Shortcut.PASTE -> stringResource(android.R.string.paste)
                            Shortcut.SELECT_ALL -> stringResource(android.R.string.selectAll)
                            Shortcut.SELECT_LINE -> stringResource(R.string.shortcuts_select_line_title)
                            Shortcut.DELETE_LINE -> stringResource(R.string.shortcuts_delete_line_title)
                            Shortcut.DUPLICATE_LINE -> stringResource(R.string.shortcuts_duplicate_line_title)
                            Shortcut.TOGGLE_CASE -> stringResource(R.string.shortcuts_toggle_case_title)
                            Shortcut.PREV_WORD -> stringResource(R.string.shortcuts_previous_word_title)
                            Shortcut.NEXT_WORD -> stringResource(R.string.shortcuts_next_word_title)
                            Shortcut.START_OF_LINE -> stringResource(R.string.shortcuts_start_of_line_title)
                            Shortcut.END_OF_LINE -> stringResource(R.string.shortcuts_end_of_line_title)
                            Shortcut.UNDO -> stringResource(R.string.shortcuts_undo_title)
                            Shortcut.REDO -> stringResource(R.string.shortcuts_redo_title)
                            Shortcut.FIND -> stringResource(R.string.shortcuts_find_title)
                            Shortcut.REPLACE -> stringResource(R.string.shortcuts_replace_title)
                            Shortcut.GOTO_LINE -> stringResource(R.string.shortcuts_goto_line_title)
                            Shortcut.FORCE_SYNTAX -> stringResource(R.string.shortcuts_force_syntax_title)
                            Shortcut.INSERT_COLOR -> stringResource(R.string.shortcuts_insert_color_title)
                        },
                        onClick = { onKeyClicked(keybinding) },
                        verticalAlignment = Alignment.CenterVertically,
                        trailingContent = {
                            Combination(keybinding)
                        }
                    )
                }
                if (keyGroup != KeyGroup.TOOLS) {
                    item {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ShortcutsScreenPreview() {
    PreviewBackground {
        ShortcutsScreen(
            viewState = ShortcutsViewState(
                shortcuts = Shortcut.entries
                    .map(::Keybinding)
                    .groupBy { it.shortcut.group }
            ),
        )
    }
}