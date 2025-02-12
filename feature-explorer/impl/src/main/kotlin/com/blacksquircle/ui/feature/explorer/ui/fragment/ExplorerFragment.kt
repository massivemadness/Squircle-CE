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

package com.blacksquircle.ui.feature.explorer.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.contract.StoragePermission
import com.blacksquircle.ui.core.extensions.navigateTo
import com.blacksquircle.ui.core.extensions.observeFragmentResult
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.extensions.viewModels
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

internal class ExplorerFragment : Fragment() {

    @Inject
    lateinit var viewModelProvider: Provider<ExplorerViewModel>

    private val viewModel by viewModels<ExplorerViewModel> { viewModelProvider.get() }
    private val navController by lazy { findNavController() }
    private val storagePermission = StoragePermission(this) { result ->
        when (result) {
            PermissionResult.DENIED,
            PermissionResult.DENIED_FOREVER -> viewModel.onPermissionDenied()
            PermissionResult.GRANTED -> viewModel.onPermissionGranted()
        }
    }

    override fun onAttach(context: Context) {
        ExplorerComponent.buildOrGet(context).inject(this)
        super.onAttach(context)
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
                    ExplorerScreen(viewModel)
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
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.Navigation -> navController.navigateTo(event.screen)
                    is ViewEvent.PopBackStack -> navController.popBackStack()
                    is ExplorerViewEvent.RequestPermission -> storagePermission.launch()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        observeFragmentResult(Screen.Server.KEY_SAVE) {
            viewModel.onFilesystemAdded()
        }
        observeFragmentResult(KEY_AUTHENTICATION) { bundle ->
            val credentials = bundle.getString(ARG_USER_INPUT).orEmpty()
            // viewModel.obtainEvent(ExplorerIntent.Authenticate(credentials))
        }
        observeFragmentResult(KEY_COMPRESS_FILE) { bundle ->
            val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
            // viewModel.obtainEvent(ExplorerIntent.CompressFile(fileName))
        }
        observeFragmentResult(KEY_CREATE_FILE) { bundle ->
            val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
            val isFolder = bundle.getBoolean(ARG_IS_FOLDER)
            // viewModel.obtainEvent(ExplorerIntent.CreateFile(fileName, isFolder))
        }
        observeFragmentResult(KEY_RENAME_FILE) { bundle ->
            val fileName = bundle.getString(ARG_USER_INPUT).orEmpty()
            // viewModel.obtainEvent(ExplorerIntent.RenameFile(fileName))
        }
        observeFragmentResult(KEY_DELETE_FILE) {
            // viewModel.obtainEvent(ExplorerIntent.DeleteFile)
        }
    }

    companion object {

        const val KEY_AUTHENTICATION = "KEY_AUTHENTICATION"
        const val KEY_COMPRESS_FILE = "KEY_COMPRESS_FILE"
        const val KEY_CREATE_FILE = "KEY_CREATE_FILE"
        const val KEY_RENAME_FILE = "KEY_RENAME_FILE"
        const val KEY_DELETE_FILE = "KEY_DELETE_FILE"

        const val ARG_USER_INPUT = "ARG_USER_INPUT"
        const val ARG_IS_FOLDER = "ARG_IS_FOLDER"
    }
}