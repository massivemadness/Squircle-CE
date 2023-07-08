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

package com.blacksquircle.ui.feature.settings.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.updatePadding
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.*
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.theme.Theme
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel
import com.blacksquircle.ui.uikit.databinding.LayoutPreferenceBinding
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class ApplicationFragment : PreferenceFragmentCompat() {

    private val viewModel by hiltNavGraphViewModels<SettingsViewModel>(R.id.settings_graph)
    private val binding by viewBinding(LayoutPreferenceBinding::bind)
    private val navController by lazy { findNavController() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_application, rootKey)

        findPreference<ListPreference>(SettingsManager.KEY_THEME)?.run {
            isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            if (!isEnabled) {
                setSummary(R.string.message_app_theme_disclaimer)
            }
            setOnPreferenceChangeListener { _, theme ->
                Theme.of(theme as String).apply()
                true
            }
        }
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
        setFadeTransition(binding.root[1] as ViewGroup, R.id.toolbar)
        postponeEnterTransition(view)

        view.applySystemWindowInsets(true) { _, top, _, bottom ->
            binding.toolbar.updatePadding(top = top)
            binding.root[1].updatePadding(bottom = bottom)
        }

        binding.toolbar.title = getString(R.string.pref_header_application_title)
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }
}