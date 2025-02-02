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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.OpenFileContract
import com.blacksquircle.ui.core.extensions.extractFilePath
import com.blacksquircle.ui.core.extensions.sendFragmentResult
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.servers.data.mapper.ServerMapper
import com.blacksquircle.ui.feature.servers.ui.fragment.CloudFragment
import com.blacksquircle.ui.feature.servers.ui.navigation.ServerViewEvent
import com.blacksquircle.ui.feature.servers.ui.viewmodel.ServerViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
                val filePath = context?.extractFilePath(result.uri)
                viewModel.onKeyFileChosen(filePath.orEmpty())
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
                    ServerScreen(
                        viewModel = viewModel,
                        navController = navController,
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ServerViewEvent.SendSaveResult -> {
                        sendFragmentResult(
                            resultKey = CloudFragment.KEY_SAVE,
                            bundle = ServerMapper.toBundle(event.serverConfig)
                        )
                        navController.popBackStack()
                    }
                    is ServerViewEvent.SendDeleteResult -> {
                        sendFragmentResult(
                            resultKey = CloudFragment.KEY_DELETE,
                            bundle = ServerMapper.toBundle(event.serverConfig)
                        )
                        navController.popBackStack()
                    }
                    is ServerViewEvent.ChooseFile -> {
                        openFileContract.launch(
                            OpenFileContract.OCTET_STREAM,
                            OpenFileContract.PEM,
                        )
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}