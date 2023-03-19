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

package com.blacksquircle.ui.feature.explorer.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.DefaultSelectionTracker
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.blacksquircle.ui.core.adapter.OnItemClickListener
import com.blacksquircle.ui.core.adapter.TabAdapter
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.contract.StoragePermission
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.*
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.BackPressedHandler
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.FileKeyProvider
import com.blacksquircle.ui.feature.explorer.data.utils.FileSorter
import com.blacksquircle.ui.feature.explorer.data.utils.Operation
import com.blacksquircle.ui.feature.explorer.data.utils.clipText
import com.blacksquircle.ui.feature.explorer.databinding.FragmentExplorerBinding
import com.blacksquircle.ui.feature.explorer.domain.model.FilesystemModel
import com.blacksquircle.ui.feature.explorer.ui.adapter.DirectoryAdapter
import com.blacksquircle.ui.feature.explorer.ui.adapter.FileAdapter
import com.blacksquircle.ui.feature.explorer.ui.adapter.ServerAdapter
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerErrorAction
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerViewEvent
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerViewState
import com.blacksquircle.ui.feature.explorer.ui.mvi.ToolbarViewState
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class ExplorerFragment : Fragment(R.layout.fragment_explorer), BackPressedHandler {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val binding by viewBinding(FragmentExplorerBinding::bind)
    private val navController by lazy { findNavController() }
    private val storagePermission = StoragePermission(this) { result ->
        when (result) {
            PermissionResult.DENIED,
            PermissionResult.DENIED_FOREVER -> {
                navController.navigate(ExplorerScreen.StorageDeniedForever)
            }
            PermissionResult.GRANTED -> {
                viewModel.obtainEvent(ExplorerIntent.Refresh)
            }
        }
    }
    private val onTabSelectedListener = object : TabAdapter.OnTabSelectedListener {
        override fun onTabSelected(position: Int) {
            viewModel.obtainEvent(ExplorerIntent.SelectTab(position))
        }
    }
    private val defaultMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_explorer_default, menu)

            val searchItem = menu.findItem(R.id.action_search)
            val searchView = searchItem?.actionView as? SearchView
            searchView?.debounce(viewLifecycleOwner.lifecycleScope) { query ->
                viewModel.obtainEvent(ExplorerIntent.SearchFiles(query))
            }

            if (viewModel.query.isNotEmpty()) {
                searchItem?.expandActionView()
                searchView?.setQuery(viewModel.query, false)
            }
        }
        override fun onPrepareMenu(menu: Menu) {
            val showHidden = menu.findItem(R.id.show_hidden)
            val sortByName = menu.findItem(R.id.sort_by_name)
            val sortBySize = menu.findItem(R.id.sort_by_size)
            val sortByDate = menu.findItem(R.id.sort_by_date)

            when (viewModel.sortMode) {
                FileSorter.SORT_BY_NAME -> sortByName?.isChecked = true
                FileSorter.SORT_BY_SIZE -> sortBySize?.isChecked = true
                FileSorter.SORT_BY_DATE -> sortByDate?.isChecked = true
            }
            showHidden?.isChecked = viewModel.showHidden
        }
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.show_hidden -> viewModel.obtainEvent(
                    if (!menuItem.isChecked) {
                        ExplorerIntent.ShowHidden
                    } else {
                        ExplorerIntent.HideHidden
                    },
                )
                R.id.sort_by_name -> viewModel.obtainEvent(ExplorerIntent.SortByName)
                R.id.sort_by_size -> viewModel.obtainEvent(ExplorerIntent.SortBySize)
                R.id.sort_by_date -> viewModel.obtainEvent(ExplorerIntent.SortByDate)
            }
            return true
        }
    }
    private val selectionMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_explorer_selection, menu)
        }
        override fun onPrepareMenu(menu: Menu) {
            val actionOpenWith = menu.findItem(R.id.action_open_with)
            val actionRename = menu.findItem(R.id.action_rename)
            val actionProperties = menu.findItem(R.id.action_properties)
            val actionCopyPath = menu.findItem(R.id.action_copy_path)

            if (tracker.selection.size() > 1) { // if more than 1 file selected
                actionOpenWith?.isVisible = false
                actionRename?.isVisible = false
                actionProperties?.isVisible = false
                actionCopyPath?.isVisible = false
            }
        }
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_copy -> viewModel.obtainEvent(ExplorerIntent.Copy)
                R.id.action_cut -> viewModel.obtainEvent(ExplorerIntent.Cut)
                R.id.action_delete -> viewModel.obtainEvent(ExplorerIntent.Delete)
                R.id.action_select_all -> viewModel.obtainEvent(ExplorerIntent.SelectAll)
                R.id.action_open_with -> viewModel.obtainEvent(ExplorerIntent.OpenFileWith())
                R.id.action_rename -> viewModel.obtainEvent(ExplorerIntent.Rename)
                R.id.action_properties -> viewModel.obtainEvent(ExplorerIntent.Properties)
                R.id.action_copy_path -> viewModel.obtainEvent(ExplorerIntent.CopyPath)
                R.id.action_create_zip -> viewModel.obtainEvent(ExplorerIntent.Compress)
            }
            return true
        }
    }

    private lateinit var tracker: SelectionTracker<String>
    private lateinit var serverAdapter: ServerAdapter
    private lateinit var tabAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.appBar.updatePadding(top = top)
            binding.root.updatePadding(bottom = bottom)
        }

        binding.tabLayout.setHasFixedSize(true)
        binding.tabLayout.adapter = DirectoryAdapter().also {
            tabAdapter = it
        }
        binding.dropdown.adapter = ServerAdapter(requireContext()) {
            navController.navigate(Screen.AddServer)
            binding.dropdown.dismiss()
        }.also {
            serverAdapter = it
        }
        binding.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = serverAdapter.getItem(position)
                viewModel.obtainEvent(ExplorerIntent.SelectFilesystem(selected.uuid))
            }
        }

        binding.filesRecyclerView.setHasFixedSize(true)
        binding.filesRecyclerView.adapter = FileAdapter(
            onItemClickListener = object : OnItemClickListener<FileModel> {
                override fun onClick(item: FileModel) {
                    if (!tracker.hasSelection()) {
                        if (item.directory) {
                            viewModel.obtainEvent(ExplorerIntent.OpenFolder(item))
                        } else {
                            viewModel.obtainEvent(ExplorerIntent.OpenFile(item))
                        }
                    } else {
                        toggleSelection(item)
                    }
                }

                override fun onLongClick(item: FileModel): Boolean {
                    toggleSelection(item)
                    return true
                }
            },
            selectionTracker = DefaultSelectionTracker(
                "DefaultSelectionTracker",
                FileKeyProvider(binding.filesRecyclerView),
                SelectionPredicates.createSelectAnything(),
                StorageStrategy.createStringStorage(),
            ).also {
                tracker = it
            },
            viewMode = viewModel.viewMode,
        ).also {
            fileAdapter = it
        }

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionCleared() {
                    val selection = fileAdapter.selection(tracker.selection)
                    viewModel.obtainEvent(ExplorerIntent.SelectFiles(selection))
                }
                override fun onSelectionChanged() {
                    val selection = fileAdapter.selection(tracker.selection)
                    viewModel.obtainEvent(ExplorerIntent.SelectFiles(selection))
                }
            },
        )

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.obtainEvent(ExplorerIntent.Refresh)
        }
        binding.actionHome.setOnClickListener {
            viewModel.obtainEvent(ExplorerIntent.SelectTab(0))
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            viewModel.obtainEvent(ExplorerIntent.UnselectAll)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tracker.clearSelection()
    }

    override fun handleOnBackPressed(): Boolean {
        if (tracker.hasSelection()) {
            viewModel.obtainEvent(ExplorerIntent.UnselectAll)
            return true
        }
        return false
    }

    private fun observeViewModel() {
        viewModel.toolbarViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ToolbarViewState.ActionBar -> {
                        binding.actionOperation.setImageResource(
                            when (state.operation) {
                                Operation.CUT -> UiR.drawable.ic_paste
                                Operation.COPY -> UiR.drawable.ic_paste
                                else -> UiR.drawable.ic_plus
                            },
                        )
                        binding.actionOperation.setOnClickListener {
                            when (state.operation) {
                                Operation.CUT -> viewModel.obtainEvent(ExplorerIntent.CutFile)
                                Operation.COPY -> viewModel.obtainEvent(ExplorerIntent.CopyFile)
                                else -> viewModel.obtainEvent(ExplorerIntent.Create)
                            }
                        }
                        tabAdapter.removeOnTabSelectedListener()
                        tabAdapter.submitList(state.breadcrumbs, state.breadcrumbs.size - 1)
                        tabAdapter.setOnTabSelectedListener(onTabSelectedListener)
                        if (state.selection.isNotEmpty()) {
                            startActionMode(state.selection.size)
                        } else {
                            stopActionMode()
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.explorerViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ExplorerViewState.Files -> {
                        binding.swipeRefresh.isVisible = true
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = false
                        fileAdapter.submitList(state.data)
                    }
                    is ExplorerViewState.Error -> {
                        binding.swipeRefresh.isVisible = false
                        binding.errorView.root.isVisible = true
                        binding.loadingBar.isVisible = false
                        binding.errorView.image.setImageResource(state.image)
                        binding.errorView.title.text = state.title
                        binding.errorView.subtitle.text = state.subtitle
                        when (val action = state.action) {
                            is ExplorerErrorAction.Undefined -> {
                                binding.errorView.actionPrimary.isVisible = false
                                binding.errorView.actionPrimary.setOnClickListener(null)
                            }
                            is ExplorerErrorAction.RequestPermission -> {
                                binding.errorView.actionPrimary.isVisible = true
                                binding.errorView.actionPrimary.setText(R.string.action_grant_access)
                                binding.errorView.actionPrimary.setOnClickListener {
                                    storagePermission.launch()
                                }
                            }
                            is ExplorerErrorAction.EnterCredentials -> {
                                binding.errorView.actionPrimary.isVisible = true
                                binding.errorView.actionPrimary.setText(R.string.action_authenticate)
                                binding.errorView.actionPrimary.setOnClickListener {
                                    navController.navigate(
                                        ExplorerScreen.AuthDialog(action.authMethod)
                                    )
                                }
                            }
                        }
                        fileAdapter.submitList(emptyList())
                    }
                    is ExplorerViewState.Loading -> {
                        binding.swipeRefresh.isVisible = true
                        binding.errorView.root.isVisible = false
                        binding.loadingBar.isVisible = true
                        fileAdapter.submitList(emptyList())
                    }
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
                    is ViewEvent.Navigation -> navController.navigate(event.screen)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.customEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ExplorerViewEvent.SelectAll -> {
                        tracker.setItemsSelected(fileAdapter.currentList.map(FileModel::fileUri), true)
                        fileAdapter.notifyItemRangeChanged(0, fileAdapter.itemCount)
                    }
                    is ExplorerViewEvent.CopyPath -> {
                        event.fileModel.path.clipText(context)
                        context?.showToast(R.string.message_done)
                    }
                    else -> Unit
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.filesystems.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNot(List<FilesystemModel>::isEmpty)
            .onEach { filesystems ->
                val position = filesystems.indexOrNull { it.uuid == viewModel.filesystem } ?: 0
                serverAdapter.submitList(filesystems)
                binding.dropdown.setSelection(position)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun startActionMode(size: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = size.toString()
        binding.dropdown.isVisible = false
        activity?.removeMenuProvider(defaultMenuProvider)
        activity?.removeMenuProvider(selectionMenuProvider)
        activity?.addMenuProvider(selectionMenuProvider, viewLifecycleOwner)
    }

    private fun stopActionMode() {
        tracker.clearSelection()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.dropdown.isVisible = true
        activity?.removeMenuProvider(selectionMenuProvider)
        activity?.removeMenuProvider(defaultMenuProvider)
        activity?.addMenuProvider(defaultMenuProvider, viewLifecycleOwner)
        fileAdapter.notifyItemRangeChanged(0, fileAdapter.itemCount)
    }

    private fun toggleSelection(fileModel: FileModel) {
        val index = fileAdapter.currentList.indexOf(fileModel)
        if (tracker.isSelected(fileModel.fileUri)) {
            tracker.deselect(fileModel.fileUri)
        } else {
            tracker.select(fileModel.fileUri)
        }
        fileAdapter.notifyItemChanged(index)
    }
}