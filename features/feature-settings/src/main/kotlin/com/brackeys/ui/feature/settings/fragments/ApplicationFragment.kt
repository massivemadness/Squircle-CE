/*
 * Copyright 2021 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.settings.fragments

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.brackeys.ui.data.storage.keyvalue.SettingsManager
import com.brackeys.ui.feature.settings.R
import com.brackeys.ui.feature.settings.viewmodel.SettingsViewModel

class ApplicationFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by activityViewModels()

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_application, rootKey)

        findPreference<Preference>(SettingsManager.KEY_COLOR_SCHEME)
            ?.setOnPreferenceClickListener {
                navController.navigate(R.id.themes_graph)
                true
            }
        findPreference<Preference>(SettingsManager.KEY_FULLSCREEN_MODE)
            ?.setOnPreferenceClickListener {
                if (viewModel.fullscreenMode) {
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                } else {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }
                true
            }
    }
}