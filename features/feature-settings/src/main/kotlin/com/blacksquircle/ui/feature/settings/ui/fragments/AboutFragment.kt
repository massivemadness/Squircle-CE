/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.core.ui.extensions.getColorAttr
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.data.utils.applicationName
import com.blacksquircle.ui.feature.settings.data.utils.versionCode
import com.blacksquircle.ui.feature.settings.data.utils.versionName

class AboutFragment : PreferenceFragmentCompat() {

    private val navController by lazy { findNavController() }
    private var counter = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(requireContext().getColorAttr(android.R.attr.colorBackground))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val changelog = findPreference<Preference>(KEY_ABOUT)
        changelog?.title = requireContext().applicationName
        changelog?.summary = getString(
            R.string.pref_about_summary,
            requireContext().versionName,
            requireContext().versionCode
        )
        changelog?.setOnPreferenceClickListener {
            if (counter < 10) {
                counter++
            } else {
                navController.navigate(R.id.changeLogFragment)
            }
            true
        }
    }

    companion object {
        private const val KEY_ABOUT = "ABOUT"
    }
}