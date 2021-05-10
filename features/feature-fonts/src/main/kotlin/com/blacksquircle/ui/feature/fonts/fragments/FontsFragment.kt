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

package com.blacksquircle.ui.feature.fonts.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.adapters.FontAdapter
import com.blacksquircle.ui.feature.fonts.databinding.FragmentFontsBinding
import com.blacksquircle.ui.feature.fonts.viewmodel.FontsViewModel
import com.blacksquircle.ui.utils.delegate.navController
import com.blacksquircle.ui.utils.delegate.viewBinding
import com.blacksquircle.ui.utils.extensions.debounce
import com.blacksquircle.ui.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FontsFragment : Fragment(R.layout.fragment_fonts) {

    private val viewModel: FontsViewModel by viewModels()
    private val binding: FragmentFontsBinding by viewBinding()
    private val navController: NavController by navController()

    private lateinit var adapter: FontAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        val itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDecoration)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = FontAdapter(object : FontAdapter.Actions {
            override fun selectFont(fontModel: FontModel) = viewModel.selectFont(fontModel)
            override fun removeFont(fontModel: FontModel) = viewModel.removeFont(fontModel)
        }).also {
            adapter = it
        }

        binding.actionAdd.setOnClickListener {
            navController.navigate(R.id.externalFontFragment)
        }

        viewModel.fetchFonts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fonts, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        if (viewModel.searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView?.setQuery(viewModel.searchQuery, false)
        }

        searchView?.debounce(viewLifecycleOwner.lifecycleScope) {
            viewModel.searchQuery = it
            viewModel.fetchFonts()
        }
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            context?.showToast(it)
        }
        viewModel.fontsEvent.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyView.isVisible = it.isEmpty()
        }
        viewModel.selectEvent.observe(viewLifecycleOwner) {
            context?.showToast(text = getString(R.string.message_selected, it))
        }
        viewModel.removeEvent.observe(viewLifecycleOwner) {
            context?.showToast(text = getString(R.string.message_font_removed, it))
        }
    }
}