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

package com.blacksquircle.ui.feature.fonts.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.OpenFileContract
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.fonts.ui.viewmodel.FontViewEvent
import com.blacksquircle.ui.feature.fonts.ui.viewmodel.FontsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class FontsFragment : Fragment() {

    private val viewModel by viewModels<FontsViewModel>()
    private val navController by lazy { findNavController() }
    private val openFileContract = OpenFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onFontLoaded(result.uri)
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
                    FontsScreen(viewModel)
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
                    is ViewEvent.PopBackStack -> navController.popBackStack()
                    is FontViewEvent.ChooseFont -> openFileContract.launch(
                        OpenFileContract.OCTET_STREAM,
                        OpenFileContract.X_FONT,
                        OpenFileContract.FONT,
                    )
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    private fun observeViewModel() {
        viewModel.fontsState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is FontsViewState.Empty -> {
                        binding.loadingBar.isVisible = false
                        binding.emptyView.isVisible = true
                        binding.recyclerView.isInvisible = true
                    }
                    is FontsViewState.Data -> {
                        binding.loadingBar.isVisible = false
                        binding.emptyView.isVisible = false
                        binding.recyclerView.isInvisible = false
                        adapter.submitList(state.fonts)
                    }
                    is FontsViewState.Loading -> {
                        binding.loadingBar.isVisible = true
                        binding.emptyView.isVisible = false
                        binding.recyclerView.isInvisible = true
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }*/
}