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

package com.blacksquircle.ui.feature.editor.ui.editor.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.tabs.TabLayout
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private const val DragAlpha = 0.5f
private const val IdleAlpha = 1.0f

@Composable
internal fun DocumentTabLayout(
    tabs: List<DocumentState>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    onDocumentClicked: (DocumentState) -> Unit = {},
    onDocumentMoved: (from: Int, to: Int) -> Unit = { _, _ -> },
    onCloseClicked: (DocumentState) -> Unit = {},
    onCloseOthersClicked: (DocumentState) -> Unit = {},
    onCloseAllClicked: () -> Unit = {},
    selectedIndex: Int = -1,
) {
    val reorderHapticFeedback = rememberReorderHapticFeedback()
    val reorderableLazyListState = rememberReorderableLazyListState(state) { from, to ->
        onDocumentMoved(from.index, to.index)
        reorderHapticFeedback.perform(ReorderHapticFeedbackType.MOVE)
    }

    TabLayout(
        state = state,
        modifier = modifier,
    ) {
        itemsIndexed(
            items = tabs,
            key = { _, state -> state.document.uuid }
        ) { i, state ->
            ReorderableItem(reorderableLazyListState, state.document.uuid) { isDragging ->
                DocumentTab(
                    name = state.document.displayName,
                    modified = state.document.modified,
                    selected = i == selectedIndex,
                    onDocumentClicked = { onDocumentClicked(state) },
                    onCloseClicked = { onCloseClicked(state) },
                    onCloseOthersClicked = { onCloseOthersClicked(state) },
                    onCloseAllClicked = onCloseAllClicked,
                    modifier = Modifier
                        .alpha(if (isDragging) DragAlpha else IdleAlpha)
                        .longPressDraggableHandle(
                            onDragStarted = {
                                reorderHapticFeedback.perform(ReorderHapticFeedbackType.START)
                            },
                            onDragStopped = {
                                reorderHapticFeedback.perform(ReorderHapticFeedbackType.END)
                            },
                        ),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DocumentNavigationPreview() {
    PreviewBackground {
        DocumentTabLayout(
            selectedIndex = 0,
            tabs = listOf(
                DocumentState(
                    document = DocumentModel(
                        uuid = "123",
                        fileUri = "file://storage/emulated/0/Downloads/untitled.txt",
                        filesystemUuid = "local",
                        displayName = "untitled.txt",
                        language = "plaintext",
                        modified = false,
                        position = 0,
                        scrollX = 0,
                        scrollY = 0,
                        selectionStart = 0,
                        selectionEnd = 0,
                        gitRepository = null,
                    ),
                )
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}