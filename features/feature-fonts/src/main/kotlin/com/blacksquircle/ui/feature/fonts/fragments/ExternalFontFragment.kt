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
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.databinding.FragmentExternalFontBinding
import com.blacksquircle.ui.feature.fonts.viewmodel.FontsViewModel
import com.blacksquircle.ui.utils.delegate.navController
import com.blacksquircle.ui.utils.delegate.viewBinding
import com.blacksquircle.ui.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExternalFontFragment : Fragment(R.layout.fragment_external_font) {

    private val viewModel: FontsViewModel by viewModels()
    private val binding: FragmentExternalFontBinding by viewBinding()
    private val navController: NavController by navController()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        binding.textInputFontName.doAfterTextChanged {
            viewModel.validateInput(
                fontName = it.toString(),
                fontPath = binding.textInputFontPath.text.toString()
            )
        }
        binding.textInputFontPath.doAfterTextChanged {
            viewModel.validateInput(
                fontName = binding.textInputFontName.text.toString(),
                fontPath = it.toString()
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
        viewModel.toastEvent.observe(viewLifecycleOwner) {
            context?.showToast(it)
        }
        viewModel.validationEvent.observe(viewLifecycleOwner) {
            binding.actionSave.isEnabled = it
        }
        viewModel.insertEvent.observe(viewLifecycleOwner) {
            context?.showToast(text = getString(R.string.message_new_font_available, it))
            navController.navigateUp()
        }
    }
}