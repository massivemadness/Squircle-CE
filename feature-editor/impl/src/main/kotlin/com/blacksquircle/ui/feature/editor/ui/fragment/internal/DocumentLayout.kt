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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.feature.editor.ui.fragment.model.DocumentState

@Composable
internal fun DocumentLayout(
    contentPadding: PaddingValues,
    documentState: DocumentState,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(contentPadding)
            .fillMaxSize()
    ) {
        // TODO CodeEditor
        Text(
            text = documentState.content?.text.orEmpty(),
            color = SquircleTheme.colors.colorTextAndIconPrimary,
            style = SquircleTheme.typography.text14Regular,
        )
        if (isLoading) {
            CircularProgress(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        /*if (isEmpty && !isLoading && !isError) {
            EmptyView(
                iconResId = R.drawable.ic_file_find,
                title = stringResource(R.string.common_no_result),
                modifier = Modifier.align(Alignment.Center)
            )
        }*/
    }
}