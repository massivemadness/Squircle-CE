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

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.util.fastAny
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.FileNode

@Composable
internal fun FileExplorer(
    contentPadding: PaddingValues,
    fileNodes: List<FileNode>,
    selectedNodes: List<FileNode>,
    modifier: Modifier = Modifier,
    onFileClicked: (FileNode) -> Unit = {},
    onFileSelected: (FileNode) -> Unit = {},
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    var cacheItemWidth by remember {
        mutableStateOf(MinItemWidth)
    }

    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {
        items(
            items = fileNodes,
            key = { it.file.fileUri },
        ) { fileNode ->
            val isSelected = selectedNodes.fastAny { it.key == fileNode.key }
            FileItem(
                fileNode = fileNode,
                isSelected = isSelected,
                onClick = { onFileClicked(fileNode) },
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFileSelected(fileNode)
                },
                modifier = Modifier
                    .onSizeChanged { size ->
                        val itemWidthDp = with(density) { size.width.toDp() }
                        if (cacheItemWidth < itemWidthDp) {
                            cacheItemWidth = itemWidthDp
                        }
                    }
                    .widthIn(min = cacheItemWidth)
                    .animateItem()
            )
        }
    }
}