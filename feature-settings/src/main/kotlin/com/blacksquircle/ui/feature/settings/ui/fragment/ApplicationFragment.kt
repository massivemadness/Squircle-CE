/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.ui.delegate.viewBinding
import com.blacksquircle.ui.core.ui.extensions.fullscreenMode
import com.blacksquircle.ui.core.ui.extensions.navigate
import com.blacksquircle.ui.core.ui.navigation.Screen
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.databinding.FragmentPreferenceBinding
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel

class ApplicationFragment : PreferenceFragmentCompat() {

    private val viewModel by hiltNavGraphViewModels<SettingsViewModel>(R.id.settings_graph)
    private val binding by viewBinding(FragmentPreferenceBinding::bind)
    private val navController by lazy { findNavController() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_application, rootKey)

        findPreference<Preference>(SettingsManager.KEY_COLOR_SCHEME)
            ?.setOnPreferenceClickListener {
                navController.navigate(Screen.Themes)
                true
            }
        findPreference<Preference>(SettingsManager.KEY_FULLSCREEN_MODE)
            ?.setOnPreferenceClickListener {
                activity?.window?.fullscreenMode(viewModel.fullscreenMode)
                true
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_preference, container, false).also {
            (it as? ViewGroup)?.addView(
                super.onCreateView(inflater, container, savedInstanceState)
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.pref_header_application_title)
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }
}