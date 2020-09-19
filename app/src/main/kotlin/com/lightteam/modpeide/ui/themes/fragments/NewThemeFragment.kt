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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.toColorInt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.model.theme.Meta
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.databinding.FragmentNewThemeBinding
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.themes.adapters.PropertyAdapter
import com.lightteam.modpeide.ui.themes.adapters.item.PropertyItem
import com.lightteam.modpeide.ui.themes.viewmodel.ThemesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewThemeFragment : BaseFragment(R.layout.fragment_new_theme), OnItemClickListener<PropertyItem> {

    private val viewModel: ThemesViewModel by viewModels()
    private val navArgs: NewThemeFragmentArgs by navArgs()

    private val importThemeContract: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                viewModel.importTheme(inputStream)
            }
        }

    private lateinit var navController: NavController
    private lateinit var binding: FragmentNewThemeBinding
    private lateinit var adapter: PropertyAdapter
    private lateinit var meta: Meta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (savedInstanceState == null) {
            viewModel.fetchProperties(navArgs.uuid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewThemeBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.adapter = PropertyAdapter(this)
            .also { adapter = it }

        binding.actionSave.setOnClickListener {
            viewModel.createTheme(meta, adapter.currentList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.metaEvent.value = meta
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_new_theme, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_import -> importThemeContract.launch(arrayOf("application/json"))
        }
        return super.onOptionsItemSelected(item)
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
        viewModel.toastEvent.observe(viewLifecycleOwner, {
            showToast(it)
        })
        viewModel.validationEvent.observe(viewLifecycleOwner, {
            binding.actionSave.isEnabled = it
        })
        viewModel.createEvent.observe(viewLifecycleOwner, {
            showToast(text = getString(R.string.message_new_theme_available, it))
            navController.navigateUp()
        })
        viewModel.metaEvent.observe(viewLifecycleOwner, {
            meta = it

            binding.textInputThemeName.doAfterTextChanged { updateMeta() }
            binding.textInputThemeAuthor.doAfterTextChanged { updateMeta() }
            binding.textInputThemeDescription.doAfterTextChanged { updateMeta() }

            binding.textInputThemeName.setText(it.name)
            binding.textInputThemeAuthor.setText(it.author)
            binding.textInputThemeDescription.setText(it.description)
        })
        viewModel.propertiesEvent.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    private fun updateMeta() {
        meta = meta.copy(
            name = binding.textInputThemeName.text.toString(),
            author = binding.textInputThemeAuthor.text.toString(),
            description = binding.textInputThemeDescription.text.toString()
        )
        viewModel.validateInput(meta.name, meta.author, meta.description)
    }
}