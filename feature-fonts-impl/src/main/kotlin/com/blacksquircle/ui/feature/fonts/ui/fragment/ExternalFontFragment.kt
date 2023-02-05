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

package com.blacksquircle.ui.feature.fonts.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.applySystemWindowInsets
import com.blacksquircle.ui.core.ui.extensions.showToast
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.databinding.FragmentExternalFontBinding
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.ui.viewmodel.FontsViewModel
import com.blacksquircle.ui.feature.fonts.ui.viewstate.ExternalFontViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ExternalFontFragment : Fragment(R.layout.fragment_external_font) {

    private val viewModel by hiltNavGraphViewModels<FontsViewModel>(R.id.fonts_graph)
    private val binding by viewBinding(FragmentExternalFontBinding::bind)
    private val navController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.toolbar.updatePadding(top = top)
            binding.root.updatePadding(bottom = bottom)
        }

        binding.textInputFontName.doAfterTextChanged { validateInput() }
        binding.textInputFontPath.doAfterTextChanged { validateInput() }

        binding.actionSave.setOnClickListener {
            val fontModel = FontModel(
                fontName = binding.textInputFontName.text.toString().trim(),
                fontPath = binding.textInputFontPath.text.toString().trim(),
                supportLigatures = binding.supportLigatures.isChecked,
                isExternal = true,
            )
            viewModel.createFont(fontModel)
        }
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.externalFontState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    ExternalFontViewState.Valid -> binding.actionSave.isEnabled = true
                    ExternalFontViewState.Invalid -> binding.actionSave.isEnabled = false
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.PopBackStack -> navController.popBackStack()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun validateInput() {
        viewModel.validateInput(
            fontName = binding.textInputFontName.text.toString(),
            fontPath = binding.textInputFontPath.text.toString(),
        )
    }
}