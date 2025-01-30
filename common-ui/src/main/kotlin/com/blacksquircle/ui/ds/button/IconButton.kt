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

package com.blacksquircle.ui.ds.button

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
@NonRestartableComposable
fun IconButton(
    iconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = SquircleTheme.colors.colorTextAndIconPrimary,
    contentDescription: String? = null,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.defaultMinSize(
            minWidth = 56.dp,
            minHeight = 56.dp,
        ),
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            tint = iconColor,
        )
    }
}