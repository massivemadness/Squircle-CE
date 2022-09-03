/*
 * Copyright 2022 Squircle IDE contributors.
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

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.DefaultSelectionTracker
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.StorageStrategy
import com.blacksquircle.ui.core.ui.adapter.OnItemClickListener
import com.blacksquircle.ui.core.ui.adapter.TabAdapter
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.*
import com.blacksquircle.ui.core.ui.navigation.BackPressedHandler
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.*
import com.blacksquircle.ui.feature.explorer.databinding.FragmentExplorerBinding
import com.blacksquircle.ui.feature.explorer.ui.adapter.DirectoryAdapter
import com.blacksquircle.ui.feature.explorer.ui.adapter.FileAdapter
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.viewstate.DirectoryViewState
import com.blacksquircle.ui.feature.explorer.ui.viewstate.ExplorerViewState
import com.blacksquircle.ui.filesystem.base.model.FileModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ExplorerFragment : Fragment(R.layout.fragment_explorer), BackPressedHandler {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val binding by viewBinding(FragmentExplorerBinding::bind)
    private val navController by lazy { findNavController() }
    private val requestResult = registerForActivityResult(RequestPermission()) { result ->
        if (result) handleSuccess() else handleFailure()
    }

    private lateinit var directoryAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = DirectoryAdapter().also {
            directoryAdapter = it
        }
        directoryAdapter.setOnTabSelectedListener(object : TabAdapter.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                viewModel.fetchFiles(directoryAdapter.getItem(position))
            }
        })

        binding.filesRecyclerView.setHasFixedSize(true)
        binding.filesRecyclerView.adapter = FileAdapter(
            onItemClickListener = object : OnItemClickListener<FileModel> {
                override fun onClick(item: FileModel) {
                    viewModel.fetchFiles(item)
                }
                override fun onLongClick(item: FileModel): Boolean {
                    return true
                }
            },
            selectionTracker = DefaultSelectionTracker(
                "static",
                FileKeyProvider(binding.recyclerView),
                SelectionPredicates.createSelectAnything(),
                StorageStrategy.createStringStorage()
            ),
            viewMode = FileAdapter.VIEW_MODE_COMPACT
        ).also {
            fileAdapter = it
        }

        binding.swipeRefresh.setOnRefreshListener {
            val index = directoryAdapter.itemCount - 1
            val lastItem = directoryAdapter.getItem(index)
            viewModel.refresh(lastItem)
        }
        binding.actionAccess.setOnClickListener {
            context?.checkStorageAccess(
                onSuccess = ::handleSuccess,
                onFailure = ::handleFailure
            )
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun handleOnBackPressed(): Boolean {
        return false
    }

    private fun observeViewModel() {
        viewModel.explorerViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ExplorerViewState.Stub -> Unit
                    is ExplorerViewState.Breadcrumbs -> {
                        binding.toolbar.isVisible = true
                        binding.recyclerView.isVisible = true
                        binding.actionHome.isVisible = true
                        binding.actionCreate.isVisible = true
                        binding.actionPaste.isVisible = false
                        directoryAdapter.submitList(state.breadcrumbs)
                        directoryAdapter.select(state.breadcrumbs.size - 1)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.directoryViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is DirectoryViewState.Restricted -> {
                        binding.restrictedView.isVisible = true
                        binding.emptyView.isVisible = false
                        binding.loadingBar.isVisible = false
                        binding.swipeRefresh.isVisible = false
                    }
                    is DirectoryViewState.Empty -> {
                        binding.restrictedView.isVisible = false
                        binding.emptyView.isVisible = true
                        binding.loadingBar.isVisible = false
                        binding.swipeRefresh.isVisible = false
                    }
                    is DirectoryViewState.Loading -> {
                        binding.restrictedView.isVisible = false
                        binding.emptyView.isVisible = false
                        binding.loadingBar.isVisible = true
                        binding.swipeRefresh.isVisible = false
                    }
                    is DirectoryViewState.Files -> {
                        binding.restrictedView.isVisible = false
                        binding.emptyView.isVisible = false
                        binding.loadingBar.isVisible = false
                        binding.swipeRefresh.isVisible = true
                        fileAdapter.submitList(state.data)
                    }
                    is DirectoryViewState.Stub -> Unit
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.refreshState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { binding.swipeRefresh.isRefreshing = it }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.PopBackStack -> {
                        // TODO move to prev breadcrumb (event.data as Int) <- count
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleSuccess() {
        val index = directoryAdapter.itemCount - 1
        val lastItem = directoryAdapter.getItem(index)
        viewModel.fetchFiles(lastItem)
    }

    private fun handleFailure() {
        activity?.requestStorageAccess(
            showRequestDialog = { requestResult.launch(WRITE_EXTERNAL_STORAGE) },
            showExplanationDialog = { intent ->
                val screen = ExplorerScreen.RestrictedDialog(
                    action = intent.action.toString(),
                    data = intent.data.toString()
                )
                navController.navigate(screen)
            }
        )
    }
}