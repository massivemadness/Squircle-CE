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
import com.blacksquircle.ui.ds.SquircleTheme
 import androidx.compose.ui.Modifier
 import androidx.compose.foundation.layout.Column
 import androidx.compose.foundation.layout.Row
 import androidx.compose.material.Text
 import androidx.compose.material.Icon
 import androidx.compose.foundation.layout.Spacer
 import androidx.compose.ui.res.painterResource
 import com.blacksquircle.ui.ds.R as UiR
 import androidx.compose.ui.unit.dp
 import androidx.compose.foundation.layout.width
 import androidx.compose.foundation.layout.fillMaxWidth
 import androidx.compose.foundation.clickable
 import androidx.compose.foundation.layout.padding

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
        verticalScroll = false,
        horizontalPadding = false,
        content = {
            Column {
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .clickable { /* ничего не делаем пока */ }
                         .padding(16.dp)
                 ) {
                     Icon(
                         painter = painterResource(id = UiR.drawable.ic_git),
                         contentDescription = null
                     )
                     Spacer(modifier = Modifier.width(16.dp))
                     Column {
                         Text(text = "Pull", style = SquircleTheme.typography.text18Regular)
                         Text(text = "Pull content from remote repo", style = SquircleTheme.typography.text16Regular)
                     }
                 }
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .clickable { /* ничего не делаем пока */ }
                         .padding(16.dp)
                 ) {
                     Icon(
                         painter = painterResource(id = UiR.drawable.ic_git),
                         contentDescription = null
                     )
                     Spacer(modifier = Modifier.width(16.dp))
                     Column {
                         Text(text = "Commit", style = SquircleTheme.typography.text18Regular)
                         Text(text = "Commit local repo changes", style = SquircleTheme.typography.text16Regular)
                     }
                 }
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .clickable { /* ничего не делаем пока */ }
                         .padding(16.dp)
                 ) {
                     Icon(
                         painter = painterResource(id = UiR.drawable.ic_git),
                         contentDescription = null
                     )
                     Spacer(modifier = Modifier.width(16.dp))
                     Column {
                         Text(text = "Push", style = SquircleTheme.typography.text18Regular)
                         Text(text = "Push content to remote repo", style = SquircleTheme.typography.text16Regular)
                     }
                 }
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .clickable { /* ничего не делаем пока */ }
                         .padding(16.dp)
                 ) {
                     Icon(
                         painter = painterResource(id = UiR.drawable.ic_git),
                         contentDescription = null
                     )
                     Spacer(modifier = Modifier.width(16.dp))
                     Column {
                         Text(text = "Checkout branch", style = SquircleTheme.typography.text18Regular)
                         Text(text = "Change local repo branch", style = SquircleTheme.typography.text16Regular)
                     }
                 }
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
            repoPath = "/sdcard/my-project"
        )
    }
}