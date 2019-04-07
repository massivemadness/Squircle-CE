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

package com.lightteam.modpeide.presentation.main.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.tabs.TabLayout
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.isValidFileName
import com.lightteam.modpeide.databinding.FragmentExplorerBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.presentation.main.activities.MainActivity.Companion.REQUEST_READ_WRITE
import com.lightteam.modpeide.presentation.main.activities.MainActivity.Companion.REQUEST_READ_WRITE2
import com.lightteam.modpeide.presentation.main.adapters.BreadcrumbAdapter
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class FragmentExplorer : DaggerFragment(),
    SwipeRefreshLayout.OnRefreshListener,
    TabLayout.OnTabSelectedListener {

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var adapter: BreadcrumbAdapter

    private lateinit var binding: FragmentExplorerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explorer, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFilter()
    }

    override fun onRefresh() {
        viewModel.loadFiles(adapter.get(binding.tabLayout.selectedTabPosition))
        binding.swipeRefresh.isRefreshing = false
    }

    // region MENU

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explorer, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        viewModel.searchEvents(searchView)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val showHiddenItem = menu.findItem(R.id.action_show_hidden)
        showHiddenItem.isChecked = viewModel.showHidden

        val sortByName = menu.findItem(R.id.sort_by_name)
        val sortBySize = menu.findItem(R.id.sort_by_size)
        val sortByDate = menu.findItem(R.id.sort_by_date)
        when (viewModel.sortMode) {
            FileSorter.SORT_BY_NAME -> sortByName.isChecked = true
            FileSorter.SORT_BY_SIZE -> sortBySize.isChecked = true
            FileSorter.SORT_BY_DATE -> sortByDate.isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_show_hidden -> {
                viewModel.setFilterHidden(!item.isChecked)
            }
            R.id.sort_by_name -> {
                viewModel.setSortMode("0")
            }
            R.id.sort_by_size -> {
                viewModel.setSortMode("1")
            }
            R.id.sort_by_date -> {
                viewModel.setSortMode("2")
            }
        }
        onRefresh()
        return super.onOptionsItemSelected(item)
    }

    // endregion MENU

    // region TABS

    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabSelected(tab: TabLayout.Tab) {
        viewModel.loadFiles(adapter.get(tab.position))
    }

    // endregion TABS

    private fun setupListeners() {
        binding.actionAccess.setOnClickListener {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_READ_WRITE
            )
        }
        binding.swipeRefresh.setOnRefreshListener(this)
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.actionHome.setOnClickListener {
            removeAfter(viewModel.getDefaultLocation())
            addToStack(viewModel.getDefaultLocation())
        }
        binding.actionAdd.setOnClickListener {
            showCreateDialog()
        }
    }

    private fun setupObservers() {
        viewModel.hasAccessEvent.observe(this.viewLifecycleOwner, Observer { hasAccess ->
            if(hasAccess) {
                viewModel.hasPermission.set(true)
                addToStack(viewModel.getDefaultLocation())
            } else {
                binding.actionAccess.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.parse("package:" + activity?.packageName)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_READ_WRITE2)
                }
            }
        })
        viewModel.deleteFileEvent.observe(this.viewLifecycleOwner, Observer { deletedFile ->
            removeAfter(deletedFile)
        })
        viewModel.renameFileEvent.observe(this.viewLifecycleOwner, Observer { renamedFile ->
            removeAfter(renamedFile)
        })
        viewModel.tabsEvent.observe(this.viewLifecycleOwner, Observer { path ->
            addToStack(path)
        })
    }

    // region INTERNAL

    private fun showCreateDialog() {
        MaterialDialog(activity!!).show {
            title(R.string.dialog_title_create)
            input(
                waitForPositiveButton = false,
                hintRes = R.string.hint_enter_file_name
            ) { dialog, text ->
                val inputField = dialog.getInputField()
                val isValid = text.toString().isValidFileName()

                inputField.error = if(isValid) {
                    null
                } else {
                    getString(R.string.message_invalid_file_name)
                }
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
            }
            checkBoxPrompt(R.string.hint_box_is_folder) {}
            positiveButton(R.string.action_create)
            negativeButton(R.string.action_cancel)
            positiveButton {
                val fileName = getInputField().text.toString()
                val isFolder = getCheckBoxPrompt().isChecked

                val parent = adapter.get(binding.tabLayout.selectedTabPosition)
                val child = parent.copy(
                    path = parent.path + "/$fileName",
                    isFolder = isFolder
                )
                viewModel.createFile(parent, child)
            }
        }
    }

    private fun addToStack(fileModel: FileModel) {
        val currPos = binding.tabLayout.selectedTabPosition
        val nextPos = currPos + 1
        val pathPos = adapter.indexOf(fileModel)

        when { // bad practice, i know
            currPos == -1 -> {
                addTab(fileModel)
            }
            pathPos == nextPos -> {
                binding.tabLayout.post {
                    binding.tabLayout.getTabAt(nextPos)?.select()
                }
            }
            pathPos == -1 -> {
                for (pos in adapter.getCount() downTo nextPos) {
                    binding.tabLayout.getTabAt(pos)?.let {
                        adapter.removeAt(pos)
                        binding.tabLayout.removeTab(it)
                    }
                }
                addTab(fileModel)
            }
        }
    }

    private fun removeAfter(fileModel: FileModel) {
        val indexOfFile = adapter.indexOf(fileModel)
        if(indexOfFile != -1) {
            for (pos in adapter.getCount() downTo indexOfFile) {
                binding.tabLayout.getTabAt(pos)?.let {
                    adapter.removeAt(pos)
                    binding.tabLayout.removeTab(it)
                }
            }
        }
    }

    private fun addTab(fileModel: FileModel) {
        val tab = binding.tabLayout.newTab()
        tab.text = fileModel.name
        tab.setCustomView(R.layout.item_tab_directory)
        adapter.add(fileModel)
        binding.tabLayout.addTab(tab)
        binding.tabLayout.post { tab.select() }
    }

    // endregion INTERNAL
}