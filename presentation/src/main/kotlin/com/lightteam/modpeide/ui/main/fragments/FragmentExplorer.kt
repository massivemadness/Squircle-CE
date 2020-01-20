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

package com.lightteam.modpeide.ui.main.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.isValidFileName
import com.lightteam.modpeide.databinding.FragmentExplorerBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.main.activities.MainActivity.Companion.REQUEST_READ_WRITE
import com.lightteam.modpeide.ui.main.activities.MainActivity.Companion.REQUEST_READ_WRITE2
import com.lightteam.modpeide.ui.main.viewmodel.ExplorerViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FragmentExplorer : BaseFragment(), TabLayout.OnTabSelectedListener {

    @Inject
    lateinit var viewModel: ExplorerViewModel

    private lateinit var binding: FragmentExplorerBinding

    override fun layoutId(): Int = R.layout.fragment_explorer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeViewModel()
        setupListeners()

        val parentActivity = activity as AppCompatActivity
        parentActivity.setSupportActionBar(binding.toolbar)

        checkPermissions()
    }

    // region PERMISSIONS

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_READ_WRITE -> {
                viewModel.hasAccessEvent.value = grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkPermissions()
    }

    private fun checkPermissions() {
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel.hasAccessEvent.value = true

            // Check if user opened a file from another app
            if(requireActivity().intent.action == Intent.ACTION_VIEW) {
                //path must be started with /storage/emulated/0/...
                viewModel.openDocument(File(requireActivity().intent.data?.path))
            }
        }
    }

    // endregion PERMISSIONS

    fun onRefresh() {
        //viewModel.provideDirectory(adapter.get(binding.tabLayout.selectedTabPosition))
        //binding.swipeRefresh.isRefreshing = false
    }

    // region MENU

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explorer, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView
            .queryTextChangeEvents()
            .skipInitialValue()
            .debounce(200, TimeUnit.MILLISECONDS)
            .filter { it.queryText.isEmpty() || it.queryText.length >= 2 }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                viewModel.onSearchQueryFilled(it.queryText)
            }
            .disposeOnFragmentDestroyView()
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
        return super.onOptionsItemSelected(item)
    }

    // endregion MENU

    // region TABS

    override fun onTabReselected(tab: TabLayout.Tab) {}
    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabSelected(tab: TabLayout.Tab) {
        viewModel.provideDirectory(tab.position)
    }

    // endregion TABS

    private fun setupListeners() {
        binding.actionAccess.setOnClickListener {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_READ_WRITE
            )
        }
        //binding.swipeRefresh.setOnRefreshListener(this)
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.actionHome.setOnClickListener {
            addToStack(viewModel.defaultLocation())
        }
        binding.actionAdd.setOnClickListener {
            showCreateDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(this, Observer {
            showToast(it)
        })
        viewModel.hasAccessEvent.observe(viewLifecycleOwner, Observer { hasAccess ->
            if(hasAccess) {
                viewModel.fileTabsAddEvent.value = viewModel.defaultLocation()
                viewModel.hasPermission.set(true)
            } else {
                binding.actionAccess.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:" + activity?.packageName)
                    startActivityForResult(intent, REQUEST_READ_WRITE2)
                }
            }
        })
        viewModel.fileUpdateListEvent.observe(viewLifecycleOwner, Observer {
            onRefresh()
        })
        viewModel.deleteFileEvent.observe(viewLifecycleOwner, Observer { deletedFile ->
            //removeAfter(deletedFile)
        })
        viewModel.renameFileEvent.observe(viewLifecycleOwner, Observer { renamedFile ->
            //removeAfter(renamedFile)
        })
        viewModel.fileTabsAddEvent.observe(viewLifecycleOwner, Observer { path ->
            addToStack(path)
        })
        viewModel.fileTabsRemoveEvent.observe(viewLifecycleOwner, Observer { position ->
            binding.tabLayout.getTabAt(position)?.let {
                binding.tabLayout.removeTab(it)
            }
        })
        viewModel.fileTabsSelectEvent.observe(viewLifecycleOwner, Observer { position ->
            binding.tabLayout.post {
                binding.tabLayout.getTabAt(position)?.select()
            }
        })
        viewModel.observePreferences()
    }

    // region INTERNAL

    private fun showCreateDialog() {
        MaterialDialog(context!!).show {
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
            checkBoxPrompt(R.string.action_folder) {}
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_create, click = {
                val fileName = getInputField().text.toString()
                val isFolder = getCheckBoxPrompt().isChecked

                /*val parent = adapter.get(binding.tabLayout.selectedTabPosition)
                val child = parent.copy(
                    path = parent.path + "/$fileName",
                    isFolder = isFolder
                )
                viewModel.createFile(parent, child)*/
            })
        }
    }

    private fun addToStack(fileModel: FileModel) {
        viewModel.addToStack(binding.tabLayout.selectedTabPosition, fileModel)
        fragmentManager?.commit {
            replace(R.id.container_directories, FragmentDirectory())
            addTab(fileModel)
        }
    }

    private fun addTab(fileModel: FileModel) {
        val tab = binding.tabLayout.newTab()
        tab.text = fileModel.name
        tab.setCustomView(R.layout.item_tab_directory)

        fragmentManager?.commit {
            add(R.id.container_directories, FragmentDirectory())
            binding.tabLayout.addTab(tab)
            binding.tabLayout.post { tab.select() }
        }
    }

    // endregion INTERNAL
}