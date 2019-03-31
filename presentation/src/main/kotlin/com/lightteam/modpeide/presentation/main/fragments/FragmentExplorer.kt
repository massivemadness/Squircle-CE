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
import android.os.Environment
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.FileConverter
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.databinding.FragmentExplorerBinding
import com.lightteam.modpeide.presentation.main.activities.MainActivity.Companion.REQUEST_READ_WRITE
import com.lightteam.modpeide.presentation.main.activities.MainActivity.Companion.REQUEST_READ_WRITE2
import com.lightteam.modpeide.presentation.main.adapters.DirectoryAdapter
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class FragmentExplorer : DaggerFragment() {

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var adapter: DirectoryAdapter

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
        binding.dirViewPager.offscreenPageLimit = 1
        binding.dirViewPager.adapter = adapter
        binding.dirLayout.setupWithViewPager(binding.dirViewPager)
    }

    // region MENU

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explorer, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean { return onQueryTextChange(query) }
            override fun onQueryTextChange(newText: String): Boolean { return viewModel.filterFiles(newText) }
        })
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

    private fun setupListeners() {
        binding.actionAccess.setOnClickListener {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_READ_WRITE
            )
        }
    }

    private fun setupObservers() {
        viewModel.hasAccessEvent.observe(this.viewLifecycleOwner, Observer { hasAccess ->
            if(hasAccess) {
                viewModel.hasPermission.set(true)
                adapter.add(FileConverter.toModel(Environment.getExternalStorageDirectory().absoluteFile))
                invalidateTabs()
            } else {
                binding.actionAccess.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.parse("package:" + activity?.packageName)
                    intent.data = uri
                    startActivityForResult(intent, REQUEST_READ_WRITE2)
                }
            }
        })
    }

    private fun invalidateTabs() {
        for (i in 0 until adapter.count) {
            val tab = binding.dirLayout.getTabAt(i)
            tab?.setCustomView(R.layout.item_tab_directory)
        }
    }
}