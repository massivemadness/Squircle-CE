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

package com.blacksquircle.ui.feature.explorer.ui.explorer.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode

@Composable
internal fun SelectionMenu(
    selection: List<FileNode>,
    workspaceType: WorkspaceType,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCutClicked: () -> Unit = {},
    onOpenWithClicked: () -> Unit = {},
    onOpenTerminalClicked: () -> Unit = {},
    onRenameClicked: () -> Unit = {},
    onPropertiesClicked: () -> Unit = {},
    onCopyPathClicked: () -> Unit = {},
    onCompressClicked: () -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(android.R.string.cut),
            onClick = onCutClicked,
            enabled = workspaceType.isLocal(),
        )
        if (selection.size == 1) {
            if (workspaceType != WorkspaceType.SERVER) {
                PopupMenuItem(
                    title = stringResource(R.string.explorer_menu_selection_open_with),
                    onClick = onOpenWithClicked,
                )
                if (selection.first().isDirectory) {
                    PopupMenuItem(
                        title = stringResource(R.string.explorer_menu_selection_open_terminal),
                        onClick = onOpenTerminalClicked,
                    )
                }
            }
            PopupMenuItem(
                title = stringResource(R.string.explorer_menu_selection_rename),
                onClick = onRenameClicked,
            )
            PopupMenuItem(
                title = stringResource(R.string.explorer_menu_selection_properties),
                onClick = onPropertiesClicked,
            )
            PopupMenuItem(
                title = stringResource(R.string.explorer_menu_selection_copy_path),
                onClick = onCopyPathClicked,
            )
        }
        PopupMenuItem(
            title = stringResource(R.string.explorer_menu_selection_compress),
            onClick = onCompressClicked,
            enabled = workspaceType.isLocal(),
        )
    }
}