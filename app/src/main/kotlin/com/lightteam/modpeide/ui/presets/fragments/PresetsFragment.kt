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

package com.lightteam.modpeide.ui.presets.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentKeyboardPresetsBinding
import com.lightteam.modpeide.domain.model.preset.PresetModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.presets.adapters.PresetAdapter
import com.lightteam.modpeide.ui.presets.viewmodel.PresetsViewModel
import com.lightteam.modpeide.utils.extensions.isUltimate
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PresetsFragment : BaseFragment(R.layout.fragment_keyboard_presets), PresetAdapter.PresetInteractor {

    private val viewModel: PresetsViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentKeyboardPresetsBinding
    private lateinit var adapter: PresetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentKeyboardPresetsBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = PresetAdapter(this)
            .also { adapter = it }

        binding.actionAdd.setOnClickListener {
            if (isUltimate()) {
                val destination = PresetsFragmentDirections.toNewPresetFragment()
                navController.navigate(destination)
            } else {
                navController.navigate(R.id.storeDialog)
            }
        }

        viewModel.fetchPresets()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_presets, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        if (viewModel.searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(viewModel.searchQuery, false)
        }

        searchView
            .queryTextChangeEvents()
            .skipInitialValue()
            .debounce(200, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                viewModel.searchQuery = it.queryText.toString()
                viewModel.fetchPresets()
            }
            .disposeOnFragmentDestroyView()
    }

    override fun selectPreset(presetModel: PresetModel) {
        viewModel.selectPreset(presetModel)
    }

    override fun editPreset(presetModel: PresetModel) {
        if (presetModel.isExternal) {
            // viewModel.editPreset(presetModel)
        }
    }

    override fun removePreset(presetModel: PresetModel) {
        if (presetModel.isExternal) {
            // viewModel.removePreset(presetModel)
        }
    }

    private fun observeViewModel() {
        viewModel.presetsEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            binding.emptyView.isVisible = it.isEmpty()
        })
        viewModel.selectEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = getString(R.string.message_selected, it))
        })
        /*viewModel.removeEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = getString(R.string.message_preset_removed, it))
        })*/
    }
}