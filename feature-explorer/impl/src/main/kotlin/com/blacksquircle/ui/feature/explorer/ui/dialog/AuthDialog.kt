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
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.extensions.activityViewModels
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.DialogAuthBinding
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import javax.inject.Inject
import javax.inject.Provider
import com.blacksquircle.ui.ds.R as UiR

internal class AuthDialog : DialogFragment() {

    @Inject
    lateinit var viewModelProvider: Provider<ExplorerViewModel>

    private val viewModel by activityViewModels<ExplorerViewModel> { viewModelProvider.get() }
    private val navArgs by navArgs<AuthDialogArgs>()

    override fun onAttach(context: Context) {
        ExplorerComponent.buildOrGet(context).inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAuthBinding.inflate(layoutInflater)
        binding.input.hint = when (AuthMethod.of(navArgs.authMethod)) {
            AuthMethod.PASSWORD -> getString(R.string.hint_enter_password)
            AuthMethod.KEY -> getString(R.string.hint_enter_passphrase)
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_authentication)
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(UiR.string.common_continue) { _, _ ->
                val password = binding.input.text.toString()
                viewModel.obtainEvent(ExplorerIntent.Authenticate(password))
            }
            .create()
    }
}