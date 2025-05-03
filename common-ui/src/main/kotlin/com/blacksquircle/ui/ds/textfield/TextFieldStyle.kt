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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.blacksquircle.ui.ds.SquircleTheme

@Immutable
data class TextFieldStyle(
    val backgroundColor: Color,
    val textStyle: TextStyle,
    val textColor: Color,
    val placeholderColor: Color,
    val cursorColor: Color,
    val handleColor: Color,
    val selectionColor: Color,
    val labelTextStyle: TextStyle,
    val labelTextColor: Color,
    val helpTextStyle: TextStyle,
    val helpTextColor: Color,
    val errorTextStyle: TextStyle,
    val errorTextColor: Color,
    val errorBorderColor: Color,
)

object TextFieldStyleDefaults {

    val Default: TextFieldStyle
        @Composable
        @ReadOnlyComposable
        get() = TextFieldStyle(
            backgroundColor = SquircleTheme.colors.colorBackgroundTertiary,
            textStyle = SquircleTheme.typography.text16Regular,
            textColor = SquircleTheme.colors.colorTextAndIconPrimary,
            placeholderColor = SquircleTheme.colors.colorTextAndIconSecondary,
            cursorColor = SquircleTheme.colors.colorPrimary,
            handleColor = SquircleTheme.colors.colorPrimary,
            selectionColor = SquircleTheme.colors.colorPrimary.copy(alpha = 0.4f),
            labelTextStyle = SquircleTheme.typography.text12Regular,
            labelTextColor = SquircleTheme.colors.colorTextAndIconSecondary,
            helpTextStyle = SquircleTheme.typography.text12Regular,
            helpTextColor = SquircleTheme.colors.colorTextAndIconSecondary,
            errorTextStyle = SquircleTheme.typography.text12Regular,
            errorTextColor = SquircleTheme.colors.colorTextAndIconError,
            errorBorderColor = SquircleTheme.colors.colorTextAndIconError,
        )
}