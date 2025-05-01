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

package com.blacksquircle.ui.ds.emptyview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.button.OutlinedButton
import com.blacksquircle.ui.ds.extensions.mergeSemantics

@Composable
fun EmptyView(
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    title: String? = null,
    subtitle: String? = null,
    action: String? = null,
    onClick: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .mergeSemantics()
            .widthIn(max = 320.dp)
            .padding(24.dp),
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = SquircleTheme.colors.colorTextAndIconSecondary,
                modifier = Modifier.size(84.dp)
            )
        }

        if (title != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
                style = SquircleTheme.typography.header20Bold,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )
        }

        if (subtitle != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                style = SquircleTheme.typography.text16Regular,
                textAlign = TextAlign.Center,
            )
        }

        if (action != null) {
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                text = action,
                onClick = onClick,
                modifier = Modifier.widthIn(min = 100.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyViewPreview() {
    PreviewBackground {
        EmptyView(
            iconResId = R.drawable.ic_file_find,
            title = "An error occurred",
            subtitle = "Please try again later",
            action = "Try again",
        )
    }
}