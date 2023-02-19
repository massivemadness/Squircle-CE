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
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.databinding.DialogShortcutBinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class ShortcutDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ShortcutsViewModel>(R.id.shortcuts_graph)
    private val navArgs by navArgs<ShortcutDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            val keybinding = viewModel.shortcuts.value
                .find { it.shortcut.key == navArgs.key }
                ?: throw IllegalStateException()

            val titleRes = when (keybinding.shortcut) {
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
                Shortcut.PREV_WORD -> R.string.shortcut_prev_word
                Shortcut.NEXT_WORD -> R.string.shortcut_next_word
                Shortcut.LINE_START -> R.string.shortcut_start_of_line
                Shortcut.LINE_END -> R.string.shortcut_end_of_line
                Shortcut.UNDO -> R.string.shortcut_undo
                Shortcut.REDO -> R.string.shortcut_redo
                Shortcut.FIND -> R.string.shortcut_find
                Shortcut.REPLACE -> R.string.shortcut_replace
                Shortcut.GOTO_LINE -> R.string.shortcut_goto_line
                Shortcut.FORCE_SYNTAX -> R.string.shortcut_force_syntax
                Shortcut.INSERT_COLOR -> R.string.shortcut_insert_color
            }
            title(titleRes)
            message(R.string.shortcut_press_key)
            customView(R.layout.dialog_shortcut)

            val binding = DialogShortcutBinding.bind(getCustomView())
            binding.ctrl.isChecked = keybinding.isCtrl
            binding.shift.isChecked = keybinding.isShift
            binding.alt.isChecked = keybinding.isAlt

            negativeButton(android.R.string.cancel)
            positiveButton(UiR.string.common_save)
        }
    }
}