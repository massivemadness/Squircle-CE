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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.preference.PreferenceHeader
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.servers.api.navigation.CloudScreen
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.api.navigation.AboutHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.AppHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.CodeStyleHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.EditorHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.FilesHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.GitHeaderScreen
import com.blacksquircle.ui.feature.settings.api.navigation.TerminalHeaderScreen
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.feature.shortcuts.api.navigation.ShortcutsScreen
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun HeaderListScreen(navController: NavController) {
    HeaderListScreen(
        onHeaderClicked = { screen ->
            navController.navigate(screen)
        },
        onBackClicked = {
            navController.popBackStack()
        },
    )
    CleanupEffect {
        SettingsComponent.release()
    }
}

@Composable
private fun HeaderListScreen(
    onBackClicked: () -> Unit = {},
    onHeaderClicked: (Any) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.settings_toolbar_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(contentPadding),
        ) {
            PreferenceHeader(
                title = stringResource(R.string.settings_header_application_title),
                subtitle = stringResource(R.string.settings_header_application_subtitle),
                onClick = { onHeaderClicked(AppHeaderScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_editor_title),
                subtitle = stringResource(R.string.settings_header_editor_subtitle),
                onClick = { onHeaderClicked(EditorHeaderScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_codestyle_title),
                subtitle = stringResource(R.string.settings_header_codestyle_subtitle),
                onClick = { onHeaderClicked(CodeStyleHeaderScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_files_title),
                subtitle = stringResource(R.string.settings_header_files_subtitle),
                onClick = { onHeaderClicked(FilesHeaderScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_terminal_title),
                subtitle = stringResource(R.string.settings_header_terminal_subtitle),
                onClick = { onHeaderClicked(TerminalHeaderScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_keybindings_title),
                subtitle = stringResource(R.string.settings_header_keybindings_subtitle),
                onClick = { onHeaderClicked(ShortcutsScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_cloud_title),
                subtitle = stringResource(R.string.settings_header_cloud_subtitle),
                onClick = { onHeaderClicked(CloudScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_git_title),
                subtitle = stringResource(R.string.settings_header_git_subtitle),
                onClick = { onHeaderClicked(GitHeaderScreen) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_about_title),
                subtitle = stringResource(R.string.settings_header_about_subtitle),
                onClick = { onHeaderClicked(AboutHeaderScreen) },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HeaderListScreenPreview() {
    PreviewBackground {
        HeaderListScreen()
    }
}