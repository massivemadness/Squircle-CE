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

package com.blacksquircle.ui.ds.textfield

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults
import com.blacksquircle.ui.ds.button.IconButtonStyleDefaults
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.ds.layout.ThreeSlotLayout
import com.blacksquircle.ui.ds.textfield.internal.TextFieldError
import com.blacksquircle.ui.ds.textfield.internal.TextFieldHelp
import com.blacksquircle.ui.ds.textfield.internal.TextFieldInput
import com.blacksquircle.ui.ds.textfield.internal.TextFieldInput2
import com.blacksquircle.ui.ds.textfield.internal.TextFieldLabel

@Composable
fun TextField(
    inputText: String,
    modifier: Modifier = Modifier,
    onInputChanged: (String) -> Unit = {},
    labelText: String? = null,
    helpText: String? = null,
    errorText: String? = null,
    placeholderText: String? = null,
    startContent: @Composable (BoxScope.() -> Unit)? = null,
    endContent: @Composable (BoxScope.() -> Unit)? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textFieldStyle: TextFieldStyle = TextFieldStyleDefaults.Default,
    textFieldSize: TextFieldSize = TextFieldSizeDefaults.M,
) {
    val inputBorderSize = if (error) textFieldSize.errorBorderSize else 0.dp
    val inputBorderColor = if (error) textFieldStyle.errorBorderColor else Color.Transparent
    val inputBackgroundColor = textFieldStyle.backgroundColor
    val inputCornerShape = RoundedCornerShape(textFieldSize.inputCornerRadius)

    val hasLabel = !labelText.isNullOrEmpty()
    val hasHelp = !helpText.isNullOrEmpty()
    val hasError = !errorText.isNullOrEmpty()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.mergeSemantics(),
    ) {
        if (hasLabel) {
            TextFieldLabel(
                text = labelText,
                textStyle = textFieldStyle.labelTextStyle,
                textColor = textFieldStyle.labelTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(textFieldSize.labelPadding)
            )
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
                    inputMinWidth = textFieldSize.inputMinWidth,
                    inputMinHeight = textFieldSize.inputMinHeight,
                    inputPadding = textFieldSize.inputPadding,
                    textStyle = textFieldStyle.textStyle,
                    textColor = textFieldStyle.textColor,
                    placeholderColor = textFieldStyle.placeholderColor,
                    cursorColor = textFieldStyle.cursorColor,
                    handleColor = textFieldStyle.handleColor,
                    selectionColor = textFieldStyle.selectionColor,
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

        if (hasError && error) {
            TextFieldError(
                text = errorText,
                textStyle = textFieldStyle.errorTextStyle,
                textColor = textFieldStyle.errorTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(textFieldSize.errorPadding)
            )
        }
        if (hasHelp && !(hasError && error)) {
            TextFieldHelp(
                text = helpText,
                textStyle = textFieldStyle.helpTextStyle,
                textColor = textFieldStyle.helpTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(textFieldSize.helpPadding)
            )
        }
    }
}

@Composable
fun TextField2(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    helpText: String? = null,
    errorText: String? = null,
    placeholderText: String? = null,
    startContent: @Composable (BoxScope.() -> Unit)? = null,
    endContent: @Composable (BoxScope.() -> Unit)? = null,
    error: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    scrollState: ScrollState = rememberScrollState(),
    textFieldStyle: TextFieldStyle = TextFieldStyleDefaults.Default,
    textFieldSize: TextFieldSize = TextFieldSizeDefaults.M,
) {
    val inputBorderSize = if (error) textFieldSize.errorBorderSize else 0.dp
    val inputBorderColor = if (error) textFieldStyle.errorBorderColor else Color.Transparent
    val inputBackgroundColor = textFieldStyle.backgroundColor
    val inputCornerShape = RoundedCornerShape(textFieldSize.inputCornerRadius)

    val hasLabel = !labelText.isNullOrEmpty()
    val hasHelp = !helpText.isNullOrEmpty()
    val hasError = !errorText.isNullOrEmpty()

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        if (hasLabel) {
            TextFieldLabel(
                text = labelText,
                textStyle = textFieldStyle.labelTextStyle,
                textColor = textFieldStyle.labelTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(textFieldSize.labelPadding)
            )
        }

        ThreeSlotLayout(
            startContent = startContent,
            middleContent = {
                TextFieldInput2(
                    state = state,
                    placeholderText = placeholderText,
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = textFieldStyle.textStyle,
                    textColor = textFieldStyle.textColor,
                    cursorColor = textFieldStyle.cursorColor,
                    handleColor = textFieldStyle.handleColor,
                    selectionColor = textFieldStyle.selectionColor,
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

        if (hasError && error) {
            TextFieldError(
                text = errorText,
                textStyle = textFieldStyle.errorTextStyle,
                textColor = textFieldStyle.errorTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(textFieldSize.errorPadding)
            )
        }
        if (hasHelp && !(hasError && error)) {
            TextFieldHelp(
                text = helpText,
                textStyle = textFieldStyle.helpTextStyle,
                textColor = textFieldStyle.helpTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(textFieldSize.helpPadding)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TextFieldPreview() {
    PreviewBackground {
        TextField(
            inputText = "",
            onInputChanged = {},
            labelText = "Label text",
            helpText = "Help text",
            placeholderText = "Placeholder",
            endContent = {
                IconButton(
                    iconResId = R.drawable.ic_close,
                    iconButtonStyle = IconButtonStyleDefaults.Secondary,
                    iconButtonSize = IconButtonSizeDefaults.S,
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}