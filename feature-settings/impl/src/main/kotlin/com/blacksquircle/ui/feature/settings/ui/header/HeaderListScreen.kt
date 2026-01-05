/*
 * Copyright Squircle CE contributors.
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
import androidx.navigation3.runtime.NavKey
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.preference.PreferenceHeader
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.servers.api.navigation.ServerListRoute
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.api.navigation.AboutHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.ApplicationHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.CodeStyleHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.EditorHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.FilesHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.GitHeaderRoute
import com.blacksquircle.ui.feature.settings.api.navigation.TerminalHeaderRoute
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.feature.shortcuts.api.navigation.ShortcutsRoute
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun HeaderListScreen(
    viewModel: HeaderListViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        HeaderListViewModel.Factory().also(component::inject)
    }
) {
    HeaderListScreen(
        onHeaderClicked = viewModel::onHeaderClicked,
        onBackClicked = viewModel::onBackClicked,
    )
    CleanupEffect {
        SettingsComponent.release()
    }
}

@Composable
private fun HeaderListScreen(
    onBackClicked: () -> Unit = {},
    onHeaderClicked: (NavKey) -> Unit = {},
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
                onClick = { onHeaderClicked(ApplicationHeaderRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_editor_title),
                subtitle = stringResource(R.string.settings_header_editor_subtitle),
                onClick = { onHeaderClicked(EditorHeaderRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_codestyle_title),
                subtitle = stringResource(R.string.settings_header_codestyle_subtitle),
                onClick = { onHeaderClicked(CodeStyleHeaderRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_files_title),
                subtitle = stringResource(R.string.settings_header_files_subtitle),
                onClick = { onHeaderClicked(FilesHeaderRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_terminal_title),
                subtitle = stringResource(R.string.settings_header_terminal_subtitle),
                onClick = { onHeaderClicked(TerminalHeaderRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_keybindings_title),
                subtitle = stringResource(R.string.settings_header_keybindings_subtitle),
                onClick = { onHeaderClicked(ShortcutsRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_cloud_title),
                subtitle = stringResource(R.string.settings_header_cloud_subtitle),
                onClick = { onHeaderClicked(ServerListRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_git_title),
                subtitle = stringResource(R.string.settings_header_git_subtitle),
                onClick = { onHeaderClicked(GitHeaderRoute) },
            )
            PreferenceHeader(
                title = stringResource(R.string.settings_header_about_title),
                subtitle = stringResource(R.string.settings_header_about_subtitle),
                onClick = { onHeaderClicked(AboutHeaderRoute) },
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