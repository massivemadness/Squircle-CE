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

package com.blacksquircle.ui.feature.settings.ui.files

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.preference.ListPreference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
fun FilesHeaderScreen(viewModel: FilesHeaderViewModel) {
    val viewState by viewModel.viewState.collectAsState()
    FilesHeaderScreen(
        viewState = viewState,
        onBackClicked = viewModel::popBackStack,
        onEncodingAutoDetectChanged = viewModel::onEncodingAutoDetectChanged,
        onEncodingForOpeningChanged = viewModel::onEncodingForOpeningChanged,
        onEncodingForSavingChanged = viewModel::onEncodingForSavingChanged,
        onLineBreaksForSavingChanged = viewModel::onLineBreakForSavingChanged,
        onShowHiddenChanged = viewModel::onShowHiddenChanged,
        onFoldersOnTopChanged = viewModel::onFoldersOnTopChanged,
        onViewModeChanged = viewModel::onViewModeChanged,
        onSortModeChanged = viewModel::onSortModeChanged,
    )
}

@Composable
private fun FilesHeaderScreen(
    viewState: FilesHeaderState,
    onBackClicked: () -> Unit,
    onEncodingAutoDetectChanged: (Boolean) -> Unit,
    onEncodingForOpeningChanged: (String) -> Unit,
    onEncodingForSavingChanged: (String) -> Unit,
    onLineBreaksForSavingChanged: (String) -> Unit,
    onShowHiddenChanged: (Boolean) -> Unit,
    onFoldersOnTopChanged: (Boolean) -> Unit,
    onViewModeChanged: (String) -> Unit,
    onSortModeChanged: (String) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_files_title),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceGroup(
                title = stringResource(R.string.pref_category_encoding)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_encoding_auto_detect_title),
                subtitle = stringResource(R.string.pref_encoding_auto_detect_summary),
                checked = viewState.encodingAutoDetect,
                onCheckedChange = onEncodingAutoDetectChanged,
            )
            ListPreference(
                title = stringResource(R.string.pref_encoding_for_opening_title),
                subtitle = stringResource(R.string.pref_encoding_for_opening_summary),
                enabled = !viewState.encodingAutoDetect,
                entries = viewState.encodingList.toTypedArray(),
                entryValues = viewState.encodingList.toTypedArray(),
                selectedValue = viewState.encodingForOpening,
                onValueSelected = onEncodingForOpeningChanged,
            )
            ListPreference(
                title = stringResource(R.string.pref_encoding_for_saving_title),
                subtitle = stringResource(R.string.pref_encoding_for_saving_summary),
                enabled = !viewState.encodingAutoDetect,
                entries = viewState.encodingList.toTypedArray(),
                entryValues = viewState.encodingList.toTypedArray(),
                selectedValue = viewState.encodingForSaving,
                onValueSelected = onEncodingForSavingChanged,
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_linebreaks)
            )
            ListPreference(
                title = stringResource(R.string.pref_linebreaks_for_saving_files_title),
                subtitle = stringResource(R.string.pref_linebreaks_for_saving_files_summary),
                entries = stringArrayResource(R.array.linebreak_entries),
                entryValues = stringArrayResource(R.array.linebreak_values),
                selectedValue = viewState.lineBreakForSaving,
                onValueSelected = onLineBreaksForSavingChanged,
            )
            HorizontalDivider()
            PreferenceGroup(
                title = stringResource(R.string.pref_category_file_manager)
            )
            SwitchPreference(
                title = stringResource(R.string.pref_show_hidden_files_title),
                subtitle = stringResource(R.string.pref_show_hidden_files_summary),
                checked = viewState.showHidden,
                onCheckedChange = onShowHiddenChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_folders_on_top_title),
                subtitle = stringResource(R.string.pref_folders_on_top_summary),
                checked = viewState.foldersOnTop,
                onCheckedChange = onFoldersOnTopChanged,
            )
            ListPreference(
                title = stringResource(R.string.pref_view_mode_title),
                entryNameAsSubtitle = true,
                entries = stringArrayResource(R.array.view_mode_entries),
                entryValues = stringArrayResource(R.array.view_mode_values),
                selectedValue = viewState.viewMode,
                onValueSelected = onViewModeChanged,
            )
            ListPreference(
                title = stringResource(R.string.pref_sort_mode_title),
                entryNameAsSubtitle = true,
                entries = stringArrayResource(R.array.sort_mode_entries),
                entryValues = stringArrayResource(R.array.sort_mode_values),
                selectedValue = viewState.sortMode,
                onValueSelected = onSortModeChanged,
            )
        }
    }
}

@Preview
@Composable
private fun FilesHeaderScreenPreview() {
    SquircleTheme {
        FilesHeaderScreen(
            viewState = FilesHeaderState(
                encodingAutoDetect = false,
                encodingForOpening = "UTF-8",
                encodingForSaving = "UTF-8",
                encodingList = emptyList(),
                lineBreakForSaving = "2",
                showHidden = true,
                foldersOnTop = true,
                viewMode = "0",
                sortMode = "0",
            ),
            onBackClicked = {},
            onEncodingAutoDetectChanged = {},
            onEncodingForOpeningChanged = {},
            onEncodingForSavingChanged = {},
            onLineBreaksForSavingChanged = {},
            onShowHiddenChanged = {},
            onFoldersOnTopChanged = {},
            onViewModeChanged = {},
            onSortModeChanged = {},
        )
    }
}