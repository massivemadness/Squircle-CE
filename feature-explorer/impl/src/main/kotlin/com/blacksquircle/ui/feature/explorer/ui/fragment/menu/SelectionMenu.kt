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

package com.blacksquircle.ui.feature.explorer.ui.fragment.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.feature.explorer.R

@Composable
internal fun SelectionMenu(
    count: Int,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCutClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onOpenWithClicked: () -> Unit = {},
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
        )
        PopupMenuItem(
            title = stringResource(android.R.string.selectAll),
            onClick = onSelectAllClicked,
        )
        if (count == 1) {
            PopupMenuItem(
                title = stringResource(R.string.action_open_with),
                onClick = onOpenWithClicked,
            )
            PopupMenuItem(
                title = stringResource(R.string.action_rename),
                onClick = onRenameClicked,
            )
            PopupMenuItem(
                title = stringResource(R.string.action_properties),
                onClick = onPropertiesClicked,
            )
            PopupMenuItem(
                title = stringResource(R.string.action_copy_path),
                onClick = onCopyPathClicked,
            )
        }
        PopupMenuItem(
            title = stringResource(R.string.action_compress),
            onClick = onCompressClicked,
        )
    }
}