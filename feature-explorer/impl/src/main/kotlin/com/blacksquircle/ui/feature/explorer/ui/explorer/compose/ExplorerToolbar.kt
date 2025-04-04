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
import androidx.compose.ui.util.fastMap
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.dropdown.Dropdown
import com.blacksquircle.ui.ds.dropdown.DropdownStyleDefaults
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.ds.toolbar.ToolbarSizeDefaults
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.menu.SelectionMenu
import com.blacksquircle.ui.feature.explorer.ui.explorer.menu.SortingMenu
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ExplorerToolbar(
    searchQuery: String,
    selectedFilesystem: String,
    filesystems: List<FilesystemModel>,
    selectedFiles: List<FileModel>,
    showHidden: Boolean,
    sortMode: SortMode,
    modifier: Modifier = Modifier,
    onFilesystemSelected: (String) -> Unit = {},
    onAddServerClicked: () -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onShowHiddenClicked: () -> Unit = {},
    onSortModeSelected: (SortMode) -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {},
    onCutClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onOpenWithClicked: () -> Unit = {},
    onRenameClicked: () -> Unit = {},
    onPropertiesClicked: () -> Unit = {},
    onCopyPathClicked: () -> Unit = {},
    onCompressClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    val selectionMode = selectedFiles.isNotEmpty()
    var searchMode by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Toolbar(
        title = if (selectionMode) selectedFiles.size.toString() else null,
        navigationIcon = if (selectionMode) UiR.drawable.ic_back else UiR.drawable.ic_close,
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
                            iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
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
            } else if (filesystems.isNotEmpty()) {
                val addServerEntry = stringResource(R.string.storage_add)
                val addServerValue = "add_server"

                val entries = remember(filesystems) {
                    (filesystems.fastMap(FilesystemModel::title) + addServerEntry).toTypedArray()
                }
                val entryValues = remember(filesystems) {
                    (filesystems.fastMap(FilesystemModel::uuid) + addServerValue).toTypedArray()
                }

                Dropdown(
                    entries = entries,
                    entryValues = entryValues,
                    currentValue = selectedFilesystem,
                    onValueSelected = { value ->
                        if (value == addServerValue) {
                            onAddServerClicked()
                        } else {
                            onFilesystemSelected(value)
                        }
                    },
                    dropdownStyle = DropdownStyleDefaults.Default.copy(
                        textStyle = SquircleTheme.typography.text18Medium,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            if (!searchMode && !selectionMode) {
                IconButton(
                    iconResId = UiR.drawable.ic_search,
                    onClick = { searchMode = true },
                )
            }

            if (selectionMode) {
                IconButton(
                    iconResId = UiR.drawable.ic_copy,
                    onClick = onCopyClicked,
                )
                IconButton(
                    iconResId = UiR.drawable.ic_delete,
                    onClick = onDeleteClicked,
                )
            }

            IconButton(
                iconResId = UiR.drawable.ic_dots_vertical,
                onClick = { menuExpanded = true },
                anchor = {
                    if (selectionMode) {
                        SelectionMenu(
                            count = selectedFiles.size,
                            expanded = menuExpanded,
                            onDismiss = { menuExpanded = false },
                            onCutClicked = { menuExpanded = false; onCutClicked() },
                            onSelectAllClicked = { menuExpanded = false; onSelectAllClicked() },
                            onOpenWithClicked = { menuExpanded = false; onOpenWithClicked() },
                            onRenameClicked = { menuExpanded = false; onRenameClicked() },
                            onPropertiesClicked = { menuExpanded = false; onPropertiesClicked() },
                            onCopyPathClicked = { menuExpanded = false; onCopyPathClicked() },
                            onCompressClicked = { menuExpanded = false; onCompressClicked() },
                        )
                    } else {
                        SortingMenu(
                            expanded = menuExpanded,
                            onDismiss = { menuExpanded = false },
                            showHidden = showHidden,
                            sortMode = sortMode,
                            onSortModeSelected = { menuExpanded = false; onSortModeSelected(it) },
                            onShowHiddenClicked = { menuExpanded = false; onShowHiddenClicked() },
                        )
                    }
                }
            )
        },
        toolbarSize = ToolbarSizeDefaults.S,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun ExplorerToolbarPreview() {
    PreviewBackground {
        ExplorerToolbar(
            searchQuery = "",
            selectedFilesystem = LocalFilesystem.LOCAL_UUID,
            filesystems = listOf(
                FilesystemModel(
                    uuid = LocalFilesystem.LOCAL_UUID,
                    title = "Local Storage",
                    defaultLocation = FileModel(
                        fileUri = "file:///storage/emulated/0/",
                        filesystemUuid = LocalFilesystem.LOCAL_UUID,
                    ),
                ),
                FilesystemModel(
                    uuid = RootFilesystem.ROOT_UUID,
                    title = "Root Directory",
                    defaultLocation = FileModel(
                        fileUri = "sufile:///",
                        filesystemUuid = RootFilesystem.ROOT_UUID,
                    ),
                ),
            ),
            selectedFiles = emptyList(),
            showHidden = true,
            sortMode = SortMode.SORT_BY_NAME,
            onFilesystemSelected = {},
            onQueryChanged = {},
            onClearQueryClicked = {},
            onShowHiddenClicked = {},
            onSortModeSelected = {},
            onBackClicked = {},
        )
    }
}