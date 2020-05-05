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
import android.widget.TextView
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.lightteam.modpeide.BuildConfig
import com.lightteam.modpeide.R
import com.lightteam.modpeide.ui.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.utils.extensions.asHtml
import com.lightteam.modpeide.utils.extensions.getRawFileText
import com.lightteam.modpeide.utils.extensions.isUltimate

class AboutFragment : DaggerPreferenceFragmentCompat() {

    companion object {
        private const val KEY_ABOUT_AND_CHANGELOG = "ABOUT_AND_CHANGELOG"
        private const val KEY_PRIVACY_POLICY = "PRIVACY_POLICY"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_about, rootKey)

        val isUltimate = requireContext().isUltimate()

        val changelog = findPreference<Preference>(KEY_ABOUT_AND_CHANGELOG)
        changelog?.setOnPreferenceClickListener { showChangelogDialog() }
        changelog?.summary = String.format(
            getString(R.string.pref_about_summary),
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        )
        if (isUltimate) {
            changelog?.setTitle(R.string.pref_about_ultimate_title)
        }

        val privacy = findPreference<Preference>(KEY_PRIVACY_POLICY)
        privacy?.setOnPreferenceClickListener { showPrivacyPolicyDialog() }
    }

    // region DIALOGS

    private fun showChangelogDialog(): Boolean {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_changelog)
            message(text = context.getRawFileText(R.raw.changelog).asHtml())
            findViewById<TextView>(R.id.md_text_message).textSize = 14f
            negativeButton(R.string.action_close)
        }
        return true
    }

    private fun showPrivacyPolicyDialog(): Boolean {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_privacy_policy)
            message(text = context.getRawFileText(R.raw.privacy_policy).asHtml())
            findViewById<TextView>(R.id.md_text_message).textSize = 14f
            negativeButton(R.string.action_close)
        }
        return true
    }

    // endregion DIALOGS
}