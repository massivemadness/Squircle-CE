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

package com.blacksquircle.ui.feature.explorer.ui.fragment.internal

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
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import com.blacksquircle.ui.filesystem.root.RootFilesystem
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ExplorerToolbar(
    searchQuery: String,
    currentFilesystem: String,
    filesystems: List<FilesystemModel>,
    modifier: Modifier = Modifier,
    onFilesystemSelected: (String) -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClearQueryClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    var searchMode by rememberSaveable {
        mutableStateOf(false)
    }
    Toolbar(
        toolbarSize = ToolbarSizeDefaults.S,
        navigationIcon = UiR.drawable.ic_close,
        onNavigationClicked = onBackClicked,
        navigationActions = {
            if (searchMode) {
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
                Dropdown(
                    entries = filesystems
                        .fastMap(FilesystemModel::title)
                        .toTypedArray(),
                    entryValues = filesystems
                        .fastMap(FilesystemModel::uuid)
                        .toTypedArray(),
                    currentValue = currentFilesystem,
                    onValueSelected = onFilesystemSelected,
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

            if (!searchMode) {
                IconButton(
                    iconResId = UiR.drawable.ic_search,
                    onClick = { searchMode = true },
                )
            }
            IconButton(
                iconResId = UiR.drawable.ic_overflow,
            )
        },
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun ExplorerToolbarPreview() {
    PreviewBackground {
        ExplorerToolbar(
            searchQuery = "",
            currentFilesystem = LocalFilesystem.LOCAL_UUID,
            filesystems = listOf(
                FilesystemModel(
                    uuid = LocalFilesystem.LOCAL_UUID,
                    title = "Local Storage",
                ),
                FilesystemModel(
                    uuid = RootFilesystem.ROOT_UUID,
                    title = "Root Storage",
                ),
            ),
            onFilesystemSelected = {},
            onQueryChanged = {},
            onClearQueryClicked = {},
            onBackClicked = {},
        )
    }
}