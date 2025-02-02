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
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSize
import com.blacksquircle.ui.ds.layout.ThreeSlotLayout
import com.blacksquircle.ui.ds.textfield.internal.TextFieldHelp
import com.blacksquircle.ui.ds.textfield.internal.TextFieldInput
import com.blacksquircle.ui.ds.textfield.internal.TextFieldInputAdvanced
import com.blacksquircle.ui.ds.textfield.internal.TextFieldLabel

@Composable
fun TextField(
    inputText: String,
    onInputChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    helpText: String? = null,
    placeholderText: String? = null,
    startContent: @Composable (BoxScope.() -> Unit)? = null,
    endContent: @Composable (BoxScope.() -> Unit)? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = SquircleTheme.typography.text16Regular,
    textColor: Color = SquircleTheme.colors.colorTextAndIconPrimary,
    cursorColor: Color = SquircleTheme.colors.colorPrimary,
    handleColor: Color = cursorColor,
    selectionColor: Color = handleColor.copy(alpha = 0.4f),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val inputCornerRadius = 6.dp
    val inputBorderSize = if (error) 2.dp else 0.dp
    val inputBorderColor = if (error) SquircleTheme.colors.colorError else Color.Transparent
    val inputBackgroundColor = SquircleTheme.colors.colorBackgroundTertiary
    val inputCornerShape = remember(inputCornerRadius) {
        RoundedCornerShape(inputCornerRadius)
    }

    val hasLabel = !labelText.isNullOrEmpty()
    val hasHelp = !helpText.isNullOrEmpty()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        if (hasLabel) {
            TextFieldLabel(
                text = labelText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        ThreeSlotLayout(
            startContent = startContent,
            middleContent = {
                TextFieldInput(
                    inputText = inputText,
                    onInputChanged = onInputChanged,
                    placeholderText = placeholderText,
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = textStyle,
                    textColor = textColor,
                    cursorColor = cursorColor,
                    handleColor = handleColor,
                    selectionColor = selectionColor,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    visualTransformation = visualTransformation,
                )
            },
            endContent = endContent,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = inputBackgroundColor, shape = inputCornerShape)
                .border(width = inputBorderSize, color = inputBorderColor, shape = inputCornerShape)
                .clip(inputCornerShape),
        )

        if (hasHelp) {
            Spacer(modifier = Modifier.height(6.dp))
            TextFieldHelp(
                text = helpText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun TextFieldAdvanced(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    helpText: String? = null,
    placeholderText: String? = null,
    startContent: @Composable (BoxScope.() -> Unit)? = null,
    endContent: @Composable (BoxScope.() -> Unit)? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
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
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    val inputCornerRadius = 6.dp
    val inputBorderSize = if (error) 2.dp else 0.dp
    val inputBorderColor = if (error) SquircleTheme.colors.colorError else Color.Transparent
    val inputBackgroundColor = SquircleTheme.colors.colorBackgroundTertiary
    val inputCornerShape = remember(inputCornerRadius) {
        RoundedCornerShape(inputCornerRadius)
    }

    val hasLabel = !labelText.isNullOrEmpty()
    val hasHelp = !helpText.isNullOrEmpty()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        if (hasLabel) {
            TextFieldLabel(
                text = labelText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        ThreeSlotLayout(
            startContent = startContent,
            middleContent = {
                TextFieldInputAdvanced(
                    state = state,
                    placeholderText = placeholderText,
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = textStyle,
                    textColor = textColor,
                    cursorColor = cursorColor,
                    handleColor = handleColor,
                    selectionColor = selectionColor,
                    keyboardOptions = keyboardOptions,
                    onKeyboardAction = onKeyboardAction,
                    lineLimits = lineLimits,
                    onTextLayout = onTextLayout,
                    interactionSource = interactionSource,
                    inputTransformation = inputTransformation,
                    outputTransformation = outputTransformation,
                    scrollState = scrollState,
                )
            },
            endContent = endContent,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = inputBackgroundColor, shape = inputCornerShape)
                .border(width = inputBorderSize, color = inputBorderColor, shape = inputCornerShape)
                .clip(inputCornerShape),
        )

        if (hasHelp) {
            Spacer(modifier = Modifier.height(6.dp))
            TextFieldHelp(
                text = helpText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )
        }
    }
}

@Preview
@Composable
private fun TextFieldPreview() {
    SquircleTheme {
        TextField(
            inputText = "",
            onInputChanged = {},
            labelText = "Label text",
            helpText = "Help text",
            placeholderText = "Placeholder",
            endContent = {
                IconButton(
                    iconResId = R.drawable.ic_close,
                    iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
                    iconSize = IconButtonSize.S,
                    onClick = {},
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}