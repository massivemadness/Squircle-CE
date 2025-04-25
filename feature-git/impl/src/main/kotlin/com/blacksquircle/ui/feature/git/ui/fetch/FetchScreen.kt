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

package com.blacksquircle.ui.feature.git.ui.fetch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.api.navigation.FetchDialog
import com.blacksquircle.ui.feature.git.internal.GitComponent

@Composable
internal fun FetchScreen(
    navArgs: FetchDialog,
    navController: NavController,
    viewModel: FetchViewModel = daggerViewModel { context ->
        val component = GitComponent.buildOrGet(context)
        FetchViewModel.ParameterizedFactory(navArgs.repository).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    FetchScreen(
        viewState = viewState,
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
private fun FetchScreen(
    viewState: FetchViewState,
    onBackClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.git_fetch_title),
        content = {
            Column {
                when {
                    viewState.isFetching -> {
                        Text(
                            text = stringResource(R.string.git_fetch_fetching),
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                            style = SquircleTheme.typography.text16Regular,
                        )

                        Spacer(Modifier.height(16.dp))

                        LinearProgress(
                            indeterminate = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    viewState.isError -> {
                        Text(
                            text = stringResource(R.string.git_fatal, viewState.errorMessage),
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                            style = SquircleTheme.typography.text16Regular,
                        )
                    }
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked
    )
}

@PreviewLightDark
@Composable
private fun FetchScreenPreview() {
    PreviewBackground {
        FetchScreen(
            viewState = FetchViewState(),
        )
    }
}