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

package com.blacksquircle.ui.feature.shortcuts.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.get
import androidx.core.view.updatePadding
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.*
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.ui.navigation.ShortcutScreen
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutIntent
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import com.blacksquircle.ui.uikit.databinding.LayoutPreferenceBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.blacksquircle.ui.uikit.R as UiR

class ShortcutsFragment : PreferenceFragmentCompat() {

    private val viewModel by hiltNavGraphViewModels<ShortcutsViewModel>(R.id.shortcuts_graph)
    private val binding by viewBinding(LayoutPreferenceBinding::bind)
    private val navController by lazy { findNavController() }

    private val defaultMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_shortcuts, menu)
        }
        override fun onPrepareMenu(menu: Menu) = Unit
        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_restore -> viewModel.obtainEvent(ShortcutIntent.RestoreShortcuts)
            }
            return true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_keybindings, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(UiR.layout.layout_preference, container, false).also {
            (it as? ViewGroup)?.addView(
                super.onCreateView(inflater, container, savedInstanceState),
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFadeTransition(binding.root[1] as ViewGroup, UiR.id.toolbar)
        postponeEnterTransition(view)
        observeViewModel()

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.toolbar.updatePadding(top = top)
            binding.root[1].updatePadding(bottom = bottom)
        }

        binding.toolbar.title = getString(R.string.pref_header_keybindings_title)
        binding.toolbar.addMenuProvider(defaultMenuProvider, viewLifecycleOwner)
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.shortcuts.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { keybindings ->
                keybindings.forEach { model ->
                    val preference = findPreference<Preference>(model.shortcut.key)
                    preference?.setOnPreferenceClickListener {
                        navController.navigate(ShortcutScreen.Edit(model.shortcut.key))
                        true
                    }
                    preference?.summary = StringBuilder().apply {
                        if (model.key == ' ') {
                            append(getString(R.string.shortcut_none))
                        } else {
                            if (model.isCtrl) append(getString(R.string.keybinding_ctrl) + " + ")
                            if (model.isShift) append(getString(R.string.keybinding_shift) + " + ")
                            if (model.isAlt) append(getString(R.string.keybinding_alt) + " + ")
                            append(model.key)
                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.viewEvent.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
                    is ViewEvent.Toast -> context?.showToast(text = event.message)
                    is ViewEvent.Navigation -> navController.navigate(event.screen)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}