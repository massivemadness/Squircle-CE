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

package com.blacksquircle.ui.ds.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class OutlinedButtonSize(
    val minWidth: Dp,
    val minHeight: Dp,
    val cornerRadius: Dp,
    val borderSize: Dp,
    val innerPadding: PaddingValues,
    val textPadding: PaddingValues,
    val iconSize: Dp,
)

object OutlinedButtonSizeDefaults {

    val S: OutlinedButtonSize
        get() = OutlinedButtonSize(
            minWidth = 64.dp,
            minHeight = 36.dp,
            cornerRadius = 4.dp,
            borderSize = 1.dp,
            innerPadding = PaddingValues(8.dp),
            textPadding = PaddingValues(horizontal = 8.dp),
            iconSize = 20.dp
        )
}