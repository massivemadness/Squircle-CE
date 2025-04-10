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
internal fun EditMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onCutClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onSelectLineClicked: () -> Unit = {},
    onDeleteLineClicked: () -> Unit = {},
    onDuplicateLineClicked: () -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        horizontalOffset = 180.dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(android.R.string.cut),
            iconResId = UiR.drawable.ic_cut,
            onClick = onCutClicked,
        )
        PopupMenuItem(
            title = stringResource(android.R.string.copy),
            iconResId = UiR.drawable.ic_copy,
            onClick = onCopyClicked,
        )
        PopupMenuItem(
            title = stringResource(android.R.string.paste),
            iconResId = UiR.drawable.ic_paste,
            onClick = onPasteClicked,
        )
        PopupMenuItem(
            title = stringResource(android.R.string.selectAll),
            iconResId = UiR.drawable.ic_select_all,
            onClick = onSelectAllClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.action_select_line),
            iconResId = UiR.drawable.ic_line_select,
            onClick = onSelectLineClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.action_delete_line),
            iconResId = UiR.drawable.ic_minus,
            onClick = onDeleteLineClicked,
        )
        PopupMenuItem(
            title = stringResource(R.string.action_duplicate_line),
            iconResId = UiR.drawable.ic_line_duplicate,
            onClick = onDuplicateLineClicked,
        )
    }
}