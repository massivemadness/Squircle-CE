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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.ds.toolbar.ToolbarSizeDefaults
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.feature.explorer.ui.explorer.menu.SelectionMenu
import com.blacksquircle.ui.feature.explorer.ui.explorer.menu.SortingMenu
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ExplorerToolbar(
    workspaceType: WorkspaceType,
    searchQuery: String,
    selection: List<FileNode>,
    showHidden: Boolean,
    compactPackages: Boolean,
    sortMode: SortMode,
    modifier: Modifier = Modifier,
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onShowHiddenClicked: () -> Unit = {},
    onCompactPackagesClicked: () -> Unit = {},
    onSortModeSelected: (SortMode) -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCutClicked: () -> Unit = {},
    onOpenWithClicked: () -> Unit = {},
    onOpenTerminalClicked: () -> Unit = {},
    onRenameClicked: () -> Unit = {},
    onPropertiesClicked: () -> Unit = {},
    onCopyPathClicked: () -> Unit = {},
    onCompressClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    val selectionMode = selection.isNotEmpty()
    val rootSelected = selection.size == 1 && selection[0].isRoot

    var searchMode by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Toolbar(
        title = if (selectionMode) selection.size.toString() else null,
        navigationIcon = if (selectionMode) UiR.drawable.ic_back else null,
        onNavigationClicked = onBackClicked,
        navigationActions = {
            if (selectionMode) {
                BackHandler {
                    onBackClicked()
                }
            } else if (searchMode) {
                val focusRequester = remember { FocusRequester() }
                TextField(
                    inputText = searchQuery,
                    onInputChanged = onQueryChanged,
                    placeholderText = stringResource(android.R.string.search_go),
                    startContent = {
                        Icon(
                            painter = painterResource(UiR.drawable.ic_search),
                            contentDescription = null,
                            tint = SquircleTheme.colors.colorTextAndIconSecondary,
                            modifier = Modifier.padding(8.dp),
                        )
                    },
                    endContent = {
                        IconButton(
                            iconResId = UiR.drawable.ic_close,
                            iconButtonStyle = IconButtonStyleDefaults.Secondary,
                            iconButtonSize = IconButtonSizeDefaults.S,
                            onClick = { onClearQueryClicked(); searchMode = false },
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .focusRequester(focusRequester)
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                BackHandler {
                    onClearQueryClicked()
                    searchMode = false
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            /** Don't show file actions if root node is selected */
            if (rootSelected) {
                return@Toolbar
            }

            if (!searchMode && !selectionMode) {
                IconButton(
                    iconResId = UiR.drawable.ic_search,
                    onClick = { searchMode = true },
                    contentDescription = stringResource(android.R.string.search_go)
                )
            }

            if (selectionMode) {
                if (workspaceType.isLocal()) {
                    IconButton(
                        iconResId = UiR.drawable.ic_copy,
                        onClick = onCopyClicked,
                        contentDescription = stringResource(android.R.string.copy),
                    )
                }
                IconButton(
                    iconResId = UiR.drawable.ic_delete,
                    onClick = onDeleteClicked,
                    contentDescription = stringResource(R.string.explorer_menu_selection_delete)
                )
            }

            IconButton(
                iconResId = UiR.drawable.ic_dots_vertical,
                onClick = { expanded = true },
                contentDescription = stringResource(UiR.string.common_menu),
                anchor = {
                    if (selectionMode) {
                        SelectionMenu(
                            selection = selection,
                            workspaceType = workspaceType,
                            expanded = expanded,
                            onDismiss = { expanded = false },
                            onCutClicked = { expanded = false; onCutClicked() },
                            onOpenWithClicked = { expanded = false; onOpenWithClicked() },
                            onOpenTerminalClicked = { expanded = false; onOpenTerminalClicked() },
                            onRenameClicked = { expanded = false; onRenameClicked() },
                            onPropertiesClicked = { expanded = false; onPropertiesClicked() },
                            onCopyPathClicked = { expanded = false; onCopyPathClicked() },
                            onCompressClicked = { expanded = false; onCompressClicked() },
                        )
                    } else {
                        SortingMenu(
                            expanded = expanded,
                            onDismiss = { expanded = false },
                            showHidden = showHidden,
                            compactPackages = compactPackages,
                            sortMode = sortMode,
                            onShowHiddenClicked = {
                                expanded = false
                                onShowHiddenClicked()
                            },
                            onCompactPackagesClicked = {
                                expanded = false
                                onCompactPackagesClicked()
                            },
                            onSortModeSelected = {
                                expanded = false
                                onSortModeSelected(it)
                            },
                        )
                    }
                }
            )
        },
        toolbarSize = ToolbarSizeDefaults.M.copy(shadowSize = 0.dp),
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun ExplorerToolbarPreview() {
    PreviewBackground {
        ExplorerToolbar(
            workspaceType = WorkspaceType.LOCAL,
            searchQuery = "",
            selection = emptyList(),
            showHidden = true,
            compactPackages = true,
            sortMode = SortMode.SORT_BY_NAME,
        )
    }
}