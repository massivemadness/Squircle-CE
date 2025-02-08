/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.settings.ui.header

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.ui.navigation.SettingsScreen

@Immutable
internal data class HeaderListViewState(
    val headers: List<PreferenceHeader> = listOf(
        PreferenceHeader(
            title = R.string.pref_header_application_title,
            subtitle = R.string.pref_header_application_summary,
            selected = false,
            screen = SettingsScreen.Application,
        ),
        PreferenceHeader(
            title = R.string.pref_header_editor_title,
            subtitle = R.string.pref_header_editor_summary,
            selected = false,
            screen = SettingsScreen.Editor,
        ),
        PreferenceHeader(
            title = R.string.pref_header_codeStyle_title,
            subtitle = R.string.pref_header_codeStyle_summary,
            selected = false,
            screen = SettingsScreen.CodeStyle,
        ),
        PreferenceHeader(
            title = R.string.pref_header_files_title,
            subtitle = R.string.pref_header_files_summary,
            selected = false,
            screen = SettingsScreen.Files,
        ),
        PreferenceHeader(
            title = R.string.pref_header_keybindings_title,
            subtitle = R.string.pref_header_keybindings_summary,
            selected = false,
            screen = SettingsScreen.Keybindings,
        ),
        PreferenceHeader(
            title = R.string.pref_header_cloud_title,
            subtitle = R.string.pref_header_cloud_summary,
            selected = false,
            screen = SettingsScreen.Cloud,
        ),
        PreferenceHeader(
            title = R.string.pref_header_about_title,
            subtitle = R.string.pref_header_about_summary,
            selected = false,
            screen = SettingsScreen.About,
        ),
    )
) : ViewState()