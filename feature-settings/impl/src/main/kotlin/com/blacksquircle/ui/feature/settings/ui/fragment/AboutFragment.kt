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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.core.delegate.viewBinding
import com.blacksquircle.ui.core.extensions.applySystemWindowInsets
import com.blacksquircle.ui.core.extensions.navigate
import com.blacksquircle.ui.core.extensions.postponeEnterTransition
import com.blacksquircle.ui.core.extensions.setFadeTransition
import com.blacksquircle.ui.feature.settings.BuildConfig
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.data.utils.applicationName
import com.blacksquircle.ui.feature.settings.data.utils.versionCode
import com.blacksquircle.ui.feature.settings.data.utils.versionName
import com.blacksquircle.ui.feature.settings.ui.navigation.SettingsScreen
import com.blacksquircle.ui.uikit.databinding.LayoutPreferenceBinding
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class AboutFragment : PreferenceFragmentCompat() {

    private val binding by viewBinding(LayoutPreferenceBinding::bind)
    private val navController by lazy { findNavController() }

    private var counter = 1

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val changelog = findPreference<Preference>(KEY_ABOUT)
        changelog?.title = requireContext().applicationName
        changelog?.summary = getString(
            R.string.pref_about_summary,
            versionName(),
            requireContext().versionCode,
        )
        changelog?.setOnPreferenceClickListener {
            if (counter < 10) {
                counter++
            } else {
                navController.navigate(SettingsScreen.ChangeLog)
            }
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

        binding.toolbar.title = getString(R.string.pref_header_about_title)
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    private fun versionName(): String {
        return if (BuildConfig.DEBUG) {
            requireContext().versionName + getString(R.string.debug_suffix)
        } else {
            requireContext().versionName
        }
    }

    companion object {
        private const val KEY_ABOUT = "ABOUT"
    }
}