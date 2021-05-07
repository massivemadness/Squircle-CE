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

package com.blacksquircle.ui.feature.settings.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blacksquircle.ui.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.adapters.item.PreferenceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val headersEvent = MutableLiveData<List<PreferenceItem>>()

    var fullscreenMode: Boolean
        get() = settingsManager.fullScreenMode
        set(value) { settingsManager.fullScreenMode = value }
    var keyboardPreset: String
        get() = settingsManager.keyboardPreset
        set(value) { settingsManager.keyboardPreset = value }

    fun fetchHeaders() {
        headersEvent.value = listOf(
            PreferenceItem(
                R.string.pref_header_application_title,
                R.string.pref_header_application_summary,
                R.id.applicationFragment
            ),
            PreferenceItem(
                R.string.pref_header_editor_title,
                R.string.pref_header_editor_summary,
                R.id.editorFragment
            ),
            PreferenceItem(
                R.string.pref_header_codeStyle_title,
                R.string.pref_header_codeStyle_summary,
                R.id.codeStyleFragment
            ),
            PreferenceItem(
                R.string.pref_header_files_title,
                R.string.pref_header_files_summary,
                R.id.filesFragment
            ),
            PreferenceItem(
                R.string.pref_header_about_title,
                R.string.pref_header_about_summary,
                R.id.aboutFragment
            )
        )
    }

    fun resetKeyboardPreset() {
        settingsManager.remove(SettingsManager.KEY_KEYBOARD_PRESET)
    }
}