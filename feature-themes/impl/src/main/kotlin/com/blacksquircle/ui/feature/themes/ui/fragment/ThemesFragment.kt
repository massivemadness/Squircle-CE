/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.themes.ui.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.CreateFileContract
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.*
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.utils.GridSpacingItemDecoration
import com.blacksquircle.ui.feature.themes.data.utils.readAssetFileText
import com.blacksquircle.ui.feature.themes.databinding.FragmentThemesBinding
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.ui.adapter.ThemeAdapter
import com.blacksquircle.ui.feature.themes.ui.mvi.ThemeIntent
import com.blacksquircle.ui.feature.themes.ui.mvi.ThemesViewState
import com.blacksquircle.ui.feature.themes.ui.navigation.ThemesScreen
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ThemesFragment : Fragment(R.layout.fragment_themes) {

    private val viewModel by hiltNavGraphViewModels<ThemesViewModel>(R.id.themes_graph)
    private val binding by viewBinding(FragmentThemesBinding::bind)
    private val navController by lazy { findNavController() }
    private val exportThemeContract = CreateFileContract(this) { result ->
        when (result) {
            is ContractResult.Success -> {
                viewModel.obtainEvent(ThemeIntent.ExportTheme(themeModel, result.uri))
            }
            is ContractResult.Canceled -> Unit
        }
    }

    private lateinit var adapter: ThemeAdapter
    private lateinit var themeModel: ThemeModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFadeTransition(binding.recyclerView, R.id.toolbar)
        postponeEnterTransition(view)
        observeViewModel()

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.toolbar.updatePadding(top = top)
            binding.root.updatePadding(bottom = bottom)
        }

        val gridLayoutManager = binding.recyclerView.layoutManager as GridLayoutManager
        GridSpacingItemDecoration(8, gridLayoutManager.spanCount).let {
            binding.recyclerView.addItemDecoration(it)
        }
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = ThemeAdapter(object : ThemeAdapter.Actions {
            override fun selectTheme(themeModel: ThemeModel) {
                viewModel.obtainEvent(ThemeIntent.SelectTheme(themeModel))
            }
            override fun exportTheme(themeModel: ThemeModel) {
                this@ThemesFragment.themeModel = themeModel
                exportThemeContract.launch(themeModel.name + ".json", CreateFileContract.JSON)
            }
            override fun editTheme(themeModel: ThemeModel) {
                navController.navigate(ThemesScreen.Update(themeModel.uuid))
            }
            override fun removeTheme(themeModel: ThemeModel) {
                viewModel.obtainEvent(ThemeIntent.RemoveTheme(themeModel))
            }
            override fun showInfo(themeModel: ThemeModel) {
                context?.showToast(text = themeModel.description)
            }
        }).also {
            adapter = it
        }

        binding.actionAdd.setOnClickListener {
            navController.navigate(ThemesScreen.Create)
        }

        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.toolbar.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_themes, menu)

                    val searchItem = menu.findItem(R.id.action_search)
                    val searchView = searchItem?.actionView as? SearchView

                    val state = viewModel.themesState.value
                    if (state.query.isNotEmpty()) {
                        searchItem?.expandActionView()
                        searchView?.setQuery(state.query, false)
                    }

                    searchView?.debounce(viewLifecycleOwner.lifecycleScope) {
                        viewModel.obtainEvent(ThemeIntent.SearchThemes(it))
                    }

                    val spinnerItem = menu.findItem(R.id.spinner)
                    val spinnerView = spinnerItem?.actionView as? AppCompatSpinner

                    spinnerView?.adapter = ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.preview_names,
                        android.R.layout.simple_spinner_dropdown_item,
                    )
                    spinnerView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) = Unit
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val path = resources.getStringArray(R.array.preview_paths)[position]
                            val extension = resources.getStringArray(R.array.preview_extensions)[position]
                            adapter.codeSnippet = requireContext().readAssetFileText(path) to extension
                        }
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return false
                }
            },
            viewLifecycleOwner,
        )
    }

    private fun observeViewModel() {
        viewModel.themesState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is ThemesViewState.Empty -> {
                        binding.loadingBar.isVisible = false
                        binding.emptyView.isVisible = true
                        binding.recyclerView.isInvisible = true
                    }
                    is ThemesViewState.Data -> {
                        binding.loadingBar.isVisible = false
                        binding.emptyView.isVisible = false
                        binding.recyclerView.isInvisible = false
                        adapter.submitList(state.themes)
                    }
                    is ThemesViewState.Loading -> {
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
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}