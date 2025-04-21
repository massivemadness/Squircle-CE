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

@Composable
internal fun GitHeaderScreen(
    navController: NavController,
    viewModel: GitHeaderViewModel = daggerViewModel { context ->
        val component = SettingsComponent.buildOrGet(context)
        GitHeaderViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GitHeaderScreen(
        onBackClicked = viewModel::onBackClicked
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
private fun AboutHeaderScreen(
    onBackClicked: () -> Unit = {}
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource("Git"),
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
            // todo
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