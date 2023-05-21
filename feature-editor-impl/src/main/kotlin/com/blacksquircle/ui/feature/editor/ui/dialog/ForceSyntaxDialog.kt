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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForceSyntaxDialog : DialogFragment() {

    private val viewModel by activityViewModels<EditorViewModel>()
    private val navArgs by navArgs<ForceSyntaxDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val langNames = resources.getStringArray(R.array.language_title)
        val langEntries = resources.getStringArray(R.array.language_name)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_force_syntax)
            .setSingleChoiceItems(langNames, langEntries.indexOf(navArgs.languageName)) { _, which ->
                val intent = EditorIntent.ForceSyntaxHighlighting(langEntries[which])
                viewModel.obtainEvent(intent)
                requireContext().showToast(text = langNames[which])
                dismiss()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }
}