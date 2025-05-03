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

package com.blacksquircle.ui.feature.git.ui.commit

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
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.git.R
import com.blacksquircle.ui.feature.git.api.navigation.CommitDialog
import com.blacksquircle.ui.feature.git.domain.model.ChangeType
import com.blacksquircle.ui.feature.git.domain.model.GitChange
import com.blacksquircle.ui.feature.git.internal.GitComponent
import com.blacksquircle.ui.feature.git.ui.commit.compose.ChangeList

@Composable
internal fun CommitScreen(
    navArgs: CommitDialog,
    navController: NavController,
    viewModel: CommitViewModel = daggerViewModel { context ->
        val component = GitComponent.buildOrGet(context)
        CommitViewModel.ParameterizedFactory(navArgs.repository).also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    CommitScreen(
        viewState = viewState,
        onChangeSelected = viewModel::onChangeSelected,
        onCommitMessageChanged = viewModel::onCommitMessageChanged,
        onAmendClicked = viewModel::onAmendClicked,
        onCommitClicked = viewModel::onCommitClicked,
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
private fun CommitScreen(
    viewState: CommitViewState,
    onChangeSelected: (GitChange) -> Unit = {},
    onCommitMessageChanged: (String) -> Unit = {},
    onAmendClicked: () -> Unit = {},
    onCommitClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    AlertDialog(
        title = stringResource(R.string.git_commit_title),
        content = {
            Column {
                when {
                    viewState.isCommitting -> {
                        Text(
                            text = stringResource(R.string.git_commit_committing),
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
                                .height(200.dp)
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
                        ChangeList(
                            changesList = viewState.changesList,
                            selectedChanges = viewState.selectedChanges,
                            onChangeSelected = onChangeSelected,
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            inputText = viewState.commitMessage,
                            onInputChanged = onCommitMessageChanged,
                            labelText = stringResource(R.string.git_commit_message),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(Modifier.height(8.dp))

                        CheckBox(
                            title = stringResource(R.string.action_amend),
                            checked = viewState.isAmend,
                            onClick = onAmendClicked,
                        )
                    }
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked,
        confirmButton = stringResource(R.string.action_commit),
        confirmButtonEnabled = viewState.isCommitButtonEnabled,
        onConfirmClicked = onCommitClicked
    )
}

@PreviewLightDark
@Composable
private fun CommitScreenPreview() {
    PreviewBackground {
        CommitScreen(
            viewState = CommitViewState(
                changesList = listOf(
                    GitChange("added.txt", ChangeType.ADDED),
                    GitChange("modified.txt", ChangeType.MODIFIED),
                    GitChange("deleted.txt", ChangeType.DELETED),
                ),
                selectedChanges = listOf(
                    GitChange("added.txt", ChangeType.ADDED),
                    GitChange("modified.txt", ChangeType.MODIFIED),
                    GitChange("deleted.txt", ChangeType.DELETED),
                ),
                isCommitting = false,
                isLoading = false,
            ),
        )
    }
}