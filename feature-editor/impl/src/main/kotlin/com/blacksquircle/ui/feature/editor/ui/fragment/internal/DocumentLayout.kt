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

import android.graphics.Typeface
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState
import com.blacksquircle.ui.feature.editor.ui.fragment.model.ErrorAction
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
internal fun DocumentLayout(
    documentState: DocumentState,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onErrorActionClicked: (ErrorAction) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        val isError = documentState.errorState != null
        if (!isError && !isLoading) {
            CodeEditor(content = documentState.content)
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
private fun CodeEditor(content: Content) {
    AndroidView(
        factory = { context ->
            CodeEditor(context).apply {
                setTextSize(14f)
                isWordwrap = true
                isScalable = true
                isLineNumberEnabled = true
                isHighlightCurrentLine = true
                isHighlightCurrentBlock = true
                isHighlightBracketPair = true
                isEditable = true
                tabWidth = 4
                typefaceText = Typeface.MONOSPACE
                typefaceLineNumber = Typeface.MONOSPACE
                isCursorAnimationEnabled = false
                isStickyTextSelection = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = { editor ->
            editor.setText(content)
        },
        onRelease = CodeEditor::release,
        modifier = Modifier.fillMaxSize()
    )
}