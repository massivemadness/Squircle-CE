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
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blacksquircle.ui.core.factory.LanguageFactory
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.ds.tabs.Tab
import com.blacksquircle.ui.ds.tabs.TabNavigation
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.ui.fragment.internal.DocumentLayout
import com.blacksquircle.ui.feature.editor.ui.fragment.internal.EditorToolbar
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun EditorScreen(viewModel: EditorViewModel) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    EditorScreen(
        viewState = viewState,
        onMenuClicked = viewModel::onMenuClicked,
    )
}

@Composable
private fun EditorScreen(
    viewState: EditorViewState,
    onMenuClicked: () -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            EditorToolbar(
                onMenuClicked = onMenuClicked,
            )
        },
        modifier = Modifier.imePadding(),
    ) { contentPadding ->
        Column(Modifier.fillMaxSize()) {
            TabNavigation(
                tabs = {
                    viewState.documents.fastForEachIndexed { index, state ->
                        Tab(
                            title = state.document.name,
                            iconResId = UiR.drawable.ic_close,
                            selected = index == viewState.selectedDocument,
                            onClick = {},
                            onActionClick = {},
                        )
                    }
                },
                selectedIndex = viewState.selectedDocument,
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
                        autoRefresh = false,
                    )
                ),
                selectedDocument = 0,
                isLoading = true,
            )
        )
    }
}