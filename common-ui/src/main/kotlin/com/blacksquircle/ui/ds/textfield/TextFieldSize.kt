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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class TextFieldSize(
    val inputCornerRadius: Dp,
    val inputMinWidth: Dp,
    val inputMinHeight: Dp,
    val inputPadding: PaddingValues,
    val errorBorderSize: Dp,
    val labelPadding: PaddingValues,
    val helpPadding: PaddingValues,
    val errorPadding: PaddingValues,
)

object TextFieldSizeDefaults {

    val M: TextFieldSize
        get() = TextFieldSize(
            inputCornerRadius = 6.dp,
            inputMinWidth = Dp.Unspecified,
            inputMinHeight = 42.dp,
            inputPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            errorBorderSize = 2.dp,
            labelPadding = PaddingValues(bottom = 6.dp),
            helpPadding = PaddingValues(top = 6.dp),
            errorPadding = PaddingValues(top = 6.dp),
        )
}