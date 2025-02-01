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

package com.blacksquircle.ui.feature.shortcuts.ui.composable

import android.view.KeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.forEachChange
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.core.extensions.keyCodeToChar
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.textfield.DsTextField
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import com.blacksquircle.ui.ds.R as UiR

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeybindingPreference(
    keybinding: Keybinding,
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    message: String? = null,
    confirmButton: String? = null,
    dismissButton: String? = null,
    onKeyAssigned: (Keybinding) -> Unit = { _ -> },
) {
    var dialogShown by remember { mutableStateOf(false) }
    var keybindingState by remember { mutableStateOf(keybinding) }

    var ctrlPressed by remember { mutableStateOf(false) }
    var shiftPressed by remember { mutableStateOf(false) }
    var altPressed by remember { mutableStateOf(false) }

    Preference(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = { dialogShown = true },
    )

    if (dialogShown) {
        AlertDialog(
            title = title,
            content = {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    if (message != null) {
                        Text(
                            text = message,
                            style = SquircleTheme.typography.text14Regular,
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    val textFieldState = remember { TextFieldState() }
                    val visualTransformation = keybindingResource(keybindingState)

                    DsTextField(
                        state = textFieldState,
                        lineLimits = TextFieldLineLimits.SingleLine,
                        inputTransformation = {
                            changes.forEachChange { range: TextRange, originalRange: TextRange ->
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
                            val controlKeyGlobal = !ctrlPressed && !shiftPressed && !altPressed
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
            confirmButton = confirmButton,
            onConfirmClicked = {
                dialogShown = false
                onKeyAssigned(keybindingState)
            },
            dismissButton = dismissButton,
            onDismissClicked = {
                dialogShown = false
            },
            onDismiss = { dialogShown = false },
        )
    }
}

@Composable
@ReadOnlyComposable
internal fun keybindingResource(keybinding: Keybinding): String {
    val noneSet = stringResource(R.string.shortcut_none)
    val ctrl = stringResource(UiR.string.common_ctrl)
    val shift = stringResource(UiR.string.common_shift)
    val alt = stringResource(UiR.string.common_alt)
    val space = stringResource(UiR.string.common_space)
    val tab = stringResource(UiR.string.common_tab)
    return StringBuilder().apply {
        val isCtrlOrAltPressed = keybinding.isCtrl || keybinding.isAlt
        if (!isCtrlOrAltPressed || keybinding.key == '\u0000') {
            append(noneSet)
        } else {
            if (keybinding.isCtrl) append("$ctrl + ")
            if (keybinding.isShift) append("$shift + ")
            if (keybinding.isAlt) append("$alt + ")
            when (keybinding.key) {
                ' ' -> append(space)
                '\t' -> append(tab)
                else -> append(keybinding.key.uppercaseChar())
            }
        }
    }.toString()
}