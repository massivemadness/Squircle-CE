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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.editor.menu.EditMenu
import com.blacksquircle.ui.feature.editor.ui.editor.menu.FileMenu
import com.blacksquircle.ui.feature.editor.ui.editor.menu.OtherMenu
import com.blacksquircle.ui.feature.editor.ui.editor.menu.ToolsMenu
import com.blacksquircle.ui.feature.editor.ui.editor.model.MenuType
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun EditorToolbar(
    canUndo: Boolean,
    canRedo: Boolean,
    modifier: Modifier = Modifier,
    onDrawerClicked: () -> Unit = {},
    onNewFileClicked: () -> Unit = {},
    onOpenFileClicked: () -> Unit = {},
    onSaveFileClicked: () -> Unit = {},
    onSaveFileAsClicked: () -> Unit = {},
    onRefreshFileClicked: () -> Unit = {},
    onCloseFileClicked: () -> Unit = {},
    onCutClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onSelectLineClicked: () -> Unit = {},
    onDeleteLineClicked: () -> Unit = {},
    onDuplicateLineClicked: () -> Unit = {},
    onForceSyntaxClicked: () -> Unit = {},
    onInsertColorClicked: () -> Unit = {},
    onGitClicked: () -> Unit = {},
    onFindClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onRedoClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var menuType by rememberSaveable {
        mutableStateOf<MenuType?>(null)
    }

    Toolbar(
        navigationIcon = UiR.drawable.ic_menu,
        onNavigationClicked = onDrawerClicked,
        navigationActions = {
            IconButton(
                iconResId = UiR.drawable.ic_folder,
                onClick = { menuType = MenuType.FILE },
                contentDescription = stringResource(R.string.action_file),
                anchor = {
                    FileMenu(
                        expanded = menuType == MenuType.FILE,
                        onDismiss = { menuType = null },
                        onNewFileClicked = { menuType = null; onNewFileClicked() },
                        onOpenFileClicked = { menuType = null; onOpenFileClicked() },
                        onSaveFileClicked = { menuType = null; onSaveFileClicked() },
                        onSaveFileAsClicked = { menuType = null; onSaveFileAsClicked() },
                        onRefreshFileClicked = { menuType = null; onRefreshFileClicked() },
                        onCloseFileClicked = { menuType = null; onCloseFileClicked() },
                    )
                }
            )
            IconButton(
                iconResId = UiR.drawable.ic_pencil,
                onClick = { menuType = MenuType.EDIT },
                contentDescription = stringResource(R.string.action_edit),
                anchor = {
                    EditMenu(
                        expanded = menuType == MenuType.EDIT,
                        onDismiss = { menuType = null },
                        onCutClicked = { menuType = null; onCutClicked() },
                        onCopyClicked = { menuType = null; onCopyClicked() },
                        onPasteClicked = { menuType = null; onPasteClicked() },
                        onSelectAllClicked = { menuType = null; onSelectAllClicked() },
                        onSelectLineClicked = { menuType = null; onSelectLineClicked() },
                        onDeleteLineClicked = { menuType = null; onDeleteLineClicked() },
                        onDuplicateLineClicked = { menuType = null; onDuplicateLineClicked() },
                    )
                }
            )
            IconButton(
                iconResId = UiR.drawable.ic_undo,
                onClick = onUndoClicked,
                enabled = canUndo,
                debounce = false,
                contentDescription = stringResource(R.string.action_undo)
            )
            IconButton(
                iconResId = UiR.drawable.ic_redo,
                onClick = onRedoClicked,
                enabled = canRedo,
                debounce = false,
                contentDescription = stringResource(R.string.action_redo)
            )
            IconButton(
                iconResId = UiR.drawable.ic_dots_vertical,
                onClick = { menuType = MenuType.OTHER },
                contentDescription = stringResource(UiR.string.common_menu),
                anchor = {
                    OtherMenu(
                        expanded = menuType == MenuType.OTHER,
                        onDismiss = { menuType = null },
                        onFindClicked = { menuType = null; onFindClicked() },
                        onToolsClicked = { menuType = MenuType.TOOLS },
                        onGitClicked = {
                            focusManager.clearFocus(force = true)
                            menuType = null
                            onGitClicked()
                        },
                        onSettingsClicked = {
                            focusManager.clearFocus(force = true)
                            menuType = null
                            onSettingsClicked()
                        },
                    )
                    ToolsMenu(
                        expanded = menuType == MenuType.TOOLS,
                        onDismiss = { menuType = null },
                        onForceSyntaxClicked = { menuType = null; onForceSyntaxClicked() },
                        onInsertColorClicked = { menuType = null; onInsertColorClicked() },
                    )
                }
            )
        },
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun EditorToolbarPreview() {
    PreviewBackground {
        EditorToolbar(
            canUndo = true,
            canRedo = false,
        )
    }
}