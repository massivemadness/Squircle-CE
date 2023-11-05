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

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.extensions.keyCodeToChar
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.databinding.DialogShortcutBinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.ui.mvi.ShortcutIntent
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.uikit.R as UiR

/**
 * All credits goes to Quoda
 */
@AndroidEntryPoint
class ShortcutDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ShortcutsViewModel>(R.id.shortcuts_graph)
    private val navArgs by navArgs<ShortcutDialogArgs>()
    private val navController by lazy { findNavController() }

    private lateinit var binding: DialogShortcutBinding

    private var ctrlPressed = false
    private var shiftPressed = false
    private var altPressed = false

    private var newCtrl = false
    private var newShift = false
    private var newAlt = false

    private var updatingText = false
    private var newEnabled = true
    private var newKey = ' '

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogShortcutBinding.inflate(layoutInflater)
        val keybinding = viewModel.shortcuts.value
            .find { it.shortcut.key == navArgs.key }
            ?: throw IllegalStateException()
        setListeners(keybinding)

        return AlertDialog.Builder(requireContext())
            .setTitle(
                when (keybinding.shortcut) {
                    Shortcut.NEW -> R.string.shortcut_new_file
                    Shortcut.OPEN -> R.string.shortcut_open_file
                    Shortcut.SAVE -> R.string.shortcut_save_file
                    Shortcut.SAVE_AS -> R.string.shortcut_save_as
                    Shortcut.CLOSE -> R.string.shortcut_close_file
                    Shortcut.CUT -> android.R.string.cut
                    Shortcut.COPY -> android.R.string.copy
                    Shortcut.PASTE -> android.R.string.paste
                    Shortcut.SELECT_ALL -> android.R.string.selectAll
                    Shortcut.SELECT_LINE -> R.string.shortcut_select_line
                    Shortcut.DELETE_LINE -> R.string.shortcut_delete_line
                    Shortcut.DUPLICATE_LINE -> R.string.shortcut_duplicate_line
                    Shortcut.TOGGLE_CASE -> R.string.shortcut_toggle_case
                    Shortcut.PREV_WORD -> R.string.shortcut_prev_word
                    Shortcut.NEXT_WORD -> R.string.shortcut_next_word
                    Shortcut.START_OF_LINE -> R.string.shortcut_start_of_line
                    Shortcut.END_OF_LINE -> R.string.shortcut_end_of_line
                    Shortcut.UNDO -> R.string.shortcut_undo
                    Shortcut.REDO -> R.string.shortcut_redo
                    Shortcut.FIND -> R.string.shortcut_find
                    Shortcut.REPLACE -> R.string.shortcut_replace
                    Shortcut.GOTO_LINE -> R.string.shortcut_goto_line
                    Shortcut.FORCE_SYNTAX -> R.string.shortcut_force_syntax
                    Shortcut.INSERT_COLOR -> R.string.shortcut_insert_color
                }
            )
            .setMessage(R.string.shortcut_press_key)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(UiR.string.common_save) { _, _ ->
                navController.popBackStack()
                val newKeybinding = keybinding.copy(
                    isCtrl = newCtrl, isShift = newShift, isAlt = newAlt, key = newKey,
                )
                viewModel.obtainEvent(ShortcutIntent.Reassign(newKeybinding))
            }
            .create()
    }

    private fun setListeners(keybinding: Keybinding) {
        binding.ctrl.setOnCheckedChangeListener { _, isChecked ->
            newCtrl = isChecked
            ctrlPressed = isChecked
            format(newEnabled, newCtrl, newShift, newAlt, newKey)
        }
        binding.shift.setOnCheckedChangeListener { _, isChecked ->
            newShift = isChecked
            shiftPressed = isChecked
            format(newEnabled, newCtrl, newShift, newAlt, newKey)
        }
        binding.alt.setOnCheckedChangeListener { _, isChecked ->
            newAlt = isChecked
            altPressed = isChecked
            format(newEnabled, newCtrl, newShift, newAlt, newKey)
        }

        // Virtual keyboard
        binding.inputShortcut.doOnTextChanged { text, start, _, count ->
            if (updatingText) return@doOnTextChanged
            if (binding.ctrl.isChecked) ctrlPressed = true
            if (binding.shift.isChecked) shiftPressed = true
            if (binding.alt.isChecked) altPressed = true

            if (!ctrlPressed && !shiftPressed && !altPressed) {
                format(false, false, false, false, ' ')
                ctrlPressed = false
                shiftPressed = false
                altPressed = false
                newEnabled = true
            } else if (count == 1) {
                val char = text?.get(start) ?: return@doOnTextChanged
                if (char.isUpperCase()) {
                    shiftPressed = true
                }
                format(true, ctrlPressed, shiftPressed, altPressed, char)
                newCtrl = ctrlPressed
                newShift = shiftPressed
                newAlt = altPressed
                if (char.code != 0) {
                    newKey = char.uppercaseChar()
                }
                newEnabled = true
                ctrlPressed = false
                shiftPressed = false
                altPressed = false
            } else {
                format(false, false, false, false, ' ')
                newEnabled = false
                ctrlPressed = false
                shiftPressed = false
                altPressed = false
            }
        }

        // Hardware keyboard
        binding.inputShortcut.setOnKeyListener { _, keyCode, event ->
            if (updatingText) return@setOnKeyListener true

            val ctrlKey = keyCode == KeyEvent.KEYCODE_CTRL_LEFT ||
                keyCode == KeyEvent.KEYCODE_CTRL_RIGHT
            val shiftKey = keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ||
                keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
            val altKey = keyCode == KeyEvent.KEYCODE_ALT_LEFT ||
                keyCode == KeyEvent.KEYCODE_ALT_RIGHT

            if (ctrlKey) {
                if (event.action != KeyEvent.ACTION_UP) {
                    ctrlPressed = !ctrlPressed
                }
                binding.ctrl.isChecked = ctrlPressed
            }
            if (shiftKey) {
                if (event.action != KeyEvent.ACTION_UP) {
                    shiftPressed = !shiftPressed
                }
                binding.shift.isChecked = shiftPressed
            }
            if (altKey) {
                if (event.action != KeyEvent.ACTION_UP) {
                    altPressed = !altPressed
                }
                binding.alt.isChecked = altPressed
            }

            val controlKeyPressed = ctrlKey || shiftKey || altKey
            val controlKeyGlobal = !ctrlPressed && !shiftPressed && !altPressed
            if (controlKeyPressed || controlKeyGlobal) {
                return@setOnKeyListener true
            }

            val char = keyCode.keyCodeToChar()
            format(true, ctrlPressed, shiftPressed, altPressed, char)

            newCtrl = ctrlPressed
            newShift = shiftPressed
            newAlt = altPressed
            newKey = char
            newEnabled = true
            ctrlPressed = false
            shiftPressed = false
            altPressed = false
            false
        }

        ctrlPressed = keybinding.isCtrl
        newCtrl = ctrlPressed

        shiftPressed = keybinding.isShift
        newShift = shiftPressed

        altPressed = keybinding.isAlt
        newAlt = altPressed

        newKey = keybinding.key
        format(newEnabled, newCtrl, newShift, newAlt, newKey)
    }

    private fun format(
        enabled: Boolean,
        ctrl: Boolean,
        shift: Boolean,
        alt: Boolean,
        char: Char,
    ) {
        if (char.code != 0) {
            if (!enabled || ctrl || shift || alt) {
                newEnabled = enabled

                val display = StringBuilder().apply {
                    if (!enabled) {
                        append(getString(R.string.shortcut_none))
                    } else if (ctrl || alt) {
                        if (ctrl) append(getString(UiR.string.common_ctrl) + " + ")
                        if (shift) append(getString(UiR.string.common_shift) + " + ")
                        if (alt) append(getString(UiR.string.common_alt) + " + ")
                        when (char) {
                            ' ' -> append(getString(UiR.string.common_space))
                            '\t' -> append(getString(UiR.string.common_tab))
                            else -> append(char.uppercaseChar())
                        }
                    } else {
                        append(getString(R.string.shortcut_none))
                    }
                }

                updatingText = true
                binding.inputShortcut.setText(display)
                updatingText = false

                binding.ctrl.isChecked = ctrl
                binding.shift.isChecked = shift
                binding.alt.isChecked = alt
                return
            }
            newEnabled = false
            format(false, ctrl, shift, alt, char)
        }
    }
}