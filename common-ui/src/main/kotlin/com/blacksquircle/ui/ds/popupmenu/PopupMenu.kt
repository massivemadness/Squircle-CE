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

package com.blacksquircle.ui.ds.popupmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
@NonRestartableComposable
fun PopupMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    verticalOffset: Dp = 0.dp,
    horizontalOffset: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenu(
        content = content,
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset(horizontalOffset, verticalOffset),
        modifier = modifier.background(SquircleTheme.colors.colorBackgroundTertiary),
    )
}

@Composable
fun PopupMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    startIconResId: Int? = null,
    endIconResId: Int? = null,
) {
    DropdownMenuItem(
        content = {
            if (startIconResId != null) {
                Icon(
                    painter = painterResource(startIconResId),
                    contentDescription = null,
                    tint = SquircleTheme.colors.colorTextAndIconSecondary,
                )
                Spacer(Modifier.width(16.dp))
            }
            Text(
                text = title,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
                style = SquircleTheme.typography.text16Regular,
            )
            if (endIconResId != null) {
                Spacer(Modifier.width(16.dp))
                Icon(
                    painter = painterResource(endIconResId),
                    contentDescription = null,
                    tint = SquircleTheme.colors.colorTextAndIconSecondary,
                )
            }
        },
        onClick = onClick,
        modifier = modifier.defaultMinSize(
            minWidth = 172.dp,
            minHeight = Dp.Unspecified,
        ),
    )
}

@PreviewLightDark
@Composable
private fun PopupMenuPreview() {
    PreviewBackground {
        PopupMenu(
            expanded = true,
            onDismiss = {},
        ) {
            PopupMenuItem(
                title = "Menu Item",
                onClick = {},
            )
            PopupMenuItem(
                title = "Menu Item",
                onClick = {},
            )
            PopupMenuItem(
                title = "Menu Item",
                onClick = {},
            )
        }
    }
}