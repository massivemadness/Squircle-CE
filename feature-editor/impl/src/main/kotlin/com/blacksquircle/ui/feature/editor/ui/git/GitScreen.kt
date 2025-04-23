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

package com.blacksquircle.ui.feature.editor.ui.git

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.feature.editor.internal.EditorComponent
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.blacksquircle.ui.feature.editor.api.navigation.GitDialog
import com.blacksquircle.ui.core.extensions.showToast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import androidx.compose.runtime.LaunchedEffect
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.dialog.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.stringResource
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun GitScreen(
    navArgs: GitDialog,
    navController: NavController,
    viewModel: GitViewModel = daggerViewModel { context ->
        val component = EditorComponent.buildOrGet(context)
        GitViewModel.Factory().also(component::inject)
    }
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GitScreen(
        viewState = viewState,
        repoPath = navArgs.repoPath,
        onFetchClicked = viewModel::onFetchClicked,
        onPullClicked = viewModel::onPullClicked,
        onCommitClicked = viewModel::onFetchClicked,
        onPushClicked = viewModel::onPushClicked,
        onCheckoutClicked = viewModel::onCheckoutClicked,
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
private fun GitScreen(
    viewState: GitViewState,
    repoPath: String,
    onFetchClicked: (String) -> Unit = {},
    onPullClicked: (String) -> Unit = {},
    onCommitClicked: (String) -> Unit = {},
    onPushClicked: (String) -> Unit = {},
    onCheckoutClicked: (String) -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    AlertDialog(
        title = "Git",
        horizontalPadding = false,
        content = {
            Column {
                GitAction(
                    iconRes = UiR.drawable.ic_autorenew,
                    title = "Fetch",
                    subtitle = "Fetch content from remote repo",
                    onClick = { onFetchClicked(repoPath) }
                )
                GitAction(
                    iconRes = UiR.drawable.ic_tray_arrow_down,
                    title = "Pull",
                    subtitle = "Pull changes from remote repo",
                    onClick = { onPullClicked(repoPath) }
                )
                GitAction(
                    iconRes = UiR.drawable.ic_source_commit,
                    title = "Commit",
                    subtitle = "Commit local repo changes",
                    onClick = { onCommitClicked(repoPath) }
                )
                GitAction(
                    iconRes = UiR.drawable.ic_tray_arrow_up,
                    title = "Push",
                    subtitle = "Push content to remote repo",
                    onClick = { onPushClicked(repoPath) }
                )
                GitAction(
                    iconRes = UiR.drawable.ic_folder_data,
                    title = "Checkout branch",
                    subtitle = "Change or create local repo branch",
                    onClick = { onCheckoutClicked(repoPath) }
                )
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onBackClicked,
        onDismiss = onBackClicked
    )
}

@PreviewLightDark
@Composable
private fun GitScreenPreview() {
    PreviewBackground {
        GitScreen(
            viewState = GitViewState(
                branch = "",
                commitText = "test",
                isLoading = false,
                showCommitDialog = false,
                showBranchDialog = false
            ),
            repoPath = "/sdcard/my-project",
        )
    }
}