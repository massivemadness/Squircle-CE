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

package com.blacksquircle.ui.ds.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.sizeM

@Composable
fun Preference(
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    leadingContent: @Composable (RowScope.() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(sizeM)
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(modifier = Modifier.size(sizeM))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) {
                    MaterialTheme.colors.onSurface
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.38f)
                },
                style = MaterialTheme.typography.body1,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = if (enabled) {
                        MaterialTheme.colors.onSurface
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.38f)
                    },
                    style = MaterialTheme.typography.body2,
                )
            }
            if (bottomContent != null) {
                bottomContent()
            }
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.size(sizeM))
            trailingContent()
        }
    }
}

@Preview
@Composable
private fun PreferenceEnabledPreview() {
    SquircleTheme {
        Preference(
            title = "Squircle CE",
            subtitle = "About application",
            enabled = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreferenceDisabledPreview() {
    SquircleTheme {
        Preference(
            title = "Squircle CE",
            subtitle = "About application",
            enabled = false,
            onClick = {},
        )
    }
}