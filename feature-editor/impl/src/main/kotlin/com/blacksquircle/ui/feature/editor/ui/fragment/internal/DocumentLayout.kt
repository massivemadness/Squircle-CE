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

package com.blacksquircle.ui.feature.editor.ui.fragment.internal

import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.feature.editor.data.model.EditorSettings
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.fragment.model.ErrorAction
import com.blacksquircle.ui.feature.editor.ui.fragment.view.CodeEditor
import com.blacksquircle.ui.feature.editor.ui.fragment.view.TextContent
import com.blacksquircle.ui.feature.editor.ui.fragment.view.syncScroll
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula

@Composable
internal fun DocumentLayout(
    documentState: DocumentState,
    settings: EditorSettings,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onErrorActionClicked: (ErrorAction) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        val content = documentState.content
        val isError = documentState.errorState != null
        if (!isError && !isLoading && content != null) {
            CodeEditor(
                content = documentState.content,
                settings = settings,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (isError && !isLoading) {
            ErrorStatus(
                errorState = documentState.errorState,
                onActionClicked = onErrorActionClicked,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (isLoading) {
            CircularProgress(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun CodeEditor(
    content: TextContent,
    settings: EditorSettings,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            CodeEditor(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = { editor ->
            editor.setTextSize(settings.fontSize)
            editor.isWordwrap = settings.wordWrap
            editor.isScalable = settings.pinchZoom
            editor.isLineNumberEnabled = settings.lineNumbers
            editor.isHighlightCurrentLine = settings.highlightCurrentLine
            editor.isHighlightCurrentBlock = true // TODO new setting
            editor.isHighlightBracketPair = settings.highlightMatchingDelimiters
            editor.isEditable = !settings.readOnly
            editor.tabWidth = settings.tabWidth
            editor.typefaceText = settings.fontType
            editor.typefaceLineNumber = settings.fontType
            editor.colorScheme = SchemeDarcula() // TODO
            editor.setText(content)
            editor.syncScroll()
        },
        onRelease = CodeEditor::release,
        modifier = modifier,
    )
}