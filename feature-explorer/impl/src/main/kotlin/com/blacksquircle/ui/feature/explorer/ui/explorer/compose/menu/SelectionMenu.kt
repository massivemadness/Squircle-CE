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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.feature.explorer.ui.explorer.store.ExplorerAction

@Composable
internal fun SelectionMenu(
    selection: List<FileNode>,
    workspaceType: WorkspaceType,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dispatch: (ExplorerAction.UiAction) -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(android.R.string.cut),
            onClick = { dispatch(ExplorerAction.UiAction.OnCutClicked) },
            enabled = workspaceType.isLocal(),
        )
        if (selection.size == 1) {
            if (workspaceType != WorkspaceType.SERVER) {
                PopupMenuItem(
                    title = stringResource(R.string.explorer_menu_selection_open_with),
                    onClick = { dispatch(ExplorerAction.UiAction.OnOpenWithClicked) },
                )
                if (selection.first().isDirectory) {
                    PopupMenuItem(
                        title = stringResource(R.string.explorer_menu_selection_open_terminal),
                        onClick = { dispatch(ExplorerAction.UiAction.OnOpenTerminalClicked) },
                    )
                }
            }
            PopupMenuItem(
                title = stringResource(R.string.explorer_menu_selection_rename),
                onClick = { dispatch(ExplorerAction.UiAction.OnRenameClicked) },
            )
            PopupMenuItem(
                title = stringResource(R.string.explorer_menu_selection_properties),
                onClick = { dispatch(ExplorerAction.UiAction.OnPropertiesClicked) },
            )
            PopupMenuItem(
                title = stringResource(R.string.explorer_menu_selection_copy_path),
                onClick = { dispatch(ExplorerAction.UiAction.OnCopyPathClicked) },
            )
        }
        PopupMenuItem(
            title = stringResource(R.string.explorer_menu_selection_compress),
            onClick = { dispatch(ExplorerAction.UiAction.OnCompressClicked) },
            enabled = workspaceType.isLocal(),
        )
    }
}