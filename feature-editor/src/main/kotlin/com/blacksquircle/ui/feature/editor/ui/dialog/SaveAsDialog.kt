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

package com.blacksquircle.ui.feature.editor.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.core.ui.extensions.showToast
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.databinding.DialogSaveAsBinding
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveAsDialog : DialogFragment() {

    private val viewModel by activityViewModels<EditorViewModel>()
    private val navArgs by navArgs<SaveAsDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            customView(R.layout.dialog_save_as, scrollable = true)
            val binding = DialogSaveAsBinding.bind(getCustomView())

            title(R.string.dialog_title_save_as)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_save) {
                val filePath = binding.input.text?.toString()?.trim()
                if (!filePath.isNullOrBlank()) {
                    viewModel.obtainEvent(EditorIntent.SaveFileAs(filePath))
                } else {
                    context.showToast(R.string.message_invalid_file_path)
                }
            }
            binding.input.setText(navArgs.filePath)
        }
    }
}