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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private const val DragAlpha = 0.5f
private const val IdleAlpha = 1.0f

@Composable
internal fun DocumentNavigation(
    tabs: List<DocumentState>,
    modifier: Modifier = Modifier,
    onDocumentClicked: (DocumentState) -> Unit = {},
    onDocumentMoved: (from: Int, to: Int) -> Unit = { _, _ -> },
    onCloseClicked: (DocumentState) -> Unit = {},
    onCloseOthersClicked: (DocumentState) -> Unit = {},
    onCloseAllClicked: () -> Unit = {},
    selectedIndex: Int = -1,
) {
    val lazyListState = rememberLazyListState()
    val reorderHapticFeedback = rememberReorderHapticFeedback()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onDocumentMoved(from.index, to.index)
        reorderHapticFeedback.perform(ReorderHapticFeedbackType.MOVE)
    }

    Box(modifier) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .zIndex(1f),
        ) {
            itemsIndexed(
                items = tabs,
                key = { _, state -> state.document.uuid }
            ) { i, state ->
                ReorderableItem(reorderableLazyListState, state.document.uuid) { isDragging ->
                    DocumentTab(
                        name = state.document.name,
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

        HorizontalDivider(Modifier.align(Alignment.BottomCenter))
    }
}

@PreviewLightDark
@Composable
private fun DocumentNavigationPreview() {
    PreviewBackground {
        DocumentNavigation(
            selectedIndex = 0,
            tabs = listOf(
                DocumentState(
                    document = DocumentModel(
                        uuid = "123",
                        fileUri = "file://storage/emulated/0/Downloads/untitled.txt",
                        filesystemUuid = "local",
                        language = "plaintext",
                        modified = false,
                        position = 0,
                        scrollX = 0,
                        scrollY = 0,
                        selectionStart = 0,
                        selectionEnd = 0,
                    ),
                )
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}