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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
internal fun TextFieldInput2(
    state: TextFieldState,
    placeholderText: String?,
    enabled: Boolean,
    readOnly: Boolean,
    textStyle: TextStyle,
    textColor: Color,
    cursorColor: Color,
    handleColor: Color,
    selectionColor: Color,
    keyboardOptions: KeyboardOptions,
    onKeyboardAction: KeyboardActionHandler?,
    lineLimits: TextFieldLineLimits,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)?,
    interactionSource: MutableInteractionSource?,
    inputTransformation: InputTransformation?,
    outputTransformation: OutputTransformation?,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val inputMinWidth = Dp.Unspecified
    val inputMinHeight = 42.dp
    val inputPadding = PaddingValues(
        horizontal = 12.dp,
        vertical = 6.dp,
    )

    val selectionColors = TextSelectionColors(handleColor, selectionColor)
    val cursorBrush = remember(cursorColor) {
        SolidColor(cursorColor)
    }

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            state = state,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = textColor),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = lineLimits,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            inputTransformation = inputTransformation,
            outputTransformation = outputTransformation,
            scrollState = scrollState,
            decorator = { innerTextField ->
                DecorationBox(
                    inputText = state.text,
                    inputTextField = innerTextField,
                    placeholder = placeholderText.orEmpty(),
                    placeholderTextStyle = textStyle,
                    placeholderTextColor = SquircleTheme.colors.colorTextAndIconSecondary,
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