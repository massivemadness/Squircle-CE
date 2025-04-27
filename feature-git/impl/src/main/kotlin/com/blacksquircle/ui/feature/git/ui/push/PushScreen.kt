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

package com.blacksquircle.ui.feature.git.ui.push

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.api.navigation.PushDialog
import com.blacksquircle.ui.feature.git.internal.GitComponent

@Composable
internal fun PushScreen(
    navArgs: PushDialog,
    navController: NavController,
    viewModel: PushViewModel = daggerViewModel { context ->
        val component = GitComponent.buildOrGet(context)
        PushViewModel.ParameterizedFactory(navArgs.repository).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    PushScreen(
        viewState = viewState,
        onForceClicked = viewModel::onForceClicked,
        onPushClicked = viewModel::onPushClicked,
        onBackClicked = viewModel::onBackClicked,
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
private fun PushScreen(
    viewState: PushViewState,
    onForceClicked: () -> Unit = {},
    onPushClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.git_push_title),
        content = {
            Column {
                when {
                    viewState.isPushing -> {
                        Text(
                            text = stringResource(R.string.git_push_pushing),
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                            style = SquircleTheme.typography.text16Regular,
                        )

                        Spacer(Modifier.height(16.dp))

                        LinearProgress(
                            indeterminate = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    viewState.isLoading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            CircularProgress()
                        }
                    }

                    viewState.isError -> {
                        Text(
                            text = stringResource(R.string.git_fatal, viewState.errorMessage),
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                            style = SquircleTheme.typography.text16Regular,
                        )
                    }

                    else -> {
                        Text(
                            text = when {
                                viewState.commitCount == -1 -> {
                                    stringResource(R.string.git_push_branch)
                                }
                                else -> {
                                    stringResource(
                                        R.string.git_push_commits,
                                        viewState.commitCount,
                                        viewState.currentBranch,
                                    )
                                }
                            },
                            style = SquircleTheme.typography.text16Regular,
                            color = SquircleTheme.colors.colorTextAndIconSecondary,
                        )

                        Spacer(Modifier.height(8.dp))

                        CheckBox(
                            title = stringResource(R.string.action_force),
                            checked = viewState.isForce,
                            onClick = onForceClicked,
                        )
                    }
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked,
        confirmButton = stringResource(R.string.action_push),
        confirmButtonEnabled = viewState.isPushButtonEnabled,
        onConfirmClicked = onPushClicked
    )
}

@PreviewLightDark
@Composable
private fun PushScreenPreview() {
    PreviewBackground {
        PushScreen(
            viewState = PushViewState(
                currentBranch = "refs/heads/master",
                commitCount = 2,
                isLoading = false,
            ),
        )
    }
}