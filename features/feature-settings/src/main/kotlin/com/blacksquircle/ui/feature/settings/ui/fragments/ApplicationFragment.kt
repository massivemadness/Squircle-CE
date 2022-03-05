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
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.core.extensions.fullscreenMode
import com.blacksquircle.ui.core.extensions.navigate
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel

class ApplicationFragment : PreferenceFragmentCompat() {

    private val viewModel by activityViewModels<SettingsViewModel>()
    private val navController by lazy { findNavController() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_application, rootKey)

        findPreference<Preference>(SettingsManager.KEY_COLOR_SCHEME)
            ?.setOnPreferenceClickListener {
                navController.navigate(Screen.Themes, navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.nav_default_enter_anim)
                    .setExitAnim(R.anim.nav_default_exit_anim)
                    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                    .build()
                )
                true
            }
        findPreference<Preference>(SettingsManager.KEY_FULLSCREEN_MODE)
            ?.setOnPreferenceClickListener {
                activity?.window?.fullscreenMode(viewModel.fullscreenMode)
                true
            }
    }
}