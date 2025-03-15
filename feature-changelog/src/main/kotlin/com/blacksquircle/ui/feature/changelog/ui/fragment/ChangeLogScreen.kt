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

package com.blacksquircle.ui.feature.changelog.ui.fragment

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.navigateTo
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.toolbar.Toolbar
import com.blacksquircle.ui.feature.changelog.R
import com.blacksquircle.ui.feature.changelog.domain.model.ReleaseModel
import com.blacksquircle.ui.feature.changelog.internal.ChangelogComponent
import com.blacksquircle.ui.feature.changelog.ui.composable.ReleaseInfo
import com.blacksquircle.ui.feature.changelog.ui.viewmodel.ChangeLogViewModel
import com.blacksquircle.ui.feature.changelog.ui.viewmodel.ChangeLogViewState
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun ChangeLogScreen(
    navController: NavController,
    viewModel: ChangeLogViewModel = daggerViewModel { context ->
        val component = ChangelogComponent.buildOrGet(context)
        ChangeLogViewModel.Factory().also(component::inject)
    },
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    ChangeLogScreen(
        viewState = viewState,
        onBackClicked = viewModel::onBackClicked,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigateTo(event.screen)
                is ViewEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    CleanupEffect {
        ChangelogComponent.release()
    }
}

@Composable
private fun ChangeLogScreen(
    viewState: ChangeLogViewState,
    onBackClicked: () -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            Toolbar(
                title = stringResource(R.string.label_changelog),
                navigationIcon = UiR.drawable.ic_back,
                onNavigationClicked = onBackClicked,
            )
        },
        modifier = Modifier.imePadding()
    ) { contentPadding ->
        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = viewState.releases,
                key = ReleaseModel::versionName
            ) { release ->
                ReleaseInfo(
                    versionName = release.versionName,
                    releaseDate = release.releaseDate,
                    releaseNotes = release.releaseNotes,
                )
                HorizontalDivider()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ChangeLogScreenPreview() {
    PreviewBackground {
        ChangeLogScreen(
            viewState = ChangeLogViewState(
                releases = listOf(
                    ReleaseModel(
                        versionName = "v2024.1.0",
                        releaseDate = "24 Jan. 2024",
                        releaseNotes = "- New UI!<br>- Improved support for tablets and foldables!",
                    )
                )
            ),
        )
    }
}