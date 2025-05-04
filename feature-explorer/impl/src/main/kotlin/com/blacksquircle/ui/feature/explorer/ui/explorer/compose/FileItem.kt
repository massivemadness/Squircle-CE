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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import com.blacksquircle.ui.ds.modifier.debounceClickable
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.progress.CircularProgressSizeDefaults
import com.blacksquircle.ui.ds.progress.CircularProgressStyleDefaults
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.blacksquircle.ui.filesystem.base.utils.plusFlag
import com.blacksquircle.ui.ds.R as UiR

internal val MinItemWidth = 248.dp
internal val MinTextWidth = 178.dp

private val VerticalPadding = 4.dp
private val HorizontalPadding = 12.dp

@Composable
internal fun FileItem(
    fileNode: FileNode,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = if (isSelected) {
                    SquircleTheme.colors.colorBackgroundTertiary
                } else {
                    Color.Transparent
                }
            )
            .debounceClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                debounce = false,
            )
            .padding(
                top = VerticalPadding,
                bottom = VerticalPadding,
                start = HorizontalPadding * fileNode.displayDepth,
                end = HorizontalPadding,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(18.dp)
        ) {
            when {
                fileNode.isLoading -> {
                    CircularProgress(
                        circularProgressStyle = CircularProgressStyleDefaults.Primary,
                        circularProgressSize = CircularProgressSizeDefaults.XS,
                    )
                }
                fileNode.isError -> {
                    Icon(
                        painter = painterResource(UiR.drawable.ic_alert_circle),
                        contentDescription = null,
                        tint = SquircleTheme.colors.colorTextAndIconError,
                    )
                }
                fileNode.isDirectory -> {
                    Icon(
                        painter = if (fileNode.isExpanded) {
                            painterResource(UiR.drawable.ic_arrow_down)
                        } else {
                            painterResource(UiR.drawable.ic_arrow_right)
                        },
                        contentDescription = null,
                        tint = SquircleTheme.colors.colorTextAndIconSecondary,
                    )
                }
            }
        }

        val icon = when {
            fileNode.isDirectory -> UiR.drawable.ic_folder
            fileNode.file.type == FileType.TEXT -> UiR.drawable.ic_file_document
            fileNode.file.type == FileType.ARCHIVE -> UiR.drawable.ic_folder_zip
            fileNode.file.type == FileType.IMAGE -> UiR.drawable.ic_file_image
            fileNode.file.type == FileType.AUDIO -> UiR.drawable.ic_file_music
            fileNode.file.type == FileType.VIDEO -> UiR.drawable.ic_file_video
            else -> UiR.drawable.ic_file
        }
        val tint = when {
            fileNode.isDirectory -> SquircleTheme.colors.colorTextAndIconAdditional
            fileNode.file.type == FileType.ARCHIVE -> SquircleTheme.colors.colorTextAndIconAdditional
            else -> SquircleTheme.colors.colorTextAndIconSecondary
        }
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint.copy(alpha = if (fileNode.isHidden) 0.45f else 1f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = if (fileNode.isRoot) "/" else fileNode.displayName,
            color = SquircleTheme.colors.colorTextAndIconSecondary,
            style = SquircleTheme.typography.text16Regular,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            modifier = Modifier.widthIn(min = MinTextWidth)
        )
    }
}

@PreviewLightDark
@Composable
private fun FileItemPreview() {
    PreviewBackground {
        Column {
            FileItem(
                fileNode = FileNode(
                    file = FileModel(
                        fileUri = "file:///storage/emulated/0/",
                        filesystemUuid = "123",
                        size = 1024 * 1024,
                        lastModified = System.currentTimeMillis(),
                        isDirectory = true,
                        permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                    ),
                    depth = 0,
                    isExpanded = true,
                    isLoading = false,
                ),
            )
            FileItem(
                fileNode = FileNode(
                    file = FileModel(
                        fileUri = "file:///storage/emulated/0/Documents",
                        filesystemUuid = "123",
                        size = 1024 * 1024,
                        lastModified = System.currentTimeMillis(),
                        isDirectory = true,
                        permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                    ),
                    depth = 1,
                    isExpanded = false,
                    isLoading = true,
                ),
            )
            FileItem(
                fileNode = FileNode(
                    file = FileModel(
                        fileUri = "file:///storage/emulated/0/Download",
                        filesystemUuid = "123",
                        size = 1024 * 1024,
                        lastModified = System.currentTimeMillis(),
                        isDirectory = true,
                        permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                    ),
                    depth = 1,
                    isExpanded = true,
                ),
            )
            FileItem(
                fileNode = FileNode(
                    file = FileModel(
                        fileUri = "file:///storage/emulated/0/.nomedia",
                        filesystemUuid = "123",
                        size = 1024 * 1024,
                        lastModified = System.currentTimeMillis(),
                        isDirectory = false,
                        permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                    ),
                    depth = 2,
                )
            )
            FileItem(
                fileNode = FileNode(
                    file = FileModel(
                        fileUri = "file:///storage/emulated/0/untitled.txt",
                        filesystemUuid = "123",
                        size = 1024 * 1024,
                        lastModified = System.currentTimeMillis(),
                        isDirectory = false,
                        permission = Permission.OWNER_READ plusFlag Permission.OWNER_WRITE,
                    ),
                    depth = 2,
                )
            )
        }
    }
}