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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp

@Composable
internal fun TextFieldInput(
    inputText: String,
    onInputChanged: (String) -> Unit,
    placeholderText: String?,
    enabled: Boolean,
    readOnly: Boolean,
    inputMinWidth: Dp,
    inputMinHeight: Dp,
    inputPadding: PaddingValues,
    textStyle: TextStyle,
    textColor: Color,
    placeholderColor: Color,
    cursorColor: Color,
    handleColor: Color,
    selectionColor: Color,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    modifier: Modifier = Modifier,
) {
    val selectionColors = TextSelectionColors(handleColor, selectionColor)
    val cursorBrush = remember(cursorColor) {
        SolidColor(cursorColor)
    }

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            value = inputText,
            onValueChange = onInputChanged,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = cursorBrush,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            maxLines = 1,
            visualTransformation = visualTransformation,
            decorationBox = { innerTextField ->
                DecorationBox(
                    inputText = inputText,
                    inputTextField = innerTextField,
                    placeholder = placeholderText.orEmpty(),
                    placeholderTextStyle = textStyle,
                    placeholderTextColor = placeholderColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .requiredWidthIn(min = inputMinWidth)
                        .requiredHeightIn(min = inputMinHeight)
                        .padding(inputPadding)
                        .clipToBounds()
                )
            },
            modifier = modifier,
        )
    }
}