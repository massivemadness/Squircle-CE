/*
 * Copyright Squircle CE contributors.
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.modifier.debounceSelectable

@Composable
fun TabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(horizontal = 12.dp),
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    anchor: @Composable (() -> Unit)? = null,
) {
    Box(modifier.width(IntrinsicSize.Max)) {
        if (anchor != null) {
            anchor()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(36.dp)
                .debounceSelectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = true,
                    role = Role.Tab,
                    interactionSource = null,
                    indication = ripple()
                )
                .padding(paddingValues)
        ) {
            if (leadingContent != null) {
                leadingContent()
            }
            Text(
                text = title,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
                style = SquircleTheme.typography.text14Bold,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            if (trailingContent != null) {
                trailingContent()
            }
        }

        TabIndicator(
            active = selected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@PreviewLightDark
@Composable
private fun TabItemPreview() {
    PreviewBackground {
        var selected by remember { mutableIntStateOf(0) }
        Row {
            TabItem(
                title = "untitled.txt",
                selected = selected == 0,
                onClick = { selected = 0 },
            )
            TabItem(
                title = "Document.txt",
                selected = selected == 1,
                onClick = { selected = 1 },
            )
        }
    }
}