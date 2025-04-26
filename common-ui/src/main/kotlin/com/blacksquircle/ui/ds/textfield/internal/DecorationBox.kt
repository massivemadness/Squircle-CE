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

package com.blacksquircle.ui.ds.textfield.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.blacksquircle.ui.ds.extensions.clearSemantics

@Composable
@NonRestartableComposable
internal fun DecorationBox(
    inputText: CharSequence,
    inputTextField: @Composable () -> Unit,
    placeholder: String,
    placeholderTextStyle: TextStyle,
    placeholderTextColor: Color,
    modifier: Modifier = Modifier,
) {
    val isPlaceholderVisible = inputText.isEmpty() && placeholder.isNotEmpty()

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
    ) {
        if (isPlaceholderVisible) {
            TextFieldPlaceholder(
                text = placeholder,
                textStyle = placeholderTextStyle,
                textColor = placeholderTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clearSemantics(),
            )
        }
        inputTextField()
    }
}