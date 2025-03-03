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

package com.blacksquircle.ui.feature.editor.ui.fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.factory.LanguageFactory
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.ui.fragment.internal.DocumentLayout
import com.blacksquircle.ui.feature.editor.ui.fragment.internal.DocumentNavigation
import com.blacksquircle.ui.feature.editor.ui.fragment.internal.EditorToolbar
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel

@Composable
internal fun EditorScreen(viewModel: EditorViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    EditorScreen(
        viewState = viewState,
        onDrawerClicked = viewModel::onDrawerClicked,
        onNewFileClicked = {},
        onOpenFileClicked = {},
        onSaveFileClicked = {},
        onSaveFileAsClicked = {},
        onCloseFileClicked = {},
        onCutClicked = {},
        onCopyClicked = {},
        onPasteClicked = {},
        onSelectAllClicked = {},
        onSelectLineClicked = {},
        onDeleteLineClicked = {},
        onDuplicateLineClicked = {},
        onForceSyntaxClicked = {},
        onInsertColorClicked = {},
        onFindClicked = {},
        onUndoClicked = {},
        onRedoClicked = {},
        onSettingsClicked = viewModel::onSettingsClicked,
        onDocumentClicked = viewModel::onDocumentClicked,
        onCloseDocumentClicked = viewModel::onCloseDocumentClicked,
    )
}

@Composable
private fun EditorScreen(
    viewState: EditorViewState,
    onDrawerClicked: () -> Unit = {},
    onNewFileClicked: () -> Unit = {},
    onOpenFileClicked: () -> Unit = {},
    onSaveFileClicked: () -> Unit = {},
    onSaveFileAsClicked: () -> Unit = {},
    onCloseFileClicked: () -> Unit = {},
    onCutClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onSelectLineClicked: () -> Unit = {},
    onDeleteLineClicked: () -> Unit = {},
    onDuplicateLineClicked: () -> Unit = {},
    onForceSyntaxClicked: () -> Unit = {},
    onInsertColorClicked: () -> Unit = {},
    onFindClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onRedoClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onDocumentClicked: (DocumentState) -> Unit = {},
    onCloseDocumentClicked: (DocumentState) -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            EditorToolbar(
                onDrawerClicked = onDrawerClicked,
                onNewFileClicked = onNewFileClicked,
                onOpenFileClicked = onOpenFileClicked,
                onSaveFileClicked = onSaveFileClicked,
                onSaveFileAsClicked = onSaveFileAsClicked,
                onCloseFileClicked = onCloseFileClicked,
                onCutClicked = onCutClicked,
                onCopyClicked = onCopyClicked,
                onPasteClicked = onPasteClicked,
                onSelectAllClicked = onSelectAllClicked,
                onSelectLineClicked = onSelectLineClicked,
                onDeleteLineClicked = onDeleteLineClicked,
                onDuplicateLineClicked = onDuplicateLineClicked,
                onForceSyntaxClicked = onForceSyntaxClicked,
                onInsertColorClicked = onInsertColorClicked,
                onFindClicked = onFindClicked,
                onUndoClicked = onUndoClicked,
                onRedoClicked = onRedoClicked,
                onSettingsClicked = onSettingsClicked,
            )
        },
        modifier = Modifier.imePadding(),
    ) { contentPadding ->
        Column(Modifier.fillMaxSize()) {
            DocumentNavigation(
                tabs = viewState.documents,
                selectedIndex = viewState.selectedDocument,
                onDocumentClicked = onDocumentClicked,
                onCloseDocumentClicked = onCloseDocumentClicked,
                modifier = Modifier.fillMaxWidth(),
            )

            HorizontalDivider()

            val documentState = viewState.documents
                .getOrNull(viewState.selectedDocument)
            if (documentState != null) {
                DocumentLayout(
                    contentPadding = contentPadding,
                    documentState = documentState,
                    isLoading = viewState.isLoading,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditorScreenPreview() {
    PreviewBackground {
        EditorScreen(
            viewState = EditorViewState(
                documents = listOf(
                    DocumentState(
                        document = DocumentModel(
                            uuid = "123",
                            fileUri = "file://storage/emulated/0/Downloads/untitled.txt",
                            filesystemUuid = "123",
                            language = LanguageFactory.fromName("plaintext"),
                            modified = false,
                            position = 0,
                            scrollX = 0,
                            scrollY = 0,
                            selectionStart = 0,
                            selectionEnd = 0,
                        ),
                        content = null,
                        errorState = null,
                    )
                ),
                selectedDocument = 0,
                isLoading = true,
            )
        )
    }
}