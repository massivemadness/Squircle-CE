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
import android.view.View
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.contract.OpenFileContract
import com.blacksquircle.ui.core.extensions.navigateTo
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
internal class NewThemeFragment : Fragment() {

    private val viewModel by hiltNavGraphViewModels<ThemesViewModel>(R.id.themes_graph)
    // private val binding by viewBinding(FragmentNewThemeBinding::bind)
    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<NewThemeFragmentArgs>()
    private val openFileContract = OpenFileContract(this) { result ->
        /*when (result) {
            is ContractResult.Success -> viewModel.obtainEvent(ThemeIntent.ImportTheme(result.uri))
            is ContractResult.Canceled -> Unit
        }*/
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            // viewModel.obtainEvent(ThemeIntent.LoadProperties(navArgs.uuid))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        /*binding.textInputThemeName.doAfterTextChanged {
            viewModel.obtainEvent(ThemeIntent.ChangeName(it.toString()))
        }
        binding.textInputThemeAuthor.doAfterTextChanged {
            viewModel.obtainEvent(ThemeIntent.ChangeAuthor(it.toString()))
        }
        binding.textInputThemeDescription.doAfterTextChanged {
            viewModel.obtainEvent(ThemeIntent.ChangeDescription(it.toString()))
        }

        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.adapter = PropertyAdapter(object : OnItemClickListener<PropertyItem> {
            override fun onClick(item: PropertyItem) {
                val event = ThemeIntent.ChooseColor(item.propertyKey, item.propertyValue)
                viewModel.obtainEvent(event)
            }
        }).also {
            adapter = it
        }

        binding.actionSave.setOnClickListener {
            val meta = Meta(
                uuid = navArgs.uuid ?: UUID.randomUUID().toString(),
                name = binding.textInputThemeName.text.toString(),
                author = binding.textInputThemeAuthor.text.toString(),
                description = binding.textInputThemeDescription.text.toString(),
            )
            viewModel.obtainEvent(ThemeIntent.CreateTheme(meta, adapter.currentList))
        }*/

        /*binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.toolbar.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_new_theme, menu)
                }
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.action_import -> openFileContract.launch(OpenFileContract.JSON)
                    }
                    return true
                }
            },
            viewLifecycleOwner,
        )*/
    }

    private fun observeViewModel() {
        viewModel.newThemeState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    is NewThemeViewState.MetaData -> {
                        /*val name = binding.textInputThemeName.text.toString()
                        val author = binding.textInputThemeAuthor.text.toString()
                        val description = binding.textInputThemeDescription.text.toString()

                        if (name != state.meta.name) {
                            binding.textInputThemeName.setText(state.meta.name)
                        }
                        if (author != state.meta.author) {
                            binding.textInputThemeAuthor.setText(state.meta.author)
                        }
                        if (description != state.meta.description) {
                            binding.textInputThemeDescription.setText(state.meta.description)
                        }

                        val isNameValid = state.meta.name.trim().isValidFileName()
                        val isAuthorValid = state.meta.author.trim().isNotBlank()
                        val isDescriptionValid = state.meta.description.trim().isNotBlank()

                        binding.actionSave.isEnabled =
                            isNameValid && isAuthorValid && isDescriptionValid

                        adapter.submitList(state.properties)*/
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.Navigation -> navController.navigateTo(event.screen)
                    is ViewEvent.PopBackStack -> navController.popBackStack()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}