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
import android.text.Html
import android.view.View
import androidx.lifecycle.Observer
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.presentation.base.fragments.DaggerPreferenceFragmentCompat
import com.lightteam.modpeide.presentation.settings.viewmodel.SettingsViewModel
import com.lightteam.modpeide.utils.commons.RawUtils
import javax.inject.Inject

class FragmentPreferences : DaggerPreferenceFragmentCompat() {

    companion object {

        //Headers
        private const val KEY_ROOT = "KEY_HEADER_ROOT"
        private const val KEY_APPLICATION = "KEY_HEADER_APPLICATION"
        private const val KEY_EDITOR = "KEY_HEADER_EDITOR"
        private const val KEY_CODE_STYLE = "KEY_HEADER_CODE_STYLE"
        private const val KEY_FILES = "KEY_HEADER_FILES"
        private const val KEY_ABOUT = "KEY_HEADER_ABOUT"

        //Sensitive
        //private const val KEY_THEME_RESOURCE = PreferenceHandler.KEY_THEME
        private const val KEY_FONT_TYPE = PreferenceHandler.KEY_FONT_TYPE
        private const val KEY_MAX_TABS_COUNT = PreferenceHandler.KEY_MAX_TABS_COUNT
        private const val KEY_INSERT_QUOTE = PreferenceHandler.KEY_INSERT_QUOTE
        private const val KEY_ABOUT_AND_CHANGELOG = "ABOUT_AND_CHANGELOG"
        private const val KEY_PRIVACY_POLICY = "PRIVACY_POLICY"
    }

    @Inject
    lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_headers, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when(preference?.key) {
            KEY_ROOT -> setPreferencesFromResource(R.xml.preference_headers, KEY_ROOT)
            KEY_APPLICATION -> setPreferencesFromResource(R.xml.preference_application, KEY_APPLICATION)
            KEY_EDITOR -> setPreferencesFromResource(R.xml.preference_editor, KEY_EDITOR)
            KEY_CODE_STYLE -> setPreferencesFromResource(R.xml.preference_code_style, KEY_CODE_STYLE)
            KEY_FILES -> setPreferencesFromResource(R.xml.preference_files, KEY_FILES)
            KEY_ABOUT -> {
                setPreferencesFromResource(R.xml.preference_about, KEY_ABOUT)

                val changelog = findPreference<Preference>(KEY_ABOUT_AND_CHANGELOG)
                changelog?.setOnPreferenceClickListener { showChangelogDialog() }

                val privacy = findPreference<Preference>(KEY_PRIVACY_POLICY)
                privacy?.setOnPreferenceClickListener { showPrivacyPolicyDialog() }
            }
        }
        activity?.title = preferenceScreen.title
        return super.onPreferenceTreeClick(preference)
    }

    override fun setPreferencesFromResource(preferencesResId: Int, key: String?) {
        super.setPreferencesFromResource(preferencesResId, key)
        if(viewModel.isUltimate()) { //available only for ultimate edition
            when(key) {
                KEY_APPLICATION -> {
                    //findPreference<Preference>(KEY_THEME_RESOURCE)?.isEnabled = true
                }
                KEY_EDITOR -> {
                    findPreference<Preference>(KEY_FONT_TYPE)?.isEnabled = true
                    findPreference<Preference>(KEY_MAX_TABS_COUNT)?.isEnabled = true
                }
                KEY_CODE_STYLE -> {
                    findPreference<Preference>(KEY_INSERT_QUOTE)?.isEnabled = true
                }
                KEY_ABOUT -> {
                    findPreference<Preference>(KEY_ABOUT_AND_CHANGELOG)
                        ?.setTitle(R.string.pref_about_ultimate_title)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.backEvent.observe(this.viewLifecycleOwner, Observer {
            if(preferenceScreen.key != KEY_ROOT) {
                val root = Preference(activity)
                root.key = KEY_ROOT
                onPreferenceTreeClick(root)
            } else {
                activity?.finish()
            }
        })
    }

    // region DIALOGS

    private fun showChangelogDialog(): Boolean {
        MaterialDialog(activity!!).show {
            title(R.string.dialog_title_changelog)
            message(text = Html.fromHtml(
                RawUtils.getRawFileText(activity!!, R.raw.changelog)
            ))
            negativeButton(R.string.action_close)
        }
        return true
    }

    private fun showPrivacyPolicyDialog(): Boolean {
        MaterialDialog(activity!!).show {
            title(R.string.dialog_title_privacy_policy)
            message(text = Html.fromHtml(
                RawUtils.getRawFileText(activity!!, R.raw.privacy_policy)
            ))
            negativeButton(R.string.action_close)
        }
        return true
    }

    // endregion DIALOGS
}