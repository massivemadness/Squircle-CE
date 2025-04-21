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
import androidx.compose.foundation.lazy.LazyColumn

@Composable
internal fun GitScreen(
    navArgs: GitDialog,
    navController: NavController
) {
    GitScreen(
        repoPath = navArgs.repoPath,
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun GitScreen(
    repoPath: String,
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = "Git",
        content = {
            LazyColumn {
                // todo
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun GitScreenPreview() {
    PreviewBackground {
        GitScreen(
            repoPath = "/sdcard/my-project",
        )
    }
}