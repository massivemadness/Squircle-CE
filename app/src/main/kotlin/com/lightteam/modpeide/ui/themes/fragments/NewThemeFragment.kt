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

package com.lightteam.modpeide.ui.themes.fragments

import android.os.Bundle
import android.view.View
import androidx.core.graphics.toColorInt
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.databinding.FragmentNewThemeBinding
import com.lightteam.modpeide.domain.feature.theme.Meta
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.themes.adapters.PropertyAdapter
import com.lightteam.modpeide.ui.themes.adapters.item.PropertyItem
import com.lightteam.modpeide.ui.themes.viewmodel.ThemesViewModel
import javax.inject.Inject

class NewThemeFragment : BaseFragment(), OnItemClickListener<PropertyItem> {

    @Inject
    lateinit var viewModel: ThemesViewModel

    private val args: NewThemeFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentNewThemeBinding
    private lateinit var adapter: PropertyAdapter
    private lateinit var meta: Meta

    override fun layoutId(): Int = R.layout.fragment_new_theme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewThemeBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.adapter = PropertyAdapter(this).also {
            adapter = it
        }
        binding.textInputThemeName.doAfterTextChanged {
            viewModel.validateInput(
                it.toString(),
                binding.textInputThemeAuthor.text.toString(),
                binding.textInputThemeDescription.text.toString()
            )
        }
        binding.textInputThemeAuthor.doAfterTextChanged {
            viewModel.validateInput(
                binding.textInputThemeName.text.toString(),
                it.toString(),
                binding.textInputThemeDescription.text.toString()
            )
        }
        binding.textInputThemeDescription.doAfterTextChanged {
            viewModel.validateInput(
                binding.textInputThemeName.text.toString(),
                binding.textInputThemeAuthor.text.toString(),
                it.toString()
            )
        }

        binding.actionSaveTheme.setOnClickListener {
            val meta = this.meta.copy(
                name = binding.textInputThemeName.text.toString(),
                author = binding.textInputThemeAuthor.text.toString(),
                description = binding.textInputThemeDescription.text.toString()
            )
            viewModel.insertTheme(meta, adapter.currentList)
        }

        viewModel.fetchProperties(args.uuid)
    }

    override fun onClick(item: PropertyItem) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_color_picker)
            colorChooser(
                colors = ColorPalette.Primary,
                subColors = ColorPalette.PrimarySub,
                initialSelection = item.propertyValue.toColorInt(),
                allowCustomArgb = true,
                showAlphaSelector = false
            ) { _, color ->
                val index = adapter.currentList.indexOf(item)
                adapter.currentList[index].propertyValue = color.toHexString()
                adapter.notifyItemChanged(index)
            }
            positiveButton(R.string.action_select)
            negativeButton(R.string.action_cancel)
        }
    }

    private fun observeViewModel() {
        viewModel.validationEvent.observe(viewLifecycleOwner, Observer {
            binding.actionSaveTheme.isEnabled = it
        })
        viewModel.insertEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = String.format(getString(R.string.message_new_theme_available), it))
            navController.navigateUp()
        })
        viewModel.metaEvent.observe(viewLifecycleOwner, Observer {
            meta = it
            binding.textInputThemeName.setText(it.name)
            binding.textInputThemeAuthor.setText(it.author)
            binding.textInputThemeDescription.setText(it.description)
        })
        viewModel.propertiesEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}