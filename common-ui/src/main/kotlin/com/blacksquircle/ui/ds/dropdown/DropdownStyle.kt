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

package com.blacksquircle.ui.ds.dropdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.blacksquircle.ui.ds.SquircleTheme

@Immutable
data class DropdownStyle(
    val iconColor: Color,
    val textColor: Color,
    val textStyle: TextStyle,
)

object DropdownStyleDefaults {

    val Default: DropdownStyle
        @Composable
        @ReadOnlyComposable
        get() = DropdownStyle(
            iconColor = SquircleTheme.colors.colorTextAndIconPrimary,
            textColor = SquircleTheme.colors.colorTextAndIconPrimary,
            textStyle = SquircleTheme.typography.text16Regular,
        )
}