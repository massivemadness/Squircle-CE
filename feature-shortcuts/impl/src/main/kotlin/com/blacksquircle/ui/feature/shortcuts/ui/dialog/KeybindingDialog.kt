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

package com.blacksquircle.ui.feature.shortcuts.ui.dialog

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
import com.blacksquircle.ui.core.extensions.sendFragmentResult
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.shortcuts.data.mapper.ShortcutMapper
import com.blacksquircle.ui.feature.shortcuts.ui.fragment.ShortcutsFragment
import com.blacksquircle.ui.feature.shortcuts.ui.navigation.ShortcutViewEvent
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.KeybindingViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class KeybindingDialog : DialogFragment() {

    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<KeybindingDialogArgs>()
    private val viewModel by viewModels<KeybindingViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<KeybindingViewModel.Factory> { factory ->
                factory.create(initial = navArgs.keybinding)
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    KeybindingScreen(viewModel)
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
                    is ViewEvent.PopBackStack -> {
                        navController.popBackStack()
                    }
                    is ShortcutViewEvent.SendSaveResult -> {
                        sendFragmentResult(
                            resultKey = ShortcutsFragment.KEY_SAVE,
                            bundle = ShortcutMapper.toBundle(event.keybinding),
                        )
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}