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

package com.blacksquircle.ui.feature.settings.ui.git

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.blacksquircle.ui.feature.settings.internal.SettingsComponent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.ds.R as UiR
import com.blacksquircle.ui.ds.toolbar.Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import com.blacksquircle.ui.core.extensions.daggerViewModel
import androidx.compose.ui.res.stringResource
import com.blacksquircle.ui.core.extensions.showToast
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import androidx.compose.foundation.layout.padding

@Composable
internal fun GitHeaderScreen(
    navController: NavController,
    viewModel: GitHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        GitHeaderViewModel.Factory().also(component::inject)
    }
) {
    GitHeaderScreen(
        onBackClicked = viewModel::onBackClicked,
        onCredentialsClicked = viewModel::onCredentialsClicked,
        onUserClicked = viewModel::onUserClicked,
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
private fun GitHeaderScreen(
    onBackClicked: () -> Unit = {},
    onCredentialsClicked: () -> Unit = {},
    onUserClicked: () -> Unit = {}
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = "Git",
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
                title = "Git"
            )
            Preference(
                title = "Credentials",
                onClick = onCredentialsClicked
            )
            Preference(
                title = "User",
                onClick = onUserClicked
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun GitHeaderScreenPreview() {
    PreviewBackground {
        GitHeaderScreen()
    }
}