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
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.databinding.FragmentExplorerBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.editor.activities.EditorActivity
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.utils.extensions.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FragmentExplorer : BaseFragment(), TabLayout.OnTabSelectedListener {

    @Inject
    lateinit var viewModel: ExplorerViewModel

    private lateinit var binding: FragmentExplorerBinding
    private lateinit var navController: NavController

    override fun layoutId(): Int = R.layout.fragment_explorer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        view.post { // to avoid exception :c
            navController = binding.navHost.findNavController()
        }
        observeViewModel()

        setSupportActionBar(binding.toolbar)
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.actionHome.setOnClickListener {
            val backStackCount = binding.navHost.fragment<NavHostFragment>().backStackEntryCount
            navController.popBackStack(backStackCount - 1)
            removeTab(backStackCount - 1)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val backStackCount = binding.navHost.fragment<NavHostFragment>().backStackEntryCount
            if (backStackCount > 1) {
                navController.popBackStack()
                removeTab(1)
            } else {
                (requireActivity() as EditorActivity).closeDrawers()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.tabLayout.removeOnTabSelectedListener(this)
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
                viewModel.searchFile(it.queryText)
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
        binding.tabLayout.post {
            navController.popBackStack(binding.tabLayout.tabCount - 1 - tab.position)
            removeTab(binding.tabLayout.tabCount - 1 - tab.position)
        }
    }

    // endregion TABS

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })
        viewModel.tabEvent.observe(viewLifecycleOwner, Observer {
            addTab(it)
        })
        viewModel.observePreferences()
    }

    private fun addTab(fileModel: FileModel) {
        binding.tabLayout.newTab(fileModel.name, R.layout.item_tab_directory) {
            it.select()
        }
    }

    private fun removeTab(n_times: Int) {
        binding.tabLayout.removeLast(n_times)
    }
}