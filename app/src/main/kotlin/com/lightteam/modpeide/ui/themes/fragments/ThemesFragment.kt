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
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.feature.scheme.Theme
import com.lightteam.modpeide.databinding.FragmentThemesBinding
import com.lightteam.modpeide.ui.base.dialogs.DialogStore
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.themes.adapters.ThemeAdapter
import com.lightteam.modpeide.ui.themes.viewmodel.ThemesViewModel
import com.lightteam.modpeide.utils.extensions.isUltimate
import javax.inject.Inject

class ThemesFragment : BaseFragment(), ThemeAdapter.ThemeInteractor {

    @Inject
    lateinit var viewModel: ThemesViewModel

    private lateinit var navController: NavController
    private lateinit var binding: FragmentThemesBinding
    private lateinit var adapter: ThemeAdapter

    override fun layoutId(): Int = R.layout.fragment_themes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentThemesBinding.bind(view)
        observeViewModel()

        navController = findNavController()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = ThemeAdapter(this).also {
            adapter = it
        }

        binding.actionAdd.setOnClickListener {
            if (requireContext().isUltimate()) {
                val destination = ThemesFragmentDirections.toNewThemeFragment(null)
                navController.navigate(destination)
            } else {
                DialogStore.Builder(requireContext()).show()
            }
        }

        viewModel.fetchThemes()
    }

    override fun selectTheme(theme: Theme) {
        if (theme.isPaid && !requireContext().isUltimate()) {
            DialogStore.Builder(requireContext()).show()
        } else {
            viewModel.selectTheme(theme)
        }
    }

    override fun removeTheme(theme: Theme) {
        if (theme.isExternal) {
            viewModel.removeTheme(theme)
        }
    }

    override fun editTheme(theme: Theme) {
        if (theme.isExternal) {
            val destination = ThemesFragmentDirections.toNewThemeFragment(theme.uuid)
            navController.navigate(destination)
        }
    }

    override fun showInfo(theme: Theme) {
        showToast(text = theme.description)
    }

    private fun observeViewModel() {
        viewModel.themesEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.selectEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = String.format(getString(R.string.message_selected), it))
        })
        viewModel.removeEvent.observe(viewLifecycleOwner, Observer {
            showToast(text = String.format(getString(R.string.message_theme_removed), it))
        })
    }
}