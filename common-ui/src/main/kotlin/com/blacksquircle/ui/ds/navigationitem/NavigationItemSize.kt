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

package com.blacksquircle.ui.ds.navigationitem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme

@Immutable
data class NavigationItemSize(
    val itemSize: DpSize,
    val indicatorSize: DpSize,
    val labelSpacer: Dp,
    val labelTextStyle: TextStyle,
)

object NavigationItemSizeDefaults {

    val Default: NavigationItemSize
        @Composable
        @ReadOnlyComposable
        get() = NavigationItemSize(
            itemSize = DpSize(64.dp, 64.dp),
            indicatorSize = DpSize(52.dp, 32.dp),
            labelSpacer = 6.dp,
            labelTextStyle = SquircleTheme.typography.text12Regular,
        )
}