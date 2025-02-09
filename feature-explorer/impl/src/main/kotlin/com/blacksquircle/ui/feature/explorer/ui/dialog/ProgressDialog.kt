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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.contract.NotificationPermission
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.extensions.navigateTo
import com.blacksquircle.ui.core.extensions.viewModels
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.internal.ExplorerComponent
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerViewEvent
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.service.FileService
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ProgressViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class ProgressDialog : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ProgressViewModel.Factory

    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<ProgressDialogArgs>()
    private val viewModel by viewModels<ProgressViewModel> {
        viewModelFactory.create(navArgs.taskId)
    }
    private val notificationPermission = NotificationPermission(this) { result ->
        when (result) {
            PermissionResult.DENIED,
            PermissionResult.DENIED_FOREVER -> {
                navController.navigateTo(ExplorerScreen.NotificationDeniedForever)
            }
            PermissionResult.GRANTED -> {
                val intent = Intent(requireContext(), FileService::class.java).apply {
                    action = FileService.ACTION_START_TASK
                    putExtra(FileService.ARG_TASK_ID, navArgs.taskId)
                }
                ContextCompat.startForegroundService(requireContext(), intent)
                navController.popBackStack()
            }
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
                    ProgressScreen(viewModel)
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
                        while (navController.currentDestination?.id != R.id.progressDialog) {
                            navController.popBackStack()
                        }
                        navController.popBackStack()
                    }
                    is ExplorerViewEvent.RunInBackground -> {
                        notificationPermission.launch()
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}