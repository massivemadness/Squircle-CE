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

package com.blacksquircle.ui.ds.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.IconButton
import com.blacksquircle.ui.ds.button.IconButtonSizeDefaults

@Composable
fun Tab(
    title: String,
    iconResId: Int,
    selected: Boolean,
    onClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(36.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = true,
                role = Role.Tab,
                interactionSource = null,
                indication = ripple()
            )
            .padding(start = 12.dp)
    ) {
        Text(
            text = title,
            color = SquircleTheme.colors.colorTextAndIconPrimary,
            style = SquircleTheme.typography.text14Bold,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )

        IconButton(
            iconResId = iconResId,
            iconColor = SquircleTheme.colors.colorTextAndIconSecondary,
            onClick = onActionClick,
            iconButtonSize = IconButtonSizeDefaults.XXS,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@PreviewLightDark
@Composable
private fun TabPreview() {
    PreviewBackground {
        Row {
            Tab(
                title = "untitled.txt",
                iconResId = R.drawable.ic_close,
                selected = true,
                onClick = {},
                onActionClick = {},
            )
            Tab(
                title = "Document.txt",
                iconResId = R.drawable.ic_close,
                selected = false,
                onClick = {},
                onActionClick = {},
            )
        }
    }
}