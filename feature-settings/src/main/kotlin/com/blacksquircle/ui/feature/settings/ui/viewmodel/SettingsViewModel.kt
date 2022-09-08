/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.data.converter.ReleaseConverter
import com.blacksquircle.ui.feature.settings.ui.adapter.item.PreferenceItem
import com.blacksquircle.ui.feature.settings.ui.adapter.item.ReleaseModel
import com.blacksquircle.ui.feature.settings.ui.navigation.SettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _headersState = MutableStateFlow(
        listOf(
            PreferenceItem(
                R.string.pref_header_application_title,
                R.string.pref_header_application_summary,
                SettingsScreen.Application
            ),
            PreferenceItem(
                R.string.pref_header_editor_title,
                R.string.pref_header_editor_summary,
                SettingsScreen.Editor
            ),
            PreferenceItem(
                R.string.pref_header_codeStyle_title,
                R.string.pref_header_codeStyle_summary,
                SettingsScreen.CodeStyle
            ),
            PreferenceItem(
                R.string.pref_header_files_title,
                R.string.pref_header_files_summary,
                SettingsScreen.Files
            ),
            PreferenceItem(
                R.string.pref_header_about_title,
                R.string.pref_header_about_summary,
                SettingsScreen.About
            )
        )
    )
    val headersState: StateFlow<List<PreferenceItem>> = _headersState

    private val _changelogState = MutableStateFlow<List<ReleaseModel>>(emptyList())
    val changelogState: StateFlow<List<ReleaseModel>> = _changelogState

    var fullscreenMode: Boolean
        get() = settingsManager.fullScreenMode
        set(value) { settingsManager.fullScreenMode = value }
    var keyboardPreset: String
        get() = settingsManager.keyboardPreset
        set(value) { settingsManager.keyboardPreset = value }

    fun resetKeyboardPreset() {
        settingsManager.remove(SettingsManager.KEY_KEYBOARD_PRESET)
    }

    fun fetchChangeLog(changelog: String) {
        _changelogState.value = ReleaseConverter.toReleaseModels(changelog)
    }
}