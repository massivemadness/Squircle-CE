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

package com.blacksquircle.ui.feature.editor.ui.fragment.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.ds.toolbar.ToolbarSizeDefaults

@Composable
internal fun EditorToolbar(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {},
) {
    Toolbar(
        navigationIcon = R.drawable.ic_menu,
        onNavigationClicked = onMenuClicked,
        navigationActions = {
            IconButton(
                iconResId = R.drawable.ic_folder,
                onClick = {},
            )
            IconButton(
                iconResId = R.drawable.ic_pencil,
                onClick = {},
            )
            IconButton(
                iconResId = R.drawable.ic_undo,
                onClick = {},
            )
            IconButton(
                iconResId = R.drawable.ic_redo,
                onClick = {},
            )
            IconButton(
                iconResId = R.drawable.ic_dots_vertical,
                onClick = {},
            )
        },
        toolbarSize = ToolbarSizeDefaults.S,
        modifier = modifier,
    )
}