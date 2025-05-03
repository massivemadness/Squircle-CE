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
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.popupmenu.PopupMenu
import com.blacksquircle.ui.ds.popupmenu.PopupMenuItem
import com.blacksquircle.ui.ds.radio.Radio
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.SortMode

@Composable
internal fun SortingMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showHidden: Boolean = true,
    compactPackages: Boolean = true,
    sortMode: SortMode = SortMode.SORT_BY_NAME,
    onShowHiddenClicked: () -> Unit = {},
    onCompactPackagesClicked: () -> Unit = {},
    onSortModeSelected: (SortMode) -> Unit = {},
) {
    PopupMenu(
        expanded = expanded,
        onDismiss = onDismiss,
        verticalOffset = (-56).dp,
        modifier = modifier,
    ) {
        PopupMenuItem(
            title = stringResource(R.string.action_show_hidden_files),
            onClick = onShowHiddenClicked,
            trailing = {
                CheckBox(
                    checked = showHidden,
                    onClick = onShowHiddenClicked,
                )
            }
        )
        PopupMenuItem(
            title = stringResource(R.string.action_compact_packages),
            onClick = onCompactPackagesClicked,
            trailing = {
                CheckBox(
                    checked = compactPackages,
                    onClick = onCompactPackagesClicked,
                )
            }
        )
        PopupMenuItem(
            title = stringResource(R.string.sort_by_name),
            onClick = { onSortModeSelected(SortMode.SORT_BY_NAME) },
            trailing = {
                Radio(
                    checked = sortMode == SortMode.SORT_BY_NAME,
                    onClick = { onSortModeSelected(SortMode.SORT_BY_NAME) },
                )
            }
        )
        PopupMenuItem(
            title = stringResource(R.string.sort_by_size),
            onClick = { onSortModeSelected(SortMode.SORT_BY_SIZE) },
            trailing = {
                Radio(
                    checked = sortMode == SortMode.SORT_BY_SIZE,
                    onClick = { onSortModeSelected(SortMode.SORT_BY_SIZE) },
                )
            }
        )
        PopupMenuItem(
            title = stringResource(R.string.sort_by_date),
            onClick = { onSortModeSelected(SortMode.SORT_BY_DATE) },
            trailing = {
                Radio(
                    checked = sortMode == SortMode.SORT_BY_DATE,
                    onClick = { onSortModeSelected(SortMode.SORT_BY_DATE) },
                )
            }
        )
    }
}