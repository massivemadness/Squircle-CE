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

package com.blacksquircle.ui.feature.settings.ui.files

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.openStorageSettings
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.preference.ListPreference
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun FilesHeaderScreen(
    navController: NavController,
    viewModel: FilesHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        FilesHeaderViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    FilesHeaderScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onEncodingAutoDetectChanged = viewModel::onEncodingAutoDetectChanged,
        onEncodingForOpeningChanged = viewModel::onEncodingForOpeningChanged,
        onEncodingForSavingChanged = viewModel::onEncodingForSavingChanged,
        onLineBreaksForSavingChanged = viewModel::onLineBreakForSavingChanged,
        onStorageAccessClicked = viewModel::onStorageAccessClicked,
        onShowHiddenChanged = viewModel::onShowHiddenChanged,
        onCompactPackagesChanged = viewModel::onCompactPackagesChanged,
        onFoldersOnTopChanged = viewModel::onFoldersOnTopChanged,
        onSortModeChanged = viewModel::onSortModeChanged,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
                is FilesHeaderViewEvent.OpenStorageSettings -> context.openStorageSettings()
            }
        }
    }
}

@Composable
private fun FilesHeaderScreen(
    viewState: FilesHeaderViewState,
    onBackClicked: () -> Unit = {},
    onEncodingAutoDetectChanged: (Boolean) -> Unit = {},
    onEncodingForOpeningChanged: (String) -> Unit = {},
    onEncodingForSavingChanged: (String) -> Unit = {},
    onLineBreaksForSavingChanged: (String) -> Unit = {},
    onStorageAccessClicked: () -> Unit = {},
    onShowHiddenChanged: (Boolean) -> Unit = {},
    onCompactPackagesChanged: (Boolean) -> Unit = {},
    onFoldersOnTopChanged: (Boolean) -> Unit = {},
    onSortModeChanged: (String) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_files_title),
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
                title = stringResource(R.string.pref_category_permissions)
            )
            Preference(
                title = stringResource(R.string.pref_storage_access_title),
                subtitle = stringResource(R.string.pref_storage_access_summary),
                onClick = onStorageAccessClicked,
            )
            HorizontalDivider()
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
                title = stringResource(R.string.pref_compact_packages_title),
                subtitle = stringResource(R.string.pref_compact_packages_summary),
                checked = viewState.compactPackages,
                onCheckedChange = onCompactPackagesChanged,
            )
            SwitchPreference(
                title = stringResource(R.string.pref_folders_on_top_title),
                subtitle = stringResource(R.string.pref_folders_on_top_summary),
                checked = viewState.foldersOnTop,
                onCheckedChange = onFoldersOnTopChanged,
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

@PreviewLightDark
@Composable
private fun FilesHeaderScreenPreview() {
    PreviewBackground {
        FilesHeaderScreen(
            viewState = FilesHeaderViewState(
                encodingAutoDetect = false,
                encodingForOpening = "UTF-8",
                encodingForSaving = "UTF-8",
                encodingList = emptyList(),
                lineBreakForSaving = "lf",
                showHidden = true,
                compactPackages = true,
                foldersOnTop = true,
                sortMode = "sort_by_name",
            ),
        )
    }
}