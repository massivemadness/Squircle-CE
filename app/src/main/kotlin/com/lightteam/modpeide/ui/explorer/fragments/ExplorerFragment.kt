/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.ui.explorer.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.lightteam.filesystem.base.model.FileModel
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.databinding.FragmentExplorerBinding
import com.lightteam.modpeide.ui.base.adapters.TabAdapter
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.base.utils.OnBackPressedHandler
import com.lightteam.modpeide.ui.explorer.adapters.DirectoryAdapter
import com.lightteam.modpeide.ui.explorer.utils.Operation
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ExplorerFragment : BaseFragment(R.layout.fragment_explorer),
    OnBackPressedHandler, TabAdapter.OnTabSelectedListener {

    private val viewModel: ExplorerViewModel by activityViewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentExplorerBinding
    private lateinit var adapter: DirectoryAdapter

    private var isClosing = false // TODO remove this
    private var operation = Operation.COPY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        observeViewModel()

        navController = childFragmentManager
            .fragment<NavHostFragment>(R.id.nav_host).navController

        setSupportActionBar(binding.toolbar)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.filesUpdateEvent.call()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.directoryRecyclerView.setHasFixedSize(true)
        binding.directoryRecyclerView.adapter = DirectoryAdapter()
            .also { adapter = it }
        adapter.setOnTabSelectedListener(this)

        binding.actionHome.setOnClickListener {
            val backStackCount = childFragmentManager
                .fragment<NavHostFragment>(R.id.nav_host).backStackEntryCount
            navController.popBackStack(backStackCount - 1)
            removeTabs(backStackCount - 1)
        }
        binding.actionPaste.setOnClickListener {
            viewModel.pasteEvent.value = operation
        }
        binding.actionCreate.setOnClickListener {
            viewModel.createEvent.call()
        }

        if (requireContext().hasExternalStorageAccess()) {
            viewModel.hasPermission.set(true)
        }
    }

    override fun handleOnBackPressed(): Boolean {
        if (!viewModel.selectionEvent.value.isNullOrEmpty()) {
            stopActionMode()
            return true
        } else {
            val backStackCount = childFragmentManager
                .fragment<NavHostFragment>(R.id.nav_host).backStackEntryCount
            if (backStackCount > 1) {
                navController.popBackStack()
                removeTabs(1)
                return true
            }
        }
        return false
    }

    override fun onTabReselected(position: Int) {}
    override fun onTabUnselected(position: Int) {}
    override fun onTabSelected(position: Int) {
        if (!isClosing) {
            isClosing = true
            val howMany = adapter.itemCount - position - 1
            navController.popBackStack(howMany)
            removeTabs(howMany)
            isClosing = false
        }
    }

    // region MENU

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explorer_default, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView
            .queryTextChangeEvents()
            .skipInitialValue()
            .debounce(200, TimeUnit.MILLISECONDS)
            .filter { it.queryText.length >= 2 || it.queryText.isEmpty() }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                viewModel.searchFile(it.queryText)
            }
            .disposeOnFragmentDestroyView()
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
            actionOpenAs.isVisible = false
            actionRename.isVisible = false
            actionProperties.isVisible = false
            actionCopyPath.isVisible = false
        }

        when (viewModel.sortMode) {
            FileSorter.SORT_BY_NAME -> sortByName?.isChecked = true
            FileSorter.SORT_BY_SIZE -> sortBySize?.isChecked = true
            FileSorter.SORT_BY_DATE -> sortByDate?.isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_copy -> {
                viewModel.copyEvent.call()
                operation = Operation.COPY
            }
            R.id.action_cut -> {
                viewModel.cutEvent.call()
                operation = Operation.CUT
            }
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

    // endregion MENU

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner, {
            showToast(it)
        })
        viewModel.tabsEvent.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            adapter.select(adapter.itemCount - 1)
        })
        viewModel.selectionEvent.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                viewModel.allowPasteFiles.set(false)
                viewModel.tempFiles.clear()
                startActionMode(it)
            } else {
                stopActionMode()
            }
        })
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

    private fun removeTabs(howMany: Int) {
        viewModel.tabsList.subList(
            viewModel.tabsList.size - howMany,
            viewModel.tabsList.size
        ).clear()
        for (i in 0 until howMany) {
            adapter.close(adapter.itemCount - 1)
        }
    }
}