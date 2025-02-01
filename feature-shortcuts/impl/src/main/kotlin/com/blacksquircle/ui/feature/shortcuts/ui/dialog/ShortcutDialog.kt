/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.shortcuts.ui.dialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.extensions.keyCodeToChar
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.ui.composable.keybindingResource
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.ds.R as UiR

@AndroidEntryPoint
class ShortcutDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ShortcutsViewModel>(R.id.shortcuts_graph)
    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<ShortcutDialogArgs>()

    private var ctrlPressed = false
    private var shiftPressed = false
    private var altPressed = false

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    val initialKeybinding = viewModel.shortcuts
                        .find { it.shortcut.key == navArgs.key }
                        ?: throw IllegalStateException()

                    var keybindingState by remember {
                        mutableStateOf(initialKeybinding)
                    }

                    AlertDialog(
                        title = when (keybindingState.shortcut) {
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
                                    style = SquircleTheme.typography.text14Regular,
                                    color = SquircleTheme.colors.colorTextAndIconSecondary,
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                val textFieldState = remember { TextFieldState() }
                                val visualTransformation = keybindingResource(keybindingState)

                                TextField(
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

                                            keybindingState = keybindingState.copy(
                                                key = inputKey.uppercaseChar(),
                                                isShift = inputKey.isUpperCase(),
                                            )
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
                                        val controlKeyGlobal =
                                            !ctrlPressed && !shiftPressed && !altPressed
                                        if (controlKeyPressed || controlKeyGlobal) {
                                            return@onKeyEvent true
                                        }

                                        if (keyAction == KeyEvent.ACTION_UP) {
                                            keybindingState = keybindingState.copy(
                                                isCtrl = ctrlPressed,
                                                isShift = shiftPressed,
                                                isAlt = altPressed,
                                                key = keyChar,
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
                                        checked = keybindingState.isCtrl,
                                        onClick = {
                                            keybindingState = keybindingState.copy(
                                                isCtrl = !keybindingState.isCtrl
                                            )
                                        }
                                    )
                                    CheckBox(
                                        title = stringResource(UiR.string.common_shift),
                                        checked = keybindingState.isShift,
                                        onClick = {
                                            keybindingState = keybindingState.copy(
                                                isShift = !keybindingState.isShift
                                            )
                                        }
                                    )
                                    CheckBox(
                                        title = stringResource(UiR.string.common_alt),
                                        checked = keybindingState.isAlt,
                                        onClick = {
                                            keybindingState = keybindingState.copy(
                                                isAlt = !keybindingState.isAlt
                                            )
                                        }
                                    )
                                }
                            }
                        },
                        confirmButton = stringResource(UiR.string.common_save),
                        onConfirmClicked = {
                            navController.popBackStack()
                            viewModel.onKeyAssigned(keybindingState)
                        },
                        dismissButton = stringResource(android.R.string.cancel),
                        onDismissClicked = {
                            navController.popBackStack()
                        },
                        onDismiss = {
                            navController.popBackStack()
                        },
                    )
                }
            }
        }
    }
}