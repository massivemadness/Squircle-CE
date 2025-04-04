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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.blacksquircle.ui.ds.R as UiR

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CompactFileItem(
    fileModel: FileModel,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) {
                    SquircleTheme.colors.colorBackgroundTertiary
                } else {
                    Color.Transparent
                }
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(
                vertical = 4.dp,
                horizontal = 12.dp,
            )
    ) {
        val icon = when {
            fileModel.directory -> UiR.drawable.ic_folder
            fileModel.type == FileType.TEXT -> UiR.drawable.ic_file_document
            fileModel.type == FileType.ARCHIVE -> UiR.drawable.ic_folder_zip
            fileModel.type == FileType.IMAGE -> UiR.drawable.ic_file_image
            fileModel.type == FileType.AUDIO -> UiR.drawable.ic_file_music
            fileModel.type == FileType.VIDEO -> UiR.drawable.ic_file_video
            else -> UiR.drawable.ic_file
        }
        val tint = when {
            fileModel.directory -> SquircleTheme.colors.colorTextAndIconAdditional
            fileModel.type == FileType.ARCHIVE -> SquircleTheme.colors.colorTextAndIconAdditional
            else -> SquircleTheme.colors.colorTextAndIconSecondary
        }
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint.copy(alpha = if (fileModel.isHidden) 0.45f else 1f),
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = fileModel.name,
            color = SquircleTheme.colors.colorTextAndIconSecondary,
            style = SquircleTheme.typography.text16Regular,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewLightDark
@Composable
private fun FileItemPreview() {
    PreviewBackground {
        Column {
            CompactFileItem(
                fileModel = FileModel(
                    fileUri = "file:///storage/emulated/0/Download",
                    filesystemUuid = "123",
                    size = 1024 * 1024,
                    lastModified = System.currentTimeMillis(),
                    directory = true,
                    permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                )
            )
            CompactFileItem(
                fileModel = FileModel(
                    fileUri = "file:///storage/emulated/0/untitled.txt",
                    filesystemUuid = "123",
                    size = 1024 * 1024,
                    lastModified = System.currentTimeMillis(),
                    directory = false,
                    permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                )
            )
            CompactFileItem(
                fileModel = FileModel(
                    fileUri = "file:///storage/emulated/0/.nomedia",
                    filesystemUuid = "123",
                    size = 1024 * 1024,
                    lastModified = System.currentTimeMillis(),
                    directory = false,
                    permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                )
            )
        }
    }
}