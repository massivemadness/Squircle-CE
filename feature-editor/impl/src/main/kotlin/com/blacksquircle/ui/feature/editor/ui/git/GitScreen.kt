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
    onBackClicked: () -> Unit = {}
) {
    AlertDialog(
        title = "Git",
        horizontalPadding = false,
        content = {
            Column {
                // todo
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

/*package com.blacksquircle.ui.feature.editor.ui.git

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.progress.LinearProgress
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.editor.api.navigation.GitDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun GitScreen(
    navArgs: GitDialog,
    navController: NavController,
    viewModel: GitHeaderViewModel = daggerViewModel
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    GitScreen(
        viewState = viewState,
        repoPath = navArgs.repoPath,
        credentialsUsername = navArgs.credentialsUsername,
        credentialsToken = navArgs.credentialsToken,
        userEmail = navArgs.userEmail,
        userName = navArgs.userName,
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun GitScreen(
    viewState: GitViewState,
    repoPath: String,
    credentialsUsername: String,
    credentialsToken: String,
    userEmail: String,
    userName: String,
    onCancelClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val git = Git.open(File(repoPath))
    val credentialsProvider = UsernamePasswordCredentialsProvider(credentialsUsername, credentialsToken)
    val showProgress = remember { mutableStateOf(false) }
    val showCommitDialog = remember { mutableStateOf(false) }
    val showCheckoutDialog = remember { mutableStateOf(false) }
    val commitText = remember { mutableStateOf("") }
    val branchName = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    if (showProgress.value) {
        AlertDialog(
            title = "Please wait...",
            content = { LinearProgress(indeterminate = true) },
            onDismiss = {}
        )
    }
    if (showCommitDialog.value) {
        AlertDialog(
            title = "Commit",
            content = {
                TextField(
                    inputText = commitText.value,
                    placeholderText = "New commit!",
                    onInputChanged = { commitText.value = it }
                )
            },
            confirmButton = stringResource(android.R.string.ok),
            dismissButton = stringResource(android.R.string.cancel),
            onConfirmClicked = {
                coroutineScope.launch {
                    showProgress.value = true
                    showCommitDialog.value = false
                    try {
                        withContext(Dispatchers.IO) {
                            git.add().addFilepattern(".").call()
                            git
                                .commit()
                                .setMessage(commitText.value)
                                .setAuthor(userName, userEmail)
                                .setCommitter(userName, userEmail)
                                .call()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Git error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        showProgress.value = false
                    }
                }
            },
            onDismissClicked = { showCommitDialog.value = false },
            onDismiss = { showCommitDialog.value = false }
        )
    }
    if (showCheckoutDialog.value) {
        AlertDialog(
            title = "Checkout branch",
            content = {
                TextField(
                    inputText = branchName.value,
                    placeholderText = "main",
                    labelText = "Current branch: " + git.repository.branch,
                    onInputChanged = { branchName.value = it }
                )
            },
            confirmButton = stringResource(android.R.string.ok),
            dismissButton = stringResource(android.R.string.cancel),
            onConfirmClicked = {
                coroutineScope.launch {
                    showProgress.value = true
                    showCheckoutDialog.value = false
                    try {
                        withContext(Dispatchers.IO) {
                            try {
                                git
                                    .checkout()
                                    .setName(branchName.value)
                                    .call()
                            } catch (e: RefNotFoundException) {
                                git
                                    .checkout()
                                    .setCreateBranch(true)
                                    .setName(branchName.value)
                                    .call()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Git error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        showProgress.value = false
                    }
                }
            },
            onDismissClicked = { showCheckoutDialog.value = false },
            onDismiss = { showCheckoutDialog.value = false }
        )
    }
    AlertDialog(
        title = "Git",
        horizontalPadding = false,
        content = {
            Column {
                GitActionRow(
                    iconRes = UiR.drawable.ic_sync,
                    title = "Fetch",
                    subtitle = "Fetch content from remote repo",
                    onClick = {
                        coroutineScope.launch {
                            showProgress.value = true
                            try {
                                withContext(Dispatchers.IO) {
                                    git
                                        .fetch()
                                        .setRemote("origin")
                                        .setCredentialsProvider(credentialsProvider)
                                        .call()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Git error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                showProgress.value = false
                            }
                        }
                    }
                )
                GitActionRow(
                    iconRes = UiR.drawable.ic_download,
                    title = "Pull",
                    subtitle = "Pull changes from remote repo",
                    onClick = {
                        coroutineScope.launch {
                            showProgress.value = true
                            try {
                                withContext(Dispatchers.IO) {
                                    git
                                        .pull()
                                        .setRemote("origin")
                                        .setCredentialsProvider(credentialsProvider)
                                        .call()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Git error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                showProgress.value = false
                            }
                        }
                    }
                )
                GitActionRow(
                    iconRes = UiR.drawable.ic_commit,
                    title = "Commit",
                    subtitle = "Commit local repo changes",
                    onClick = { showCommitDialog.value = true }
                )
                GitActionRow(
                    iconRes = UiR.drawable.ic_upload,
                    title = "Push",
                    subtitle = "Push content to remote repo",
                    onClick = {
                        coroutineScope.launch {
                            showProgress.value = true
                            try {
                                withContext(Dispatchers.IO) {
                                    git
                                        .push()
                                        .setRemote("origin")
                                        .setCredentialsProvider(credentialsProvider)
                                        .call()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Git error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                showProgress.value = false
                            }
                        }
                    }
                )
                GitActionRow(
                    iconRes = UiR.drawable.ic_folder_data,
                    title = "Checkout branch",
                    subtitle = "Change or create local repo branch",
                    onClick = { showCheckoutDialog.value = true }
                )
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked
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
                isLoading = false
            ),
            repoPath = "/sdcard/my-project",
            credentialsUsername = "test",
            credentialsToken = "ghp_000000",
            userEmail = "mail@example.com",
            userName = "test"
        )
    }
}*/