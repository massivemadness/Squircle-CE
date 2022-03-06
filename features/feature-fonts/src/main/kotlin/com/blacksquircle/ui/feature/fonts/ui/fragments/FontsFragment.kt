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

package com.blacksquircle.ui.feature.fonts.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.debounce
import com.blacksquircle.ui.core.ui.extensions.navigate
import com.blacksquircle.ui.core.ui.extensions.showToast
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.databinding.FragmentFontsBinding
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.ui.adapters.FontAdapter
import com.blacksquircle.ui.feature.fonts.ui.navigation.FontsScreen
import com.blacksquircle.ui.feature.fonts.ui.viewmodel.FontsViewModel
import com.blacksquircle.ui.feature.fonts.ui.viewstate.FontsViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FontsFragment : Fragment(R.layout.fragment_fonts) {

    private val viewModel by activityViewModels<FontsViewModel>()
    private val binding by viewBinding(FragmentFontsBinding::bind)
    private val navController by lazy { findNavController() }

    private lateinit var adapter: FontAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).let {
            binding.recyclerView.addItemDecoration(it)
        }
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = FontAdapter(object : FontAdapter.Actions {
            override fun selectFont(fontModel: FontModel) = viewModel.selectFont(fontModel)
            override fun removeFont(fontModel: FontModel) = viewModel.removeFont(fontModel)
        }).also {
            adapter = it
        }

        binding.actionAdd.setOnClickListener {
            navController.navigate(FontsScreen.Create)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fonts, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        val state = viewModel.fontsState.value
        if (state.query.isNotEmpty()) {
            searchItem?.expandActionView()
            searchView?.setQuery(state.query, false)
        }

        searchView?.debounce(viewLifecycleOwner.lifecycleScope) {
            viewModel.fetchFonts(it)
        }
    }

    private fun observeViewModel() {
        viewModel.fontsState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is FontsViewState.Empty -> {
                        binding.loadingBar.isVisible = false
                        binding.emptyView.isVisible = true
                        binding.recyclerView.isInvisible = true
                    }
                    is FontsViewState.Data -> {
                        binding.loadingBar.isVisible = false
                        binding.emptyView.isVisible = false
                        binding.recyclerView.isInvisible = false
                        adapter.submitList(state.fonts)
                    }
                    FontsViewState.Loading -> {
                        binding.loadingBar.isVisible = true
                        binding.emptyView.isVisible = false
                        binding.recyclerView.isInvisible = true
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    else -> Unit
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}