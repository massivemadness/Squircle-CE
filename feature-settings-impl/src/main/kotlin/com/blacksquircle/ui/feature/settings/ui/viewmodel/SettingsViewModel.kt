/*
 * Copyright 2023 Squircle CE contributors.
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
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.ui.adapter.PreferenceHeader
import com.blacksquircle.ui.feature.settings.ui.navigation.SettingsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    stringProvider: StringProvider,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _headersState = MutableStateFlow(
        listOf(
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_application_title),
                subtitle = stringProvider.getString(R.string.pref_header_application_summary),
                selected = false,
                screen = SettingsScreen.Application,
            ),
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_editor_title),
                subtitle = stringProvider.getString(R.string.pref_header_editor_summary),
                selected = false,
                screen = SettingsScreen.Editor,
            ),
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_codeStyle_title),
                subtitle = stringProvider.getString(R.string.pref_header_codeStyle_summary),
                selected = false,
                screen = SettingsScreen.CodeStyle,
            ),
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_files_title),
                subtitle = stringProvider.getString(R.string.pref_header_files_summary),
                selected = false,
                screen = SettingsScreen.Files,
            ),
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_keybindings_title),
                subtitle = stringProvider.getString(R.string.pref_header_keybindings_summary),
                selected = false,
                screen = SettingsScreen.Keybindings,
            ),
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_cloud_title),
                subtitle = stringProvider.getString(R.string.pref_header_cloud_summary),
                selected = false,
                screen = SettingsScreen.Cloud,
            ),
            PreferenceHeader(
                title = stringProvider.getString(R.string.pref_header_about_title),
                subtitle = stringProvider.getString(R.string.pref_header_about_summary),
                selected = false,
                screen = SettingsScreen.About,
            ),
        ),
    )
    val headersState: StateFlow<List<PreferenceHeader>> = _headersState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    var fullscreenMode: Boolean
        get() = settingsManager.fullScreenMode
        set(value) { settingsManager.fullScreenMode = value }

    fun selectHeader(header: PreferenceHeader) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.Navigation(header.screen))
        }
    }
}