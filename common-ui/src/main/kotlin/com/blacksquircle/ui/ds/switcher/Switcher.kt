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

package com.blacksquircle.ui.ds.switcher

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun Switcher(
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: () -> Unit = {},
    checked: Boolean = true,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() }
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.toggleable(
            interactionSource = interactionSource,
            indication = null,
            value = checked,
            enabled = enabled,
            onValueChange = { onClick() },
        )
    ) {
        Box(Modifier.requiredSize(42.dp)) {
            Switch(
                checked = checked,
                onCheckedChange = { onClick() },
                enabled = enabled,
                interactionSource = interactionSource,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SquircleTheme.colors.colorPrimary,
                    checkedTrackColor = SquircleTheme.colors.colorPrimary,
                    checkedTrackAlpha = 0.54f,
                    uncheckedThumbColor = SquircleTheme.colors.colorTextAndIconSecondary,
                    uncheckedTrackColor = SquircleTheme.colors.colorTextAndIconSecondary,
                    uncheckedTrackAlpha = 0.38f,
                    disabledCheckedThumbColor = SquircleTheme.colors.colorTextAndIconDisabled,
                    disabledCheckedTrackColor = SquircleTheme.colors.colorTextAndIconDisabled,
                    disabledUncheckedThumbColor = SquircleTheme.colors.colorTextAndIconDisabled,
                    disabledUncheckedTrackColor = SquircleTheme.colors.colorTextAndIconDisabled,
                )
            )
        }
        if (title != null) {
            Text(
                text = title,
                style = SquircleTheme.typography.text16Regular,
                color = if (enabled) {
                    SquircleTheme.colors.colorTextAndIconPrimary
                } else {
                    SquircleTheme.colors.colorTextAndIconDisabled
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun SwitcherCheckedPreview() {
    SquircleTheme {
        Switcher(
            title = "Switcher",
            checked = true,
        )
    }
}

@Preview
@Composable
private fun SwitcherUncheckedPreview() {
    SquircleTheme {
        Switcher(
            title = "Switcher",
            checked = false,
        )
    }
}

@Preview
@Composable
private fun SwitcherCheckedDisabledPreview() {
    SquircleTheme {
        Switcher(
            title = "Switcher",
            checked = true,
            enabled = false,
        )
    }
}

@Preview
@Composable
private fun SwitcherUncheckedDisabledPreview() {
    SquircleTheme {
        Switcher(
            title = "Switcher",
            checked = false,
            enabled = false,
        )
    }
}