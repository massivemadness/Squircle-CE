/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.explorer.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.blacksquircle.ui.data.utils.FileSorter
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.adapters.DirectoryAdapter
import com.blacksquircle.ui.feature.explorer.databinding.FragmentExplorerBinding
import com.blacksquircle.ui.feature.explorer.utils.*
import com.blacksquircle.ui.feature.explorer.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.utils.adapters.TabAdapter
import com.blacksquircle.ui.utils.delegate.viewBinding
import com.blacksquircle.ui.utils.extensions.*
import com.blacksquircle.ui.utils.interfaces.BackPressedHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExplorerFragment : Fragment(R.layout.fragment_explorer), BackPressedHandler {

    private val viewModel: ExplorerViewModel by activityViewModels()
    private val binding: FragmentExplorerBinding by viewBinding()

    private lateinit var navController: NavController
    private lateinit var adapter: DirectoryAdapter

    private var isClosing = false // TODO remove

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        navController = childFragmentManager
            .fragment<NavHostFragment>(R.id.nav_host).navController

        setSupportActionBar(binding.toolbar)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.filesUpdateEvent.call()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.directoryRecyclerView.setHasFixedSize(true)
        binding.directoryRecyclerView.adapter = DirectoryAdapter().also {
            adapter = it
        }
        adapter.setOnTabSelectedListener(object : TabAdapter.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                if (!isClosing) {
                    isClosing = true
                    navigateBreadcrumb(adapter.currentList[position])
                    isClosing = false
                }
            }
        })

        binding.actionHome.setOnClickListener {
            navigateBreadcrumb(adapter.currentList.first())
        }
        binding.actionPaste.setOnClickListener {
            viewModel.pasteEvent.call()
        }
        binding.actionCreate.setOnClickListener {
            viewModel.createEvent.call()
        }

        adapter.submitList(viewModel.tabsList)
    }

    override fun handleOnBackPressed(): Boolean {
        return when {
            !viewModel.selectionEvent.value.isNullOrEmpty() -> {
                stopActionMode()
                return true
            }
            adapter.currentList.isNotEmpty() -> {
                val target = adapter.currentList.lastIndex - 1
                if (target > 0) {
                    navigateBreadcrumb(adapter.currentList[target])
                } else {
                    navigateBreadcrumb(adapter.currentList.first())
                }
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explorer_default, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.debounce(viewLifecycleOwner.lifecycleScope) {
            viewModel.searchFile(it)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val actionShowHidden = menu.findItem(R.id.action_show_hidden)
        val actionOpenAs = menu.findItem(R.id.action_open_as)
        val actionRename = menu.findItem(R.id.action_rename)
        val actionProperties = menu.findItem(R.id.action_properties)
        val actionCopyPath = menu.findItem(R.id.action_copy_path)

        val sortByName = menu.findItem(R.id.sort_by_name)
        val sortBySize = menu.findItem(R.id.sort_by_size)
        val sortByDate = menu.findItem(R.id.sort_by_date)

        actionShowHidden?.isChecked = viewModel.showHidden

        val selectionSize = viewModel.selectionEvent.value?.size ?: 0
        if (selectionSize > 1) { // if more than 1 file selected
            actionOpenAs?.isVisible = false
            actionRename?.isVisible = false
            actionProperties?.isVisible = false
            actionCopyPath?.isVisible = false
        }

        when (viewModel.sortMode) {
            FileSorter.SORT_BY_NAME -> sortByName?.isChecked = true
            FileSorter.SORT_BY_SIZE -> sortBySize?.isChecked = true
            FileSorter.SORT_BY_DATE -> sortByDate?.isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_copy -> viewModel.copyEvent.call()
            R.id.action_cut -> viewModel.cutEvent.call()
            R.id.action_delete -> viewModel.deleteEvent.call()
            R.id.action_select_all -> viewModel.selectAllEvent.call()
            R.id.action_open_as -> viewModel.openAsEvent.call()
            R.id.action_rename -> viewModel.renameEvent.call()
            R.id.action_properties -> viewModel.propertiesEvent.call()
            R.id.action_copy_path -> viewModel.copyPathEvent.call()
            R.id.action_create_zip -> viewModel.archiveEvent.call()
            R.id.action_show_hidden -> viewModel.showHidden = !item.isChecked
            R.id.sort_by_name -> viewModel.sortMode = FileSorter.SORT_BY_NAME
            R.id.sort_by_size -> viewModel.sortMode = FileSorter.SORT_BY_SIZE
            R.id.sort_by_date -> viewModel.sortMode = FileSorter.SORT_BY_DATE
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            context?.showToast(it)
        }
        viewModel.showAppBarEvent.observe(viewLifecycleOwner) {
            binding.appBar.isVisible = it
        }
        viewModel.allowPasteFiles.observe(viewLifecycleOwner) {
            binding.actionPaste.isVisible = it
            binding.actionCreate.isVisible = !it
        }
        viewModel.tabEvent.observe(viewLifecycleOwner) {
            navigateBreadcrumb(it)
        }
        viewModel.selectionEvent.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                viewModel.allowPasteFiles.value = false
                viewModel.tempFiles.clear()
                startActionMode(it)
            } else {
                stopActionMode()
            }
        }
    }

    private fun startActionMode(list: List<FileModel>) {
        binding.toolbar.title = list.size.toString()
        binding.toolbar.replaceMenu(R.menu.menu_explorer_actions)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            stopActionMode()
        }
    }

    private fun stopActionMode() {
        viewModel.deselectAllEvent.call()
        binding.toolbar.setTitle(R.string.label_local_storage)
        binding.toolbar.replaceMenu(R.menu.menu_explorer_default)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun navigateBreadcrumb(tab: FileModel): Boolean {
        return if (adapter.currentList.contains(tab)) {
            val position = adapter.currentList.indexOf(tab)
            val howMany = adapter.itemCount - 1 - position
            val shouldPop = howMany > 0
            if (shouldPop) {
                navController.popBackStack(howMany)
                for (i in 0 until howMany) {
                    adapter.close(adapter.itemCount - 1)
                }
            } else {
                adapter.select(adapter.itemCount - 1)
            }
            shouldPop
        } else {
            viewModel.tabsList.replaceList(adapter.currentList + tab)
            adapter.submitList(adapter.currentList + tab)
            adapter.select(adapter.itemCount - 1)
            false
        }
    }
}