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
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun DsTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
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
    val cursorBrush = remember(cursorColor) { SolidColor(cursorColor) }
    val textSelectionColors = TextSelectionColors(handleColor, selectionColor)

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle.copy(color = textColor),
            cursorBrush = cursorBrush,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            decorationBox = { innerTextField ->
                DecorationBox(innerTextField)
            }
        )
    }
}

@Composable
fun DsTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
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
    val cursorBrush = remember(cursorColor) { SolidColor(cursorColor) }
    val textSelectionColors = TextSelectionColors(handleColor, selectionColor)

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        BasicTextField(
            state = state,
            modifier = modifier,
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
                DecorationBox(innerTextField)
            },
            scrollState = scrollState,
        )
    }
}

@Composable
@NonRestartableComposable
private fun DecorationBox(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SquircleTheme.colors.colorBackgroundTertiary,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(12.dp)
    ) {
        content()
    }
}