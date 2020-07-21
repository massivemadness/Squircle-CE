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

package com.lightteam.modpeide.ui.fonts.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentExternalFontBinding
import com.lightteam.modpeide.domain.model.font.FontModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.fonts.viewmodel.FontsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExternalFontFragment : BaseFragment(R.layout.fragment_external_font) {

    private val viewModel: FontsViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentExternalFontBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExternalFontBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.textInputFontName.doAfterTextChanged {
            viewModel.validateInput(
                it.toString(),
                binding.textInputFontPath.text.toString()
            )
        }
        binding.textInputFontPath.doAfterTextChanged {
            viewModel.validateInput(
                binding.textInputFontName.text.toString(),
                it.toString()
            )
        }
        binding.actionAdd.setOnClickListener {
            val fontModel = FontModel(
                fontName = binding.textInputFontName.text.toString().trim(),
                fontPath = binding.textInputFontPath.text.toString().trim(),
                supportLigatures = binding.supportLigatures.isChecked,
                isExternal = true,
                isPaid = true
            )
            viewModel.insertFont(fontModel)
        }
    }

    private fun observeViewModel() {
        viewModel.validationEvent.observe(viewLifecycleOwner, Observer {
            binding.actionAdd.isEnabled = it
        })
        viewModel.insertEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = getString(R.string.message_new_font_available, it))
            navController.navigateUp()
        })
    }
}