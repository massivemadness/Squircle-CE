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

package com.blacksquircle.ui.ds.toolbar.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.toolbar.ToolbarSize

@Composable
internal fun ToolbarContent(
    title: String?,
    subtitle: String?,
    alignment: Alignment.Horizontal,
    toolbarSize: ToolbarSize,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = alignment,
        modifier = modifier
            .padding(toolbarSize.contentPadding)
            .semantics(mergeDescendants = true) {
                heading()
            },
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                style = SquircleTheme.typography.text18Medium,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
        if (!subtitle.isNullOrEmpty()) {
            Spacer(Modifier.height(toolbarSize.textSpacer))

            Text(
                text = subtitle,
                style = SquircleTheme.typography.text12Regular,
                color = SquircleTheme.colors.colorTextAndIconSecondary,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}