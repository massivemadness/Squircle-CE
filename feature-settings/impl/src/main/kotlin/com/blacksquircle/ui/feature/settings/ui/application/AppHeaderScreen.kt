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

package com.blacksquircle.ui.feature.settings.ui.application

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.preference.Preference
import com.blacksquircle.ui.ds.preference.PreferenceGroup
import com.blacksquircle.ui.ds.preference.SwitchPreference
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun AppHeaderScreen(
    navController: NavController,
    viewModel: AppHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        AppHeaderViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    AppHeaderScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
        onColorSchemeClicked = viewModel::onColorSchemeClicked,
        onFullscreenChanged = viewModel::onFullscreenChanged,
        onConfirmExitChanged = viewModel::onConfirmExitChanged,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }
}

@Composable
private fun AppHeaderScreen(
    viewState: AppHeaderViewState,
    onBackClicked: () -> Unit = {},
    onColorSchemeClicked: () -> Unit = {},
    onFullscreenChanged: (Boolean) -> Unit = {},
    onConfirmExitChanged: (Boolean) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.pref_header_application_title),
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
                title = stringResource(R.string.pref_category_look_and_feel)
            )
            Preference(
                title = stringResource(R.string.pref_color_scheme_title),
                subtitle = stringResource(R.string.pref_color_scheme_summary),
                onClick = onColorSchemeClicked,
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

@PreviewLightDark
@Composable
private fun AppHeaderScreenPreview() {
    PreviewBackground {
        AppHeaderScreen(
            viewState = AppHeaderViewState(
                fullscreenMode = false,
                confirmExit = true
            ),
        )
    }
}