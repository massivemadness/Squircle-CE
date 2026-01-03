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

package com.blacksquircle.ui.feature.editor.ui.editor.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun OtherMenu(
    showGit: Boolean,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onFindClicked: () -> Unit = {},
    onToolsClicked: () -> Unit = {},
    onGitClicked: () -> Unit = {},
    onTerminalClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        horizontalOffset = 200.dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_find),
            iconResId = UiR.drawable.ic_file_find,
            onClick = onFindClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_tools),
            iconResId = UiR.drawable.ic_wrench,
            onClick = onToolsClicked,
            submenu = true,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_git),
            iconResId = UiR.drawable.ic_git,
            onClick = onGitClicked,
            enabled = showGit,
            submenu = true,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_terminal),
            iconResId = UiR.drawable.ic_console,
            onClick = onTerminalClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_settings),
            iconResId = UiR.drawable.ic_settings,
            onClick = onSettingsClicked,
        )
    }
}