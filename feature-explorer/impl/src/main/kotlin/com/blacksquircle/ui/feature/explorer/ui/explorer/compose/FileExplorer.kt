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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.util.fastAny
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode
import com.blacksquircle.ui.filesystem.base.model.FileModel

@Composable
internal fun FileExplorer(
    contentPadding: PaddingValues,
    fileNodes: List<FileNode>,
    selectedFiles: List<FileModel>,
    modifier: Modifier = Modifier,
    onFileClicked: (FileNode) -> Unit = {},
    onFileSelected: (FileNode) -> Unit = {},
    onErrorActionClicked: (ErrorAction) -> Unit = {},
    onRefreshClicked: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val hapticFeedback = LocalHapticFeedback.current

    LazyColumn(
        state = lazyListState,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = fileNodes,
            key = { it.file.fileUri },
        ) { fileNode ->
            val isSelected = selectedFiles.fastAny { it.fileUri == fileNode.file.fileUri }
            FileItem(
                fileNode = fileNode,
                isSelected = isSelected,
                onClick = { onFileClicked(fileNode) },
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFileSelected(fileNode)
                },
                modifier = Modifier.animateItem(),
            )
        }
    }
}