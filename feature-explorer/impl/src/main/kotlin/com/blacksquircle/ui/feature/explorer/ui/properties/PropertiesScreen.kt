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

package com.blacksquircle.ui.feature.explorer.ui.properties

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.api.navigation.PropertiesDialog
import com.blacksquircle.ui.feature.explorer.data.utils.formatDate
import com.blacksquircle.ui.feature.explorer.data.utils.formatSize
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.blacksquircle.ui.filesystem.base.utils.hasFlag
import com.blacksquircle.ui.filesystem.base.utils.plusFlag

@Composable
internal fun PropertiesScreen(
    navArgs: PropertiesDialog,
    navController: NavController,
) {
    PropertiesScreen(
        fileName = navArgs.fileName,
        filePath = navArgs.filePath,
        fileSize = navArgs.fileSize,
        lastModified = navArgs.lastModified,
        permission = navArgs.permission,
        onCancelClicked = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun PropertiesScreen(
    fileName: String,
    filePath: String,
    fileSize: Long,
    lastModified: Long,
    permission: Int,
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = stringResource(R.string.dialog_title_properties),
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    inputText = fileName,
                    labelText = stringResource(R.string.properties_name),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    inputText = filePath,
                    labelText = stringResource(R.string.properties_path),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    inputText = lastModified.formatDate(
                        pattern = stringResource(R.string.properties_date_format)
                    ),
                    labelText = stringResource(R.string.properties_modified),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    inputText = fileSize.formatSize(),
                    labelText = stringResource(R.string.properties_size),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    CheckBox(
                        title = stringResource(R.string.properties_readable),
                        checked = permission hasFlag Permission.OWNER_READ,
                    )
                    CheckBox(
                        title = stringResource(R.string.properties_writable),
                        checked = permission hasFlag Permission.OWNER_WRITE,
                    )
                    CheckBox(
                        title = stringResource(R.string.properties_executable),
                        checked = permission hasFlag Permission.OWNER_EXECUTE,
                    )
                }
            }
        },
        dismissButton = stringResource(android.R.string.cancel),
        onDismissClicked = onCancelClicked,
        onDismiss = onCancelClicked,
    )
}

@PreviewLightDark
@Composable
private fun PropertiesScreenPreview() {
    PreviewBackground {
        PropertiesScreen(
            fileName = "untitled.txt",
            filePath = "/storage/emulated/0/untitled.txt",
            fileSize = 1024 * 1024,
            lastModified = System.currentTimeMillis(),
            permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
        )
    }
}