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

package com.blacksquircle.ui.feature.editor.ui.editor

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blacksquircle.ui.core.contract.ContractResult
import com.blacksquircle.ui.core.contract.MimeType
import com.blacksquircle.ui.core.contract.rememberCreateFileContract
import com.blacksquircle.ui.core.contract.rememberOpenFileContract
import com.blacksquircle.ui.core.effect.CleanupEffect
import com.blacksquircle.ui.core.effect.NavResultEffect
import com.blacksquircle.ui.core.extensions.daggerViewModel
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.divider.HorizontalDivider
import com.blacksquircle.ui.ds.drawer.DrawerState
import com.blacksquircle.ui.ds.drawer.rememberDrawerState
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.ds.scaffold.ScaffoldSuite
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.domain.model.DocumentModel
import com.blacksquircle.ui.feature.editor.internal.EditorComponent
import com.blacksquircle.ui.feature.editor.ui.editor.compose.CodeEditor
import com.blacksquircle.ui.feature.editor.ui.editor.compose.DocumentNavigation
import com.blacksquircle.ui.feature.editor.ui.editor.compose.EditorToolbar
import com.blacksquircle.ui.feature.editor.ui.editor.compose.ErrorStatus
import com.blacksquircle.ui.feature.editor.ui.editor.compose.ExtendedKeyboard
import com.blacksquircle.ui.feature.editor.ui.editor.compose.SearchPanel
import com.blacksquircle.ui.feature.editor.ui.editor.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.editor.model.EditorController
import com.blacksquircle.ui.feature.editor.ui.editor.model.ErrorAction
import com.blacksquircle.ui.feature.editor.ui.editor.model.rememberEditorController
import com.blacksquircle.ui.feature.explorer.ui.DrawerExplorer
import com.blacksquircle.ui.feature.git.api.navigation.CheckoutDialog.Companion.KEY_CHECKOUT
import com.blacksquircle.ui.feature.git.api.navigation.PullDialog.Companion.KEY_PULL
import kotlinx.coroutines.launch
import com.blacksquircle.ui.ds.R as UiR

internal const val KEY_CLOSE_FILE = "KEY_CLOSE_FILE"
internal const val KEY_SELECT_LANGUAGE = "KEY_SELECT_LANGUAGE"
internal const val KEY_GOTO_LINE = "KEY_GOTO_LINE"
internal const val KEY_INSERT_COLOR = "KEY_INSERT_COLOR"

internal const val ARG_FILE_UUID = "ARG_FILE_UUID"
internal const val ARG_LANGUAGE = "ARG_LANGUAGE"
internal const val ARG_LINE_NUMBER = "ARG_LINE_NUMBER"
internal const val ARG_COLOR = "ARG_COLOR"

@Composable
internal fun EditorScreen(
    navController: NavController,
    viewModel: EditorViewModel = daggerViewModel { context ->
        val component = EditorComponent.buildOrGet(context)
        EditorViewModel.Factory().also(component::inject)
    },
) {
    val scope = rememberCoroutineScope()
    val editorController = rememberEditorController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    EditorScreen(
        viewState = viewState,
        drawerState = drawerState,
        editorController = editorController,
        onDrawerClicked = {
            scope.launch {
                if (drawerState.isOpen) {
                    drawerState.close()
                } else {
                    drawerState.open()
                }
            }
        },
        onNewFileClicked = viewModel::onNewFileClicked,
        onOpenFileClicked = viewModel::onOpenFileClicked,
        onSaveFileClicked = viewModel::onSaveFileClicked,
        onSaveFileAsClicked = viewModel::onSaveFileAsClicked,
        onRefreshFileClicked = viewModel::onRefreshFileClicked,
        onCloseFileClicked = viewModel::onCloseFileClicked,
        onContentChanged = viewModel::onContentChanged,
        onShortcutPressed = viewModel::onShortcutPressed,
        onCutClicked = viewModel::onCutClicked,
        onCopyClicked = viewModel::onCopyClicked,
        onPasteClicked = viewModel::onPasteClicked,
        onSelectAllClicked = viewModel::onSelectAllClicked,
        onSelectLineClicked = viewModel::onSelectLineClicked,
        onDeleteLineClicked = viewModel::onDeleteLineClicked,
        onDuplicateLineClicked = viewModel::onDuplicateLineClicked,
        onForceSyntaxClicked = viewModel::onForceSyntaxClicked,
        onInsertColorClicked = viewModel::onInsertColorClicked,
        onToggleFindClicked = viewModel::onToggleFindClicked,
        onToggleReplaceClicked = viewModel::onToggleReplaceClicked,
        onFindTextChanged = viewModel::onFindTextChanged,
        onReplaceTextChanged = viewModel::onReplaceTextChanged,
        onRegexClicked = viewModel::onRegexClicked,
        onMatchCaseClicked = viewModel::onMatchCaseClicked,
        onWordsOnlyClicked = viewModel::onWordsOnlyClicked,
        onPreviousMatchClicked = viewModel::onPreviousMatchClicked,
        onNextMatchClicked = viewModel::onNextMatchClicked,
        onReplaceMatchClicked = viewModel::onReplaceMatchClicked,
        onReplaceAllClicked = viewModel::onReplaceAllClicked,
        onUndoClicked = viewModel::onUndoClicked,
        onRedoClicked = viewModel::onRedoClicked,
        onSettingsClicked = viewModel::onSettingsClicked,
        onGitClicked = viewModel::onGitClicked,
        onDocumentClicked = viewModel::onDocumentClicked,
        onDocumentMoved = viewModel::onDocumentMoved,
        onCloseClicked = viewModel::onCloseClicked,
        onCloseOthersClicked = viewModel::onCloseOthersClicked,
        onCloseAllClicked = viewModel::onCloseAllClicked,
        onErrorActionClicked = viewModel::onErrorActionClicked,
        onExtraKeyClicked = viewModel::onExtraKeyClicked,
        onExtraOptionsClicked = viewModel::onExtraOptionsClicked,
    )

    val defaultFileName = stringResource(UiR.string.common_untitled)
    val newFileContract = rememberCreateFileContract(MimeType.TEXT) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onFileOpened(result.uri)
            is ContractResult.Canceled -> Unit
        }
    }
    val openFileContract = rememberOpenFileContract { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onFileOpened(result.uri)
            is ContractResult.Canceled -> Unit
        }
    }
    val saveFileContract = rememberCreateFileContract(MimeType.TEXT) { result ->
        when (result) {
            is ContractResult.Success -> viewModel.onSaveFileSelected(result.uri)
            is ContractResult.Canceled -> Unit
        }
    }

    val activity = LocalActivity.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collect { event ->
            when (event) {
                is ViewEvent.Toast -> context.showToast(text = event.message)
                is ViewEvent.Navigation -> navController.navigate(event.screen)
                is ViewEvent.PopBackStack -> {
                    if (!navController.popBackStack()) {
                        activity?.finish()
                    }
                }
                is EditorViewEvent.CreateFileContract -> {
                    newFileContract.launch(defaultFileName)
                }
                is EditorViewEvent.OpenFileContract -> {
                    openFileContract.launch(arrayOf(MimeType.ANY))
                }
                is EditorViewEvent.SaveAsFileContract -> {
                    saveFileContract.launch(event.fileName)
                }
                is EditorViewEvent.Command -> {
                    scope.launch {
                        editorController.send(event.command)
                    }
                }
            }
        }
    }

    NavResultEffect(KEY_CLOSE_FILE) { bundle ->
        val fileUuid = bundle.getString(ARG_FILE_UUID).orEmpty()
        viewModel.onCloseModifiedClicked(fileUuid)
    }
    NavResultEffect(KEY_SELECT_LANGUAGE) { bundle ->
        val language = bundle.getString(ARG_LANGUAGE).orEmpty()
        viewModel.onLanguageChanged(language)
    }
    NavResultEffect(KEY_GOTO_LINE) { bundle ->
        val lineNumber = bundle.getInt(ARG_LINE_NUMBER)
        viewModel.onLineSelected(lineNumber)
    }
    NavResultEffect(KEY_INSERT_COLOR) { bundle ->
        val color = bundle.getInt(ARG_COLOR)
        viewModel.onColorSelected(color)
    }
    NavResultEffect(KEY_PULL) {
        viewModel.onRefreshFileClicked()
    }
    NavResultEffect(KEY_CHECKOUT) {
        viewModel.onRefreshFileClicked()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onResumed()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.onPaused()
    }

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            viewModel.onBackClicked()
        }
    }

    CleanupEffect {
        EditorComponent.release()
    }

    ReportDrawnWhen {
        !viewState.isLoading
    }
}

@Composable
private fun EditorScreen(
    viewState: EditorViewState,
    editorController: EditorController,
    drawerState: DrawerState,
    onDrawerClicked: () -> Unit = {},
    onNewFileClicked: () -> Unit = {},
    onOpenFileClicked: () -> Unit = {},
    onSaveFileClicked: () -> Unit = {},
    onSaveFileAsClicked: () -> Unit = {},
    onRefreshFileClicked: () -> Unit = {},
    onCloseFileClicked: () -> Unit = {},
    onContentChanged: () -> Unit = {},
    onShortcutPressed: (Boolean, Boolean, Boolean, Int) -> Unit = { _, _, _, _ -> },
    onCutClicked: () -> Unit = {},
    onCopyClicked: () -> Unit = {},
    onPasteClicked: () -> Unit = {},
    onSelectAllClicked: () -> Unit = {},
    onSelectLineClicked: () -> Unit = {},
    onDeleteLineClicked: () -> Unit = {},
    onDuplicateLineClicked: () -> Unit = {},
    onForceSyntaxClicked: () -> Unit = {},
    onInsertColorClicked: () -> Unit = {},
    onToggleFindClicked: () -> Unit = {},
    onToggleReplaceClicked: () -> Unit = {},
    onFindTextChanged: (String) -> Unit = {},
    onReplaceTextChanged: (String) -> Unit = {},
    onRegexClicked: () -> Unit = {},
    onMatchCaseClicked: () -> Unit = {},
    onWordsOnlyClicked: () -> Unit = {},
    onPreviousMatchClicked: () -> Unit = {},
    onNextMatchClicked: () -> Unit = {},
    onReplaceMatchClicked: () -> Unit = {},
    onReplaceAllClicked: () -> Unit = {},
    onUndoClicked: () -> Unit = {},
    onRedoClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onGitClicked: () -> Unit = {},
    onDocumentClicked: (DocumentModel) -> Unit = {},
    onDocumentMoved: (from: Int, to: Int) -> Unit = { _, _ -> },
    onCloseClicked: (DocumentModel) -> Unit = {},
    onCloseOthersClicked: (DocumentModel) -> Unit = {},
    onCloseAllClicked: () -> Unit = {},
    onErrorActionClicked: (ErrorAction) -> Unit = {},
    onExtraKeyClicked: (Char) -> Unit = {},
    onExtraOptionsClicked: () -> Unit = {},
) {
    ScaffoldSuite(
        topBar = {
            EditorToolbar(
                canUndo = viewState.canUndo,
                canRedo = viewState.canRedo,
                onDrawerClicked = onDrawerClicked,
                onNewFileClicked = onNewFileClicked,
                onOpenFileClicked = onOpenFileClicked,
                onSaveFileClicked = onSaveFileClicked,
                onSaveFileAsClicked = onSaveFileAsClicked,
                onRefreshFileClicked = onRefreshFileClicked,
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
                onFindClicked = onToggleFindClicked,
                onUndoClicked = onUndoClicked,
                onRedoClicked = onRedoClicked,
                onSettingsClicked = onSettingsClicked,
                onGitClicked = onGitClicked
            )
        },
        bottomBar = {
            if (viewState.showExtendedKeyboard) {
                ExtendedKeyboard(
                    preset = viewState.settings.keyboardPreset,
                    showExtraKeys = viewState.showExtraKeys,
                    onExtraKeyClicked = onExtraKeyClicked,
                    onExtraOptionsClicked = onExtraOptionsClicked,
                    onOpenFileClicked = onOpenFileClicked,
                    onSaveFileClicked = onSaveFileClicked,
                    onCloseFileClicked = onCloseFileClicked,
                    onUndoClicked = onUndoClicked,
                    onRedoClicked = onRedoClicked,
                )
            }
        },
        drawerState = drawerState,
        drawerGesturesEnabled = drawerState.isOpen,
        drawerContent = {
            if (LocalInspectionMode.current) {
                return@ScaffoldSuite
            }
            DrawerExplorer(onDrawerClicked)
        },
        modifier = Modifier.imePadding(),
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            DocumentNavigation(
                tabs = viewState.documents,
                selectedIndex = viewState.selectedDocument,
                onDocumentClicked = { onDocumentClicked(it.document) },
                onDocumentMoved = onDocumentMoved,
                onCloseClicked = { onCloseClicked(it.document) },
                onCloseOthersClicked = { onCloseOthersClicked(it.document) },
                onCloseAllClicked = onCloseAllClicked,
                modifier = Modifier.fillMaxWidth(),
            )

            val currentDocument = viewState.currentDocument
            val content = currentDocument?.content
            val searchState = currentDocument?.searchState

            val isError = viewState.isError
            val isLoading = viewState.isLoading
            val isEmpty = viewState.isEmpty

            if (!isError && !isLoading && searchState != null) {
                SearchPanel(
                    searchState = searchState,
                    onFindTextChanged = onFindTextChanged,
                    onReplaceTextChanged = onReplaceTextChanged,
                    onToggleReplaceClicked = onToggleReplaceClicked,
                    onRegexClicked = onRegexClicked,
                    onMatchCaseClicked = onMatchCaseClicked,
                    onWordsOnlyClicked = onWordsOnlyClicked,
                    onCloseSearchClicked = onToggleFindClicked,
                    onPreviousMatchClicked = onPreviousMatchClicked,
                    onNextMatchClicked = onNextMatchClicked,
                    onReplaceMatchClicked = onReplaceMatchClicked,
                    onReplaceAllClicked = onReplaceAllClicked,
                )
                HorizontalDivider()
            }

            if (!isError && !isLoading && content != null) {
                key(currentDocument.document.uuid) {
                    CodeEditor(
                        content = currentDocument.content,
                        language = currentDocument.document.language,
                        settings = viewState.settings,
                        controller = editorController,
                        onContentChanged = onContentChanged,
                        onShortcutPressed = onShortcutPressed,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            if (isError && !isLoading) {
                Box(Modifier.fillMaxSize()) {
                    ErrorStatus(
                        errorState = currentDocument?.errorState,
                        onActionClicked = onErrorActionClicked,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (isEmpty && !isLoading) {
                Box(Modifier.fillMaxSize()) {
                    EmptyView(
                        iconResId = UiR.drawable.ic_file_find,
                        title = stringResource(R.string.message_no_open_files),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            if (isLoading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgress(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
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
                selectedDocument = 0,
                isLoading = true,
            ),
            editorController = rememberEditorController(),
            drawerState = rememberDrawerState(DrawerValue.Closed)
        )
    }
}