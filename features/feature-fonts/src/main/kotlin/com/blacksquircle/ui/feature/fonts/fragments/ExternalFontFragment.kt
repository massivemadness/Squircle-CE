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
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.blacksquircle.ui.core.delegate.navController
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.databinding.FragmentExternalFontBinding
import com.blacksquircle.ui.feature.fonts.viewmodel.FontsViewModel
import com.blacksquircle.ui.feature.fonts.viewstate.ExternalFontViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ExternalFontFragment : Fragment(R.layout.fragment_external_font) {

    private val viewModel by activityViewModels<FontsViewModel>()
    private val binding by viewBinding(FragmentExternalFontBinding::bind)
    private val navController by navController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.textInputFontName.doAfterTextChanged {
            viewModel.validateInput(
                fontName = it.toString(),
                fontPath = binding.textInputFontPath.text.toString(),
            )
        }
        binding.textInputFontPath.doAfterTextChanged {
            viewModel.validateInput(
                fontName = binding.textInputFontName.text.toString(),
                fontPath = it.toString(),
            )
        }
        binding.actionSave.setOnClickListener {
            val fontModel = FontModel(
                fontName = binding.textInputFontName.text.toString().trim(),
                fontPath = binding.textInputFontPath.text.toString().trim(),
                supportLigatures = binding.supportLigatures.isChecked,
                isExternal = true
            )
            viewModel.createFont(fontModel)
        }
    }

    private fun observeViewModel() {
        viewModel.toastEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { context?.showToast(text = it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.popBackStackEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { navController.popBackStack() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.externalFontState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { state ->
                when (state) {
                    ExternalFontViewState.Valid -> binding.actionSave.isEnabled = true
                    ExternalFontViewState.Invalid -> binding.actionSave.isEnabled = false
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}