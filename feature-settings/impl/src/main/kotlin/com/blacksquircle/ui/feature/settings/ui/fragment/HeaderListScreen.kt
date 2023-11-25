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

package com.blacksquircle.ui.feature.settings.ui.fragment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.section.SectionItem
import com.blacksquircle.ui.ds.sizeL
import com.blacksquircle.ui.ds.sizeXS
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.ui.navigation.SettingsScreen
import com.blacksquircle.ui.feature.settings.ui.viewmodel.PreferenceHeader
import com.blacksquircle.ui.feature.settings.ui.viewmodel.SettingsViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun HeaderListScreen(viewModel: SettingsViewModel) {
    val state by viewModel.headersState.collectAsState()
    HeaderListContent(
        state = state,
        onBackClicked = viewModel::onBackClicked,
        onItemClicked = viewModel::selectHeader
    )
}

@Composable
private fun HeaderListContent(
    state: List<PreferenceHeader>,
    onBackClicked: () -> Unit,
    onItemClicked: (PreferenceHeader) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Toolbar(
            title = stringResource(R.string.label_settings),
            backIcon = UiR.drawable.ic_back,
            onBackClicked = onBackClicked,
        )
        HeaderList(
            headers = state,
            onItemClicked = onItemClicked,
        )
    }
}

@Composable
private fun HeaderList(
    headers: List<PreferenceHeader>,
    onItemClicked: (PreferenceHeader) -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(sizeL)
            .windowInsetsPadding(
                WindowInsets.systemBars
                    .only(WindowInsetsSides.Bottom)
            ),
        verticalArrangement = Arrangement.spacedBy(sizeXS)
    ) {
        headers.forEach { header ->
            SectionItem(
                icon = header.icon,
                title = header.title,
                subtitle = header.subtitle,
                isSelected = false,
                onSelected = { onItemClicked(header) },
            )
        }
    }
}

@Preview
@Composable
private fun HeaderListScreenPreview() {
    SquircleTheme {
        HeaderListContent(
            state = previewHeaders(),
            onBackClicked = {},
            onItemClicked = {},
        )
    }
}

@Composable
private fun previewHeaders(): List<PreferenceHeader> {
    return listOf(
        PreferenceHeader(
            icon = UiR.drawable.ic_tools_outline,
            title = stringResource(R.string.pref_header_application_title),
            subtitle = stringResource(R.string.pref_header_application_summary),
            selected = false,
            screen = SettingsScreen.Application,
        ),
        PreferenceHeader(
            icon = UiR.drawable.ic_edit_outline,
            title = stringResource(R.string.pref_header_editor_title),
            subtitle = stringResource(R.string.pref_header_editor_summary),
            selected = false,
            screen = SettingsScreen.Editor,
        ),
        PreferenceHeader(
            icon = UiR.drawable.ic_code,
            title = stringResource(R.string.pref_header_codeStyle_title),
            subtitle = stringResource(R.string.pref_header_codeStyle_summary),
            selected = false,
            screen = SettingsScreen.CodeStyle,
        ),
        PreferenceHeader(
            icon = UiR.drawable.ic_file_cabinet,
            title = stringResource(R.string.pref_header_files_title),
            subtitle = stringResource(R.string.pref_header_files_summary),
            selected = false,
            screen = SettingsScreen.Files,
        ),
        PreferenceHeader(
            icon = UiR.drawable.ic_keyboard_outline,
            title = stringResource(R.string.pref_header_keybindings_title),
            subtitle = stringResource(R.string.pref_header_keybindings_summary),
            selected = false,
            screen = SettingsScreen.Keybindings,
        ),
        PreferenceHeader(
            icon = UiR.drawable.ic_server,
            title = stringResource(R.string.pref_header_cloud_title),
            subtitle = stringResource(R.string.pref_header_cloud_summary),
            selected = false,
            screen = SettingsScreen.Cloud,
        ),
        PreferenceHeader(
            icon = UiR.drawable.ic_info,
            title = stringResource(R.string.pref_header_about_title),
            subtitle = stringResource(R.string.pref_header_about_summary),
            selected = false,
            screen = SettingsScreen.About,
        ),
    )
}