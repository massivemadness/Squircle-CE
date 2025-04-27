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

package com.blacksquircle.ui.feature.explorer.ui.explorer.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun CreateMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onNewFileClicked: () -> Unit = {},
    onNewFolderClicked: () -> Unit = {},
    onCloneRepoClicked: () -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-36).dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(R.string.action_new_file),
            iconResId = UiR.drawable.ic_file,
            onClick = onNewFileClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.action_new_folder),
            iconResId = UiR.drawable.ic_folder,
            onClick = onNewFolderClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.action_clone),
            iconResId = UiR.drawable.ic_git,
            onClick = onCloneRepoClicked,
        )
    }
}