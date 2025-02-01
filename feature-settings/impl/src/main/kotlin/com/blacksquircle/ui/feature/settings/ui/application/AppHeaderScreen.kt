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

package com.blacksquircle.ui.feature.settings.ui.application

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.extensions.fullscreenMode
import com.blacksquircle.ui.core.theme.Theme
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.extensions.findActivity
import com.blacksquircle.ui.ds.preference.ListPreference
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun AppHeaderScreen(viewModel: AppHeaderViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    AppHeaderScreen(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onThemeChanged = viewModel::onThemeChanged,
        onFullscreenChanged = viewModel::onFullscreenChanged,
        onConfirmExitChanged = viewModel::onConfirmExitChanged,
    )
}

@Composable
private fun AppHeaderScreen(
    viewState: AppHeaderState,
    onBackClicked: () -> Unit,
    onThemeChanged: (String) -> Unit,
    onFullscreenChanged: (Boolean) -> Unit,
    onConfirmExitChanged: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_application_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.navigationBarsPadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.pref_category_look_and_feel)
            )
            ListPreference(
                title = stringResource(R.string.pref_app_theme_title),
                subtitle = stringResource(R.string.pref_app_theme_summary),
                entries = stringArrayResource(R.array.theme_entries),
                entryValues = stringArrayResource(R.array.theme_values),
                selectedValue = viewState.appTheme,
                onValueSelected = { value ->
                    onThemeChanged(value)
                    Theme.of(value).apply()
                },
            )
            Preference(
                title = stringResource(R.string.pref_color_scheme_title),
                subtitle = stringResource(R.string.pref_color_scheme_summary),
                onClick = {
                    // TODO
                },
            )

            val context = LocalContext.current
            SwitchPreference(
                title = stringResource(R.string.pref_fullscreen_title),
                subtitle = stringResource(R.string.pref_fullscreen_summary),
                checked = viewState.fullscreenMode,
                onCheckedChange = { value ->
                    onFullscreenChanged(value)
                    context.findActivity().window
                        .fullscreenMode(value)
                },
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_other)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_confirm_exit_title),
                subtitle = stringResource(R.string.pref_confirm_exit_summary),
                checked = viewState.confirmExit,
                onCheckedChange = onConfirmExitChanged,
            )
        }
    }
}

@Preview
@Composable
private fun AppHeaderScreenPreview() {
    SquircleTheme {
        AppHeaderScreen(
            viewState = AppHeaderState(
                appTheme = Theme.DARK.value,
                fullscreenMode = false,
                confirmExit = true
            ),
            onBackClicked = {},
            onThemeChanged = {},
            onFullscreenChanged = {},
            onConfirmExitChanged = {},
        )
    }
}