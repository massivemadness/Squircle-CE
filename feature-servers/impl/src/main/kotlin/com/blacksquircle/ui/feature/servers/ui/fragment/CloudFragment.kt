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

package com.blacksquircle.ui.feature.servers.ui.fragment

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
import com.blacksquircle.ui.core.extensions.navigateTo
import com.blacksquircle.ui.core.extensions.observeFragmentResult
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.extensions.viewModels
import com.blacksquircle.ui.core.internal.ComponentHolder
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.servers.internal.ServersComponent
import com.blacksquircle.ui.feature.servers.ui.viewmodel.CloudViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

internal class CloudFragment : Fragment() {

    @Inject
    lateinit var viewModelProvider: Provider<CloudViewModel>

    private val viewModel by viewModels<CloudViewModel> { viewModelProvider.get() }
    private val componentHolder by viewModels {
        val component = ServersComponent.buildOrGet(requireContext())
        ComponentHolder(component) { ServersComponent.release() }
    }
    private val navController by lazy { findNavController() }

    override fun onAttach(context: Context) {
        componentHolder.component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    CloudScreen(viewModel)
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
                    is ViewEvent.Navigation -> navController.navigateTo(event.screen)
                    is ViewEvent.PopBackStack -> navController.popBackStack()
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        observeFragmentResult(Screen.Server.KEY_SAVE) {
            viewModel.loadServers()
        }
        observeFragmentResult(Screen.Server.KEY_DELETE) {
            viewModel.loadServers()
        }
    }
}