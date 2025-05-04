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

package com.blacksquircle.ui.ds.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.modifier.debounceClickable

@Composable
fun ActionLayout(
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    title: String? = null,
    subtitle: String? = null,
    onClick: () -> Unit = {},
    iconColor: Color = SquircleTheme.colors.colorTextAndIconSecondary,
    titleColor: Color = SquircleTheme.colors.colorTextAndIconPrimary,
    subtitleColor: Color = SquircleTheme.colors.colorTextAndIconSecondary,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .debounceClickable(onClick = onClick)
            .padding(
                horizontal = 18.dp,
                vertical = 12.dp,
            )
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconColor,
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Column {
            if (title != null) {
                Text(
                    text = title,
                    style = SquircleTheme.typography.text16Regular,
                    color = titleColor,
                )
            }
            if (title != null && subtitle != null) {
                Spacer(Modifier.height(2.dp))
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = SquircleTheme.typography.text14Regular,
                    color = subtitleColor,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ActionLayoutPreview() {
    PreviewBackground {
        ActionLayout(
            iconRes = R.drawable.ic_autorenew,
            title = "Fetch",
            subtitle = "Fetch content from remote repository",
        )
    }
}