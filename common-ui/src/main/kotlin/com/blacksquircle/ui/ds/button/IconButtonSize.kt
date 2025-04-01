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

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class IconButtonSize(
    val iconSize: Dp,
    val rippleSize: Dp,
)

object IconButtonSizeDefaults {

    val XXS: IconButtonSize
        get() = IconButtonSize(
            iconSize = 18.dp,
            rippleSize = 16.dp,
        )

    val XS: IconButtonSize
        get() = IconButtonSize(
            iconSize = 36.dp,
            rippleSize = 18.dp,
        )

    val S: IconButtonSize
        get() = IconButtonSize(
            iconSize = 42.dp,
            rippleSize = 18.dp,
        )

    val M: IconButtonSize
        get() = IconButtonSize(
            iconSize = 48.dp,
            rippleSize = 22.dp,
        )

    val L: IconButtonSize
        get() = IconButtonSize(
            iconSize = 56.dp,
            rippleSize = 24.dp,
        )
}