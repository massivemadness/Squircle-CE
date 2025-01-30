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

package com.blacksquircle.ui.ds.radio

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun Radio(
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: () -> Unit = {},
    checked: Boolean = true,
    enabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.selectable(
            selected = checked,
            enabled = enabled,
            onClick = onClick,
        )
    ) {
        RadioButton(
            selected = checked,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = SquircleTheme.colors.colorTextAndIconBrand,
                unselectedColor = SquircleTheme.colors.colorBackgroundTertiary,
                disabledColor = SquircleTheme.colors.colorTextAndIconDisabled,
            )
        )

        if (title != null) {
            Text(
                text = title,
                style = SquircleTheme.typography.text18Regular,
                color = SquircleTheme.colors.colorTextAndIconPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun RadioCheckedPreview() {
    SquircleTheme {
        Radio(
            title = "Radio",
            checked = true,
        )
    }
}

@Preview
@Composable
private fun RadioUncheckedPreview() {
    SquircleTheme {
        Radio(
            title = "Radio",
            checked = false,
        )
    }
}