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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.DefaultSelectionTracker
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
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
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.viewstate.DirectoryViewState
import com.blacksquircle.ui.feature.explorer.ui.viewstate.ExplorerViewState
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ExplorerFragment : Fragment(R.layout.fragment_explorer), BackPressedHandler {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val binding by viewBinding(FragmentExplorerBinding::bind)
    private val navController by lazy { findNavController() }
    private val requestAccess = registerForActivityResult(RequestPermission()) { result ->
        if (result) permissionGranted() else permissionRejected()
    }

    private lateinit var tracker: SelectionTracker<String>
    private lateinit var tabAdapter: DirectoryAdapter
    private lateinit var fileAdapter: FileAdapter

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = DirectoryAdapter().also {
            tabAdapter = it
        }
        tabAdapter.setOnTabSelectedListener(object : TabAdapter.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                val selected = tabAdapter.getItem(position)
                viewModel.obtainEvent(ExplorerEvent.ListFiles(selected))
            }
        })

        binding.filesRecyclerView.setHasFixedSize(true)
        binding.filesRecyclerView.adapter = FileAdapter(
            onItemClickListener = object : OnItemClickListener<FileModel> {
                override fun onClick(item: FileModel) {
                    if (!tracker.hasSelection()) {
                        if (item.isFolder) {
                            viewModel.obtainEvent(ExplorerEvent.ListFiles(item))
                        } else when (item.getType()) {
                            FileType.ARCHIVE -> Unit // extract
                            FileType.DEFAULT,
                            FileType.TEXT -> viewModel.obtainEvent(ExplorerEvent.OpenFile(item))
                            else -> viewModel.obtainEvent(ExplorerEvent.OpenFileAs(item))
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
                FileKeyProvider(binding.recyclerView),
                SelectionPredicates.createSelectAnything(),
                StorageStrategy.createStringStorage()
            ).also {
                tracker = it
            },
            viewMode = FileAdapter.VIEW_MODE_COMPACT
        ).also {
            fileAdapter = it
        }

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionCleared() {
                    val selection = fileAdapter.selection(tracker.selection)
                    viewModel.obtainEvent(ExplorerEvent.SelectFiles(selection))
                }

                override fun onSelectionChanged() {
                    val selection = fileAdapter.selection(tracker.selection)
                    viewModel.obtainEvent(ExplorerEvent.SelectFiles(selection))
                }
            }
        )

        binding.swipeRefresh.setOnRefreshListener {
            val index = tabAdapter.itemCount - 1
            val selected = tabAdapter.getItem(index)
            viewModel.obtainEvent(ExplorerEvent.Refresh(selected))
        }
        binding.actionAccess.setOnClickListener {
            context?.checkStorageAccess(
                onSuccess = ::permissionGranted,
                onFailure = ::permissionRejected
            )
        }
        binding.actionHome.setOnClickListener {
            val selected = tabAdapter.getItem(0)
            viewModel.obtainEvent(ExplorerEvent.ListFiles(selected))
        }

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            stopActionMode()
        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_explorer_default, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                val actionShowHidden = menu.findItem(R.id.action_show_hidden)
                val actionOpenAs = menu.findItem(R.id.action_open_as)
                val actionRename = menu.findItem(R.id.action_rename)
                val actionProperties = menu.findItem(R.id.action_properties)
                val actionCopyPath = menu.findItem(R.id.action_copy_path)

                val sortByName = menu.findItem(R.id.sort_by_name)
                val sortBySize = menu.findItem(R.id.sort_by_size)
                val sortByDate = menu.findItem(R.id.sort_by_date)

                // actionShowHidden?.isChecked = viewModel.showHidden

                val selectionSize = tracker.selection.size()
                if (selectionSize > 1) { // if more than 1 file selected
                    actionOpenAs?.isVisible = false
                    actionRename?.isVisible = false
                    actionProperties?.isVisible = false
                    actionCopyPath?.isVisible = false
                }

                /*when (viewModel.sortMode) {
                    FileSorter.SORT_BY_NAME -> sortByName?.isChecked = true
                    FileSorter.SORT_BY_SIZE -> sortBySize?.isChecked = true
                    FileSorter.SORT_BY_DATE -> sortByDate?.isChecked = true
                }*/
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_copy -> viewModel.obtainEvent(ExplorerEvent.Copy)
                    R.id.action_cut -> viewModel.obtainEvent(ExplorerEvent.Cut)
                    R.id.action_delete -> viewModel.obtainEvent(ExplorerEvent.Delete)
                    R.id.action_select_all -> viewModel.obtainEvent(ExplorerEvent.SelectAll)
                    R.id.action_open_as -> viewModel.obtainEvent(ExplorerEvent.OpenFileAs())
                    R.id.action_rename -> viewModel.obtainEvent(ExplorerEvent.Rename)
                    R.id.action_properties -> viewModel.obtainEvent(ExplorerEvent.Properties)
                    R.id.action_copy_path -> viewModel.obtainEvent(ExplorerEvent.CopyPath)
                    R.id.action_create_zip -> viewModel.obtainEvent(ExplorerEvent.Compress)
                    R.id.action_show_hidden -> viewModel.obtainEvent(
                        if (!menuItem.isChecked) {
                            ExplorerEvent.HideHidden
                        } else {
                            ExplorerEvent.ShowHidden
                        }
                    )
                    R.id.action_search -> {
                        val searchView = menuItem.actionView as? SearchView
                        searchView?.debounce(viewLifecycleOwner.lifecycleScope) { query ->
                            viewModel.obtainEvent(ExplorerEvent.SearchFiles(query))
                        }
                    }
                    R.id.sort_by_name -> viewModel.obtainEvent(ExplorerEvent.SortByName)
                    R.id.sort_by_size -> viewModel.obtainEvent(ExplorerEvent.SortBySize)
                    R.id.sort_by_date -> viewModel.obtainEvent(ExplorerEvent.SortByDate)
                }
                return false
            }
        }, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.obtainEvent(ExplorerEvent.SearchFiles(""))
        tracker.clearSelection()
    }

    override fun handleOnBackPressed(): Boolean {
        return viewModel.handleOnBackPressed()
    }

    private fun observeViewModel() {
        viewModel.explorerViewState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ExplorerViewState.Stub -> Unit
                    is ExplorerViewState.Data -> {
                        binding.toolbar.isVisible = true
                        binding.recyclerView.isVisible = true
                        binding.actionHome.isVisible = true
                        binding.actionBuffer.setImageResource(
                            when (state.bufferType) {
                                BufferType.COPY -> R.drawable.ic_paste
                                BufferType.CUT -> R.drawable.ic_paste
                                else -> R.drawable.ic_plus
                            }
                        )
                        binding.actionBuffer.setOnClickListener {
                            when (state.bufferType) {
                                BufferType.COPY -> viewModel.obtainEvent(ExplorerEvent.Paste)
                                BufferType.CUT -> viewModel.obtainEvent(ExplorerEvent.Paste)
                                else -> viewModel.obtainEvent(ExplorerEvent.Create)
                            }
                        }
                        tabAdapter.submitList(state.breadcrumbs)
                        tabAdapter.select(state.breadcrumbs.size - 1)
                        if (state.selection.isNotEmpty()) {
                            startActionMode(state.selection.size)
                        } else {
                            stopActionMode()
                        }
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
                        fileAdapter.submitList(emptyList())
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
                    is ViewEvent.Navigation -> navController.navigate(event.screen)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun startActionMode(size: Int) {
        if (tracker.hasSelection()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            binding.toolbar.title = size.toString()
            binding.toolbar.replaceMenu(R.menu.menu_explorer_actions)
        }
    }

    private fun stopActionMode() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.title = getString(R.string.label_local_storage)
        binding.toolbar.replaceMenu(R.menu.menu_explorer_default)
        tracker.clearSelection()
        fileAdapter.notifyDataSetChanged()
    }

    private fun toggleSelection(fileModel: FileModel) {
        val index = fileAdapter.currentList.indexOf(fileModel)
        if (tracker.isSelected(fileModel.path)) {
            tracker.deselect(fileModel.path)
        } else {
            tracker.select(fileModel.path)
        }
        fileAdapter.notifyItemChanged(index)
    }

    private fun permissionGranted() {
        val index = tabAdapter.itemCount - 1
        val selected = tabAdapter.getItem(index)
        viewModel.obtainEvent(ExplorerEvent.ListFiles(selected))
    }

    private fun permissionRejected() {
        activity?.requestStorageAccess(
            showRequestDialog = { requestAccess.launch(WRITE_EXTERNAL_STORAGE) },
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