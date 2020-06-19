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

package com.lightteam.modpeide.ui.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.lightteam.modpeide.BuildConfig
import com.lightteam.modpeide.R
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.utils.extensions.isUltimate

class AboutFragment : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_ABOUT_AND_CHANGELOG = "ABOUT_AND_CHANGELOG"
        private const val KEY_PRIVACY_POLICY = "PRIVACY_POLICY"
    }

    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val changelog = findPreference<Preference>(KEY_ABOUT_AND_CHANGELOG)
        changelog?.setOnPreferenceClickListener {
            navController.navigate(R.id.changeLogDialog)
            true
        }

        if (isUltimate()) {
            changelog?.setTitle(R.string.pref_about_ultimate_title)
        }
        changelog?.summary = String.format(
            getString(R.string.pref_about_summary),
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )

        val privacy = findPreference<Preference>(KEY_PRIVACY_POLICY)
        privacy?.setOnPreferenceClickListener {
            navController.navigate(R.id.privacyPolicyDialog)
            true
        }
    }
}