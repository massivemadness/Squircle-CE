/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.ds.textfield

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.textfield.internal.DecorationBox
import com.blacksquircle.ui.ds.textfield.internal.HelperText
import com.blacksquircle.ui.ds.textfield.internal.PlaceholderText

@Composable
fun TextField(
    inputText: String,
    onInputChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    topHelperText: String? = null,
    bottomHelperText: String? = null,
    placeholderText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = SquircleTheme.typography.text16Regular,
    textColor: Color = SquircleTheme.colors.colorTextAndIconPrimary,
    cursorColor: Color = SquircleTheme.colors.colorPrimary,
    handleColor: Color = cursorColor,
    selectionColor: Color = handleColor.copy(alpha = 0.4f),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
) {
    Column(modifier) {
        if (topHelperText != null) {
            HelperText(topHelperText)
            Spacer(modifier = Modifier.size(6.dp))
        }
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(handleColor, selectionColor)
        ) {
            val cursorBrush = remember(cursorColor) { SolidColor(cursorColor) }
            val isPlaceholderVisible = inputText.isEmpty() && !placeholderText.isNullOrEmpty()
            BasicTextField(
                value = inputText,
                onValueChange = onInputChanged,
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle.copy(color = textColor),
                cursorBrush = cursorBrush,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                decorationBox = { innerTextField ->
                    DecorationBox(
                        innerTextField = innerTextField,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clipToBounds()
                    ) {
                        if (isPlaceholderVisible) {
                            PlaceholderText(
                                text = placeholderText.orEmpty(),
                                textStyle = textStyle,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                    }
                }
            )
        }
        if (bottomHelperText != null) {
            Spacer(modifier = Modifier.size(6.dp))
            HelperText(bottomHelperText)
        }
    }
}

@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    topHelperText: String? = null,
    bottomHelperText: String? = null,
    placeholderText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = SquircleTheme.typography.text16Regular,
    textColor: Color = SquircleTheme.colors.colorTextAndIconPrimary,
    cursorColor: Color = SquircleTheme.colors.colorPrimary,
    handleColor: Color = cursorColor,
    selectionColor: Color = handleColor.copy(alpha = 0.4f),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(modifier) {
        if (topHelperText != null) {
            HelperText(topHelperText)
            Spacer(modifier = Modifier.size(6.dp))
        }
        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(handleColor, selectionColor)
        ) {
            val cursorBrush = remember(cursorColor) { SolidColor(cursorColor) }
            val isPlaceholderVisible = state.text.isEmpty() && !placeholderText.isNullOrEmpty()
            BasicTextField(
                state = state,
                enabled = enabled,
                readOnly = readOnly,
                inputTransformation = inputTransformation,
                textStyle = textStyle.copy(color = textColor),
                keyboardOptions = keyboardOptions,
                onKeyboardAction = onKeyboardAction,
                lineLimits = lineLimits,
                onTextLayout = onTextLayout,
                interactionSource = interactionSource,
                cursorBrush = cursorBrush,
                outputTransformation = outputTransformation,
                decorator = { innerTextField ->
                    DecorationBox(
                        innerTextField = innerTextField,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clipToBounds()
                    ) {
                        if (isPlaceholderVisible) {
                            PlaceholderText(
                                text = placeholderText.orEmpty(),
                                textStyle = textStyle,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                    }
                },
                scrollState = scrollState,
            )
        }
        if (bottomHelperText != null) {
            Spacer(modifier = Modifier.size(6.dp))
            HelperText(bottomHelperText)
        }
    }
}

@Preview
@Composable
private fun TextFieldPreview() {
    SquircleTheme {
        TextField(
            inputText = "",
            topHelperText = "Top helper text",
            placeholderText = "Placeholder",
            singleLine = true,
            onInputChanged = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}