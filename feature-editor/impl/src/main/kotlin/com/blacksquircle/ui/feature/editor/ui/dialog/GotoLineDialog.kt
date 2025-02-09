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

package com.blacksquircle.ui.feature.editor.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.blacksquircle.ui.core.extensions.activityViewModels
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.databinding.DialogGotoLineBinding
import com.blacksquircle.ui.feature.editor.internal.EditorComponent
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import javax.inject.Inject
import javax.inject.Provider

internal class GotoLineDialog : DialogFragment() {

    @Inject
    lateinit var viewModelProvider: Provider<EditorViewModel>

    private val viewModel by activityViewModels<EditorViewModel> { viewModelProvider.get() }

    override fun onAttach(context: Context) {
        EditorComponent.buildOrGet(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogGotoLineBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_goto_line)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_go_to) { _, _ ->
                val line = binding.input.text.toString()
                viewModel.obtainEvent(EditorIntent.GotoLineNumber(line))
            }
            .create()
    }
}