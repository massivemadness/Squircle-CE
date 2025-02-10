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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class DropdownSize(
    val height: Dp,
    val cornerRadius: Dp,
    val padding: PaddingValues,
    val textSpacer: Dp,
)

object DropdownSizeDefaults {

    val M: DropdownSize
        get() = DropdownSize(
            height = 42.dp,
            cornerRadius = 4.dp,
            padding = PaddingValues(horizontal = 8.dp),
            textSpacer = 16.dp,
        )
}