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

package com.blacksquircle.ui.feature.settings.ui.codestyle

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
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SliderPreference
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun CodeHeaderScreen(viewModel: CodeHeaderViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    CodeHeaderContent(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onAutoIndentChanged = viewModel::onAutoIndentChanged,
        onAutoBracketsChanged = viewModel::onAutoBracketsChanged,
        onAutoQuotesChanged = viewModel::onAutoQuotesChanged,
        onUseSpacesChanged = viewModel::onUseSpacesChanged,
        onTabWidthChanged = viewModel::onTabWidthChanged,
    )
}

@Composable
private fun CodeHeaderContent(
    viewState: CodeHeaderState,
    onBackClicked: () -> Unit,
    onAutoIndentChanged: (Boolean) -> Unit,
    onAutoBracketsChanged: (Boolean) -> Unit,
    onAutoQuotesChanged: (Boolean) -> Unit,
    onUseSpacesChanged: (Boolean) -> Unit,
    onTabWidthChanged: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_codeStyle_title),
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
                title = stringResource(R.string.pref_category_code_style)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_auto_indent_title),
                subtitle = stringResource(R.string.pref_auto_indent_summary),
                checked = viewState.autoIndentation,
                onCheckedChange = onAutoIndentChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_close_brackets_title),
                subtitle = stringResource(R.string.pref_close_brackets_summary),
                checked = viewState.autoCloseBrackets,
                onCheckedChange = onAutoBracketsChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_close_quotes_title),
                subtitle = stringResource(R.string.pref_close_quotes_summary),
                checked = viewState.autoCloseQuotes,
                onCheckedChange = onAutoQuotesChanged,
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_tab_options)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_use_spaces_not_tabs_title),
                subtitle = stringResource(R.string.pref_use_spaces_not_tabs_summary),
                checked = viewState.useSpacesInsteadOfTabs,
                onCheckedChange = onUseSpacesChanged,
            )
            SliderPreference(
                title = stringResource(R.string.pref_tab_width_title),
                subtitle = stringResource(R.string.pref_tab_width_summary),
                minValue = 2f,
                maxValue = 8f,
                step = 2,
                currentValue = viewState.tabWidth.toFloat(),
                onValueChanged = { tabWidth ->
                    onTabWidthChanged(tabWidth.toInt())
                }
            )
        }
    }
}

@Preview
@Composable
private fun AppHeaderScreenPreview() {
    SquircleTheme {
        CodeHeaderContent(
            viewState = CodeHeaderState(
                autoIndentation = true,
                autoCloseBrackets = true,
                autoCloseQuotes = true,
                useSpacesInsteadOfTabs = true,
                tabWidth = 4,
            ),
            onBackClicked = {},
            onAutoIndentChanged = {},
            onAutoBracketsChanged = {},
            onAutoQuotesChanged = {},
            onUseSpacesChanged = {},
            onTabWidthChanged = {},
        )
    }
}