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

package com.blacksquircle.ui.feature.shortcuts.ui.keybinding

import android.view.KeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.forEachChange
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField2
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.api.extensions.keyCodeToChar
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.api.navigation.EditKeybindingDialog
import com.blacksquircle.ui.feature.shortcuts.data.mapper.ShortcutMapper
import com.blacksquircle.ui.feature.shortcuts.internal.ShortcutsComponent
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.compose.keybindingResource
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.KEY_SAVE
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun KeybindingScreen(
    navArgs: EditKeybindingDialog,
    navController: NavController,
    viewModel: KeybindingViewModel = daggerViewModel { context ->
        val component = ShortcutsComponent.buildOrGet(context)
        val keybinding = Keybinding(
            shortcut = navArgs.shortcut,
            isCtrl = navArgs.isCtrl,
            isShift = navArgs.isShift,
            isAlt = navArgs.isAlt,
            key = Char(navArgs.keyCode),
        )
        KeybindingViewModel.ParameterizedFactory(keybinding).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    KeybindingScreen(
        viewState = viewState,
        onKeyPressed = viewModel::onKeyPressed,
        onMultiKeyPressed = viewModel::onMultiKeyPressed,
        onCtrlClicked = viewModel::onCtrlClicked,
        onShiftClicked = viewModel::onShiftClicked,
        onAltClicked = viewModel::onAltClicked,
        onSaveClicked = viewModel::onSaveClicked,
        onCancelClicked = viewModel::onCancelClicked,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
                is KeybindingViewEvent.SendSaveResult -> {
                    sendNavigationResult(
                        key = KEY_SAVE,
                        result = ShortcutMapper.toBundle(event.keybinding)
                    )
                    navController.popBackStack()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeybindingScreen(
    viewState: KeybindingViewState,
    onKeyPressed: (Char) -> Unit = {},
    onMultiKeyPressed: (Boolean, Boolean, Boolean, Char) -> Unit = { _, _, _, _ -> },
    onCtrlClicked: () -> Unit = {},
    onShiftClicked: () -> Unit = {},
    onAltClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
    onCancelClicked: () -> Unit = {},
) {
    AlertDialog(
        title = when (viewState.shortcut) {
            Shortcut.NEW -> stringResource(R.string.shortcut_new_file)
            Shortcut.OPEN -> stringResource(R.string.shortcut_open_file)
            Shortcut.SAVE -> stringResource(R.string.shortcut_save_file)
            Shortcut.SAVE_AS -> stringResource(R.string.shortcut_save_as)
            Shortcut.CLOSE -> stringResource(R.string.shortcut_close_file)
            Shortcut.CUT -> stringResource(android.R.string.cut)
            Shortcut.COPY -> stringResource(android.R.string.copy)
            Shortcut.PASTE -> stringResource(android.R.string.paste)
            Shortcut.SELECT_ALL -> stringResource(android.R.string.selectAll)
            Shortcut.SELECT_LINE -> stringResource(R.string.shortcut_select_line)
            Shortcut.DELETE_LINE -> stringResource(R.string.shortcut_delete_line)
            Shortcut.DUPLICATE_LINE -> stringResource(R.string.shortcut_duplicate_line)
            Shortcut.TOGGLE_CASE -> stringResource(R.string.shortcut_toggle_case)
            Shortcut.PREV_WORD -> stringResource(R.string.shortcut_prev_word)
            Shortcut.NEXT_WORD -> stringResource(R.string.shortcut_next_word)
            Shortcut.START_OF_LINE -> stringResource(R.string.shortcut_start_of_line)
            Shortcut.END_OF_LINE -> stringResource(R.string.shortcut_end_of_line)
            Shortcut.UNDO -> stringResource(R.string.shortcut_undo)
            Shortcut.REDO -> stringResource(R.string.shortcut_redo)
            Shortcut.FIND -> stringResource(R.string.shortcut_find)
            Shortcut.REPLACE -> stringResource(R.string.shortcut_replace)
            Shortcut.GOTO_LINE -> stringResource(R.string.shortcut_goto_line)
            Shortcut.FORCE_SYNTAX -> stringResource(R.string.shortcut_force_syntax)
            Shortcut.INSERT_COLOR -> stringResource(R.string.shortcut_insert_color)
        },
        content = {
            Column {
                Text(
                    text = stringResource(R.string.shortcut_press_key),
                    style = SquircleTheme.typography.text16Regular,
                    color = SquircleTheme.colors.colorTextAndIconSecondary,
                )
                Spacer(modifier = Modifier.height(12.dp))

                val textFieldState = remember { TextFieldState() }
                val visualTransformation = keybindingResource(
                    ctrl = viewState.isCtrl,
                    shift = viewState.isShift,
                    alt = viewState.isAlt,
                    key = viewState.key,
                )

                var ctrlPressed by remember { mutableStateOf(false) }
                var shiftPressed by remember { mutableStateOf(false) }
                var altPressed by remember { mutableStateOf(false) }

                TextField2(
                    state = textFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    inputTransformation = {
                        changes.forEachChange { range, originalRange ->
                            val isDeletion = originalRange.length > range.length
                            val inputBuffer = asCharSequence()
                            val inputKey = if (isDeletion) {
                                '\u232B' // ⌫
                            } else {
                                inputBuffer.last()
                            }

                            replace(0, length, inputKey.toString())

                            onKeyPressed(inputKey)
                        }
                    },
                    outputTransformation = {
                        replace(0, length, visualTransformation)
                    },
                    modifier = Modifier.onKeyEvent { keyEvent ->
                        val keyCode = keyEvent.nativeKeyEvent.keyCode
                        val keyAction = keyEvent.nativeKeyEvent.action
                        val keyChar = keyCode.keyCodeToChar()

                        val ctrlKey = keyCode == KeyEvent.KEYCODE_CTRL_LEFT ||
                            keyCode == KeyEvent.KEYCODE_CTRL_RIGHT
                        val shiftKey = keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ||
                            keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
                        val altKey = keyCode == KeyEvent.KEYCODE_ALT_LEFT ||
                            keyCode == KeyEvent.KEYCODE_ALT_RIGHT

                        if (ctrlKey && keyAction != KeyEvent.ACTION_UP) {
                            ctrlPressed = !ctrlPressed
                        }
                        if (shiftKey && keyAction != KeyEvent.ACTION_UP) {
                            shiftPressed = !shiftPressed
                        }
                        if (altKey && keyAction != KeyEvent.ACTION_UP) {
                            altPressed = !altPressed
                        }

                        val controlKeyPressed = ctrlKey || shiftKey || altKey
                        val controlKeyGlobal = !ctrlPressed && !shiftPressed && !altPressed
                        if (controlKeyPressed || controlKeyGlobal) {
                            return@onKeyEvent true
                        }

                        if (keyAction == KeyEvent.ACTION_UP) {
                            onMultiKeyPressed(
                                ctrlPressed,
                                shiftPressed,
                                altPressed,
                                keyChar
                            )
                            ctrlPressed = false
                            shiftPressed = false
                            altPressed = false
                        }
                        false
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CheckBox(
                        title = stringResource(UiR.string.common_ctrl),
                        checked = viewState.isCtrl,
                        onClick = onCtrlClicked,
                    )
                    CheckBox(
                        title = stringResource(UiR.string.common_shift),
                        checked = viewState.isShift,
                        onClick = onShiftClicked,
                    )
                    CheckBox(
                        title = stringResource(UiR.string.common_alt),
                        checked = viewState.isAlt,
                        onClick = onAltClicked,
                    )
                }
            }
        },
        confirmButton = stringResource(UiR.string.common_save),
        onConfirmClicked = onSaveClicked,
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun KeybindingScreenPreview() {
    PreviewBackground {
        KeybindingScreen(
            viewState = KeybindingViewState(
                shortcut = Shortcut.CUT,
                isCtrl = true,
                isShift = false,
                isAlt = false,
                key = 'X',
            ),
        )
    }
}