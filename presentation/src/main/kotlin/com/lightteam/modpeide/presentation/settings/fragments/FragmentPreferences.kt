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

package com.lightteam.modpeide.presentation.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.preference.Preference
import com.lightteam.modpeide.R
import com.lightteam.modpeide.presentation.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.presentation.settings.viewmodel.SettingsViewModel
import javax.inject.Inject

class FragmentPreferences : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_APPLICATION = "KEY_HEADER_APPLICATION"
        private const val KEY_EDITOR = "KEY_HEADER_EDITOR"
        private const val KEY_CODE_STYLE = "KEY_HEADER_CODE_STYLE"
        private const val KEY_FILES = "KEY_HEADER_FILES"
        private const val KEY_ABOUT = "KEY_HEADER_ABOUT"
    }

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference?.key) {
            KEY_APPLICATION -> {
                setPreferencesFromResource(R.xml.preferences, KEY_APPLICATION)
            }
            KEY_EDITOR -> {
                setPreferencesFromResource(R.xml.preferences, KEY_EDITOR)
            }
            KEY_CODE_STYLE -> {
                setPreferencesFromResource(R.xml.preferences, KEY_CODE_STYLE)
            }
            KEY_FILES -> {
                setPreferencesFromResource(R.xml.preferences, KEY_FILES)
            }
            KEY_ABOUT -> {
                setPreferencesFromResource(R.xml.preferences, KEY_ABOUT)
            }
            null -> {
                setPreferencesFromResource(R.xml.preferences, null)
            }
        }
        /*activity?.title = if(preferenceScreen.hasKey()) {
            preferenceScreen.title
        } else {
            getString(R.string.label_settings)
        }*/
        return true
    }

    private fun setupObservers() {
        viewModel.backEvent.observe(this.viewLifecycleOwner, Observer {
            if(preferenceScreen.key != null) {
                onPreferenceTreeClick(null)
            } else {
                activity?.finish()
            }
        })
    }
}