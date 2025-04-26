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
import com.blacksquircle.ui.feature.servers.api.navigation.CloudScreen
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.api.navigation.AboutHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.AppHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.CodeStyleHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.EditorHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.FilesHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.GitHeaderScreen
import com.blacksquircle.ui.feature.settings.ui.header.model.PreferenceHeader
import com.blacksquircle.ui.feature.shortcuts.api.navigation.ShortcutsScreen

@Immutable
internal data class HeaderListViewState(
    val headers: List<PreferenceHeader> = listOf(
        PreferenceHeader(
            title = R.string.pref_header_application_title,
            subtitle = R.string.pref_header_application_summary,
            selected = false,
            screen = AppHeaderScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_editor_title,
            subtitle = R.string.pref_header_editor_summary,
            selected = false,
            screen = EditorHeaderScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_codeStyle_title,
            subtitle = R.string.pref_header_codeStyle_summary,
            selected = false,
            screen = CodeStyleHeaderScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_files_title,
            subtitle = R.string.pref_header_files_summary,
            selected = false,
            screen = FilesHeaderScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_keybindings_title,
            subtitle = R.string.pref_header_keybindings_summary,
            selected = false,
            screen = ShortcutsScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_cloud_title,
            subtitle = R.string.pref_header_cloud_summary,
            selected = false,
            screen = CloudScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_git_title,
            subtitle = R.string.pref_header_git_summary,
            selected = false,
            screen = GitHeaderScreen,
        ),
        PreferenceHeader(
            title = R.string.pref_header_about_title,
            subtitle = R.string.pref_header_about_summary,
            selected = false,
            screen = AboutHeaderScreen,
        ),
    )
) : ViewState