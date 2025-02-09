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

package com.blacksquircle.ui.feature.explorer.ui.dialog

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
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.checkbox.CheckBox
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.toReadableDate
import com.blacksquircle.ui.feature.explorer.data.utils.toReadableSize
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.blacksquircle.ui.filesystem.base.utils.hasFlag
import com.blacksquircle.ui.filesystem.base.utils.plusFlag

@Composable
internal fun PropertiesScreen(
    fileModel: FileModel,
    onCancelClicked: () -> Unit = {}
) {
    AlertDialog(
        title = stringResource(R.string.dialog_title_properties),
        content = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(
                    inputText = fileModel.name,
                    labelText = stringResource(R.string.properties_name),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    inputText = fileModel.path,
                    labelText = stringResource(R.string.properties_path),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                val pattern = stringResource(R.string.properties_date_format)
                TextField(
                    inputText = fileModel.lastModified.toReadableDate(pattern),
                    labelText = stringResource(R.string.properties_modified),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                TextField(
                    inputText = fileModel.size.toReadableSize(),
                    labelText = stringResource(R.string.properties_size),
                    readOnly = true,
                )

                Spacer(Modifier.height(8.dp))

                Row {
                    CheckBox(
                        title = stringResource(R.string.properties_readable),
                        checked = fileModel.permission hasFlag Permission.OWNER_READ,
                    )
                    CheckBox(
                        title = stringResource(R.string.properties_writable),
                        checked = fileModel.permission hasFlag Permission.OWNER_WRITE,
                    )
                    CheckBox(
                        title = stringResource(R.string.properties_executable),
                        checked = fileModel.permission hasFlag Permission.OWNER_EXECUTE,
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
            fileModel = FileModel(
                fileUri = "file:///storage/emulated/0/untitled",
                filesystemUuid = "123",
                size = 1024 * 1024,
                lastModified = System.currentTimeMillis(),
                directory = true,
                permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
            )
        )
    }
}