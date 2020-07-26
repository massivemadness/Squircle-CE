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
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.databinding.FragmentNewPresetBinding
import com.lightteam.modpeide.domain.model.preset.PresetModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.presets.viewmodel.PresetsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NewPresetFragment : BaseFragment(R.layout.fragment_new_preset) {

    private val viewModel: PresetsViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentNewPresetBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewPresetBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.textInputPresetName.doAfterTextChanged {
            viewModel.validateInput(
                presetName = it.toString(),
                presetChars = binding.textInputPresetChars.text.toString()
            )
        }
        binding.textInputPresetChars.doAfterTextChanged {
            viewModel.validateInput(
                presetName = binding.textInputPresetName.text.toString(),
                presetChars = it.toString()
            )
        }
        binding.actionSave.setOnClickListener {
            val presetModel = PresetModel(
                uuid = UUID.randomUUID().toString(),
                name = binding.textInputPresetName.text.toString(),
                isExternal = true,
                keys = binding.textInputPresetChars.text.toString().split("")
            )
            viewModel.insertPreset(presetModel)
        }
    }

    private fun observeViewModel() {
        viewModel.validationEvent.observe(viewLifecycleOwner, Observer {
            binding.actionSave.isEnabled = it
        })
        viewModel.insertEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = getString(R.string.message_new_preset_available, it))
            navController.navigateUp()
        })
    }
}