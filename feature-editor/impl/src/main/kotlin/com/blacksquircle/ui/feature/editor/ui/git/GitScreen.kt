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
import com.blacksquircle.ui.feature.editor.api.navigation.GitDialog
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.dialog.AlertDialog
import androidx.compose.ui.res.stringResource
import com.blacksquircle.ui.ds.PreviewBackground
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import com.blacksquircle.ui.ds.R as UiR
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import androidx.compose.foundation.layout.fillMaxWidth
import com.blacksquircle.ui.ds.progress.LinearProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.blacksquircle.ui.ds.textfield.TextField

@Composable
internal fun GitScreen(
    navArgs: GitDialog,
    navController: NavController
) {
    GitScreen(
        repoPath = navArgs.repoPath,
        credentials = navArgs.credentials,
        user = navArgs.user,
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun GitScreen(
    repoPath: String,
    credentials: String,
    user: String,
    onCancelClicked: () -> Unit = {}
) {
    val auth = credentials.split("::")
    val userData = user.split("::")
    val git = Git.open(File(repoPath))
    val credentialsProvider = UsernamePasswordCredentialsProvider(auth[0], auth[1])
    val showProgress = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    if (showProgress.value) {
        AlertDialog(
            title = "Please wait...",
            content = {
                LinearProgress(indeterminate = true)
            },
            onDismiss = {}
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
                                    git.fetch()
                                        .setRemote("origin")
                                        .setCredentialsProvider(credentialsProvider)
                                        .call()
                                }
                            } catch (e: Exception) {
                                // todo: error toast
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
                                    git.pull()
                                        .setRemote("origin")
                                        .setCredentialsProvider(credentialsProvider)
                                        .call()
                                }
                            } catch (e: Exception) {
                                // todo: error toast
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
                    onClick = {
                        var commitText = ""
                        AlertDialog(
                            title = "Commit",
                            content = {
                                TextField(
                                    inputText = commitText,
                                    onInputChanged = { commitText = inputText }
                                )
                            },
                            onDismiss = onCancelClicked,
                            confirmButton = stringResource(android.R.string.ok),
                            dismissButton = stringResource(android.R.string.cancel),
                            onConfirmClicked = {
                                coroutineScope.launch {
                                    showProgress.value = true
                                    try {
                                        withContext(Dispatchers.IO) {
                                            git.add().addFilepattern(".").call()
                                            git.pull()
                                                .setRemote("origin")
                                                .setCredentialsProvider(credentialsProvider)
                                                .call()
                                        }
                                    } catch (e: Exception) {
                                        // todo: error toast
                                    } finally {
                                        showProgress.value = false
                                    }
                                }
                            },
                            onDismissClicked = onCancelClicked
                        )
                    }
                )
                GitActionRow(
                    iconRes = UiR.drawable.ic_upload,
                    title = "Push",
                    subtitle = "Push content to remote repo",
                    onClick = { /* TODO: Реализовать push */ }
                )
                GitActionRow(
                    iconRes = UiR.drawable.ic_folder_data,
                    title = "Checkout branch",
                    subtitle = "Change local repo branch",
                    onClick = { /* TODO: Реализовать checkout */ }
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
            repoPath = "/sdcard/my-project",
            credentials = "test::ghp_000000",
            user = "mail@example.com::test"
        )
    }
}