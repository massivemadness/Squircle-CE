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

package com.blacksquircle.ui.feature.servers.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.OpenFileContract
import com.blacksquircle.ui.core.extensions.sendResult
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.dropdown.Dropdown
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.data.mapper.ServerMapper
import com.blacksquircle.ui.feature.servers.ui.fragment.CloudFragment
import com.blacksquircle.ui.feature.servers.ui.viewmodel.ServerViewModel
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileServer
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import com.blacksquircle.ui.ds.R as UiR

@AndroidEntryPoint
internal class ServerDialog : DialogFragment() {

    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<ServerDialogArgs>()
    private val viewModel by viewModels<ServerViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<ServerViewModel.Factory> { factory ->
                factory.create(serverData = navArgs.data)
            }
        }
    )
    private val openFileContract = OpenFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> {
                // val filePath = context?.extractFilePath(result.uri)
                // binding.inputKeyFile.setText(filePath)
            }

            is ContractResult.Canceled -> Unit
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    ServerScreen(viewModel, navController)
                }
            }
        }
    }
}