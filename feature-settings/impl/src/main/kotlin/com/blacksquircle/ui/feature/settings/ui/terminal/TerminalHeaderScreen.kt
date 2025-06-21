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

package com.blacksquircle.ui.feature.settings.ui.terminal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.preference.ListPreference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.feature.terminal.api.model.RuntimeType
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun TerminalHeaderScreen(
    navController: NavController,
    viewModel: TerminalHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        TerminalHeaderViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    TerminalHeaderScreen(
        viewState = viewState,
        onBackClicked = navController::popBackStack,
        onTerminalRuntimeChanged = viewModel::onTerminalRuntimeChanged,
        onCursorBlinkingChanged = viewModel::onCursorBlinkingChanged,
        onKeepScreenOnChanged = viewModel::onKeepScreenOnChanged,
    )
}

@Composable
private fun TerminalHeaderScreen(
    viewState: TerminalHeaderViewState,
    onBackClicked: () -> Unit = {},
    onTerminalRuntimeChanged: (String) -> Unit = {},
    onCursorBlinkingChanged: (Boolean) -> Unit = {},
    onKeepScreenOnChanged: (Boolean) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.settings_header_terminal_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.settings_category_environment),
            )
            ListPreference(
                title = stringResource(R.string.settings_terminal_shell_title),
                subtitle = stringResource(R.string.settings_terminal_shell_subtitle),
                entries = RuntimeType.entries
                    .fastMap(RuntimeType::title).toTypedArray(),
                entryValues = RuntimeType.entries
                    .fastMap(RuntimeType::value).toTypedArray(),
                selectedValue = viewState.currentRuntime.value,
                onValueSelected = onTerminalRuntimeChanged,
            )

            HorizontalDivider()

            PreferenceGroup(
                title = stringResource(R.string.settings_category_behavior)
            )
            SwitchPreference(
                title = stringResource(R.string.settings_terminal_cursor_blinking_title),
                subtitle = stringResource(R.string.settings_terminal_cursor_blinking_subtitle),
                checked = viewState.cursorBlinking,
                onCheckedChange = onCursorBlinkingChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.settings_terminal_keep_screen_on_title),
                subtitle = stringResource(R.string.settings_terminal_keep_screen_on_subtitle),
                checked = viewState.keepScreenOn,
                onCheckedChange = onKeepScreenOnChanged,
            )
        }
    }
}

@Preview
@Composable
private fun TerminalHeaderScreenPreview() {
    PreviewBackground {
        TerminalHeaderScreen(
            viewState = TerminalHeaderViewState(
                currentRuntime = RuntimeType.ANDROID,
                cursorBlinking = true,
                keepScreenOn = true,
            )
        )
    }
}