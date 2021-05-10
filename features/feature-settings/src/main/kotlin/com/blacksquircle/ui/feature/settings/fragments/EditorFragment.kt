/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.settings.fragments

import android.os.Bundle
import androidx.navigation.NavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.utils.delegate.navController

class EditorFragment : PreferenceFragmentCompat() {

    private val navController: NavController by navController()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_editor, rootKey)

        findPreference<Preference>(KEY_FONT_TYPE)?.setOnPreferenceClickListener {
            navController.navigate(R.id.fonts_graph)
            true
        }
        findPreference<Preference>(KEY_KEYBOARD_PRESET)?.setOnPreferenceClickListener {
            navController.navigate(R.id.presetDialog)
            true
        }
    }

    companion object {
        private const val KEY_FONT_TYPE = SettingsManager.KEY_FONT_TYPE
        private const val KEY_KEYBOARD_PRESET = SettingsManager.KEY_KEYBOARD_PRESET
    }
}