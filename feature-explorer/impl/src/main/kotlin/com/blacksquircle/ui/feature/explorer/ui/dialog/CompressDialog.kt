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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.extensions.activityViewModels
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.DialogCompressBinding
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import javax.inject.Inject
import javax.inject.Provider
import com.blacksquircle.ui.ds.R as UiR

internal class CompressDialog : DialogFragment() {

    @Inject
    lateinit var viewModelProvider: Provider<ExplorerViewModel>

    private val viewModel by activityViewModels<ExplorerViewModel> { viewModelProvider.get() }
    private val navController by lazy { findNavController() }

    override fun onAttach(context: Context) {
        ExplorerComponent.buildOrGet(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogCompressBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_archive_name)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.action_compress) { _, _ ->
                val fileName = binding.input.text?.ifEmpty { getString(UiR.string.common_untitled) }
                navController.popBackStack()
                viewModel.obtainEvent(ExplorerIntent.CompressFile(fileName.toString()))
            }
            .create()
    }
}