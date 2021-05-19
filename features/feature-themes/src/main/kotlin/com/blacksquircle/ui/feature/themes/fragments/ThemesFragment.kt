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

package com.blacksquircle.ui.feature.themes.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.utils.MDUtil.getStringArray
import com.blacksquircle.ui.domain.model.themes.ThemeModel
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.adapters.ThemeAdapter
import com.blacksquircle.ui.feature.themes.databinding.FragmentThemesBinding
import com.blacksquircle.ui.feature.themes.utils.GridSpacingItemDecoration
import com.blacksquircle.ui.feature.themes.utils.readAssetFileText
import com.blacksquircle.ui.feature.themes.viewmodel.ThemesViewModel
import com.blacksquircle.ui.utils.delegate.navController
import com.blacksquircle.ui.utils.delegate.viewBinding
import com.blacksquircle.ui.utils.extensions.checkStorageAccess
import com.blacksquircle.ui.utils.extensions.debounce
import com.blacksquircle.ui.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemesFragment : Fragment(R.layout.fragment_themes) {

    private val viewModel: ThemesViewModel by viewModels()
    private val binding: FragmentThemesBinding by viewBinding()
    private val navController: NavController by navController()

    private lateinit var adapter: ThemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        val gridLayoutManager = binding.recyclerView.layoutManager as GridLayoutManager
        val gridSpacingDecoration = GridSpacingItemDecoration(8, gridLayoutManager.spanCount)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(gridSpacingDecoration)
        binding.recyclerView.adapter = ThemeAdapter(object : ThemeAdapter.Actions {
            override fun selectTheme(themeModel: ThemeModel) = viewModel.selectTheme(themeModel)
            override fun exportTheme(themeModel: ThemeModel) {
                activity?.checkStorageAccess(
                    onSuccess = { viewModel.exportTheme(themeModel) },
                    onFailure = { context?.showToast(R.string.message_access_required) }
                )
            }
            override fun editTheme(themeModel: ThemeModel) {
                val bundle = bundleOf(NewThemeFragment.NAV_THEME_UUID to themeModel.uuid)
                navController.navigate(R.id.newThemeFragment, bundle)
            }
            override fun removeTheme(themeModel: ThemeModel) = viewModel.removeTheme(themeModel)
            override fun showInfo(themeModel: ThemeModel) {
                context?.showToast(text = themeModel.description)
            }
        }).also {
            adapter = it
        }

        binding.actionAdd.setOnClickListener {
            val bundle = bundleOf(NewThemeFragment.NAV_THEME_UUID to null)
            navController.navigate(R.id.newThemeFragment, bundle)
        }

        viewModel.fetchThemes()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_themes, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        if (viewModel.searchQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView?.setQuery(viewModel.searchQuery, false)
        }

        val spinnerItem = menu.findItem(R.id.spinner)
        val spinnerView = spinnerItem?.actionView as? AppCompatSpinner

        spinnerView?.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.language_names,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinnerView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val path = requireContext().getStringArray(R.array.language_paths)[position]
                val extension = requireContext().getStringArray(R.array.language_extensions)[position]
                adapter.codeSnippet = requireContext().readAssetFileText(path) to extension
            }
        }

        searchView?.debounce(viewLifecycleOwner.lifecycleScope) {
            viewModel.searchQuery = it
            viewModel.fetchThemes()
        }
    }

    private fun observeViewModel() {
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            context?.showToast(it)
        }
        viewModel.themesEvent.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyView.isVisible = it.isEmpty()
        }
        viewModel.selectEvent.observe(viewLifecycleOwner) {
            context?.showToast(text = getString(R.string.message_selected, it))
        }
        viewModel.exportEvent.observe(viewLifecycleOwner) {
            context?.showToast(
                text = getString(R.string.message_theme_exported, it),
                duration = Toast.LENGTH_LONG
            )
        }
        viewModel.removeEvent.observe(viewLifecycleOwner) {
            context?.showToast(text = getString(R.string.message_theme_removed, it))
        }
    }
}