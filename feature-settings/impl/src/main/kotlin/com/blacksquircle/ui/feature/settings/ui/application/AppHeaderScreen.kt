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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun AppHeaderScreen(viewModel: AppHeaderViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    AppHeaderContent(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onFullscreenChanged = viewModel::onFullscreenChanged,
        onConfirmExitChanged = viewModel::onConfirmExitChanged,
    )
}

@Composable
private fun AppHeaderContent(
    viewState: AppHeaderState,
    onBackClicked: () -> Unit,
    onFullscreenChanged: (Boolean) -> Unit,
    onConfirmExitChanged: (Boolean) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_application_title),
                backIcon = UiR.drawable.ic_back,
                onBackClicked = onBackClicked,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.pref_category_look_and_feel)
            )
            Preference(
                title = stringResource(R.string.pref_app_theme_title),
                subtitle = stringResource(R.string.pref_app_theme_summary),
                onClick = {},
            )
            Preference(
                title = stringResource(R.string.pref_color_scheme_title),
                subtitle = stringResource(R.string.pref_color_scheme_summary),
                onClick = {},
            )
            SwitchPreference(
                title = stringResource(R.string.pref_fullscreen_title),
                subtitle = stringResource(R.string.pref_fullscreen_summary),
                checked = viewState.fullscreenMode,
                onCheckedChange = onFullscreenChanged,
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
        AppHeaderContent(
            viewState = AppHeaderState(fullscreenMode = false, confirmExit = true),
            onBackClicked = {},
            onFullscreenChanged = {},
            onConfirmExitChanged = {},
        )
    }
}