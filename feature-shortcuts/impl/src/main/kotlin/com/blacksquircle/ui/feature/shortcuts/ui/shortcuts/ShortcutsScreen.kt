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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.NavResultEffect
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
import com.blacksquircle.ui.feature.shortcuts.data.mapper.ShortcutMapper
import com.blacksquircle.ui.feature.shortcuts.internal.ShortcutsComponent
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.compose.keybindingResource
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_SAVE = "KEY_SAVE"
internal const val KEY_RESOLVE = "KEY_RESOLVE"

internal const val ARG_REASSIGN = "ARG_REASSIGN"

@Composable
internal fun ShortcutsScreen(
    navController: NavController,
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
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    NavResultEffect(KEY_SAVE) { bundle ->
        val keybinding = ShortcutMapper.fromBundle(bundle)
        viewModel.onSaveClicked(keybinding)
    }
    NavResultEffect(KEY_RESOLVE) { bundle ->
        val reassign = bundle.getBoolean(ARG_REASSIGN)
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
                title = stringResource(R.string.pref_header_keybindings_title),
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
                                    title = stringResource(R.string.action_restore),
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
                        subtitle = keybindingResource(
                            ctrl = keybinding.isCtrl,
                            shift = keybinding.isShift,
                            alt = keybinding.isAlt,
                            key = keybinding.key,
                        ),
                        onClick = { onKeyClicked(keybinding) },
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