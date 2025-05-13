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
internal fun GitMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onFetchClicked: () -> Unit = {},
    onPullClicked: () -> Unit = {},
    onCommitClicked: () -> Unit = {},
    onPushClicked: () -> Unit = {},
    onCheckoutClicked: () -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_git_fetch),
            iconResId = UiR.drawable.ic_autorenew,
            onClick = onFetchClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_git_pull),
            iconResId = UiR.drawable.ic_tray_arrow_down,
            onClick = onPullClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_git_commit),
            iconResId = UiR.drawable.ic_source_commit,
            onClick = onCommitClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_git_push),
            iconResId = UiR.drawable.ic_tray_arrow_up,
            onClick = onPushClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.editor_menu_git_checkout),
            iconResId = UiR.drawable.ic_source_branch,
            onClick = onCheckoutClicked,
        )
    }
}