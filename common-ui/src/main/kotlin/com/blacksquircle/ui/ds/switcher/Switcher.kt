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

package com.blacksquircle.ui.ds.switcher

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.extensions.clearSemantics
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.ds.modifier.debounceToggleable

private const val CheckedTrackAlpha = 0.54f
private const val UncheckedTrackAlpha = 0.38f

@Composable
fun Switcher(
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: () -> Unit = {},
    checked: Boolean = true,
    enabled: Boolean = true,
    switcherStyle: SwitcherStyle = SwitcherStyleDefaults.Primary,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() }
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .mergeSemantics()
            .debounceToggleable(
                interactionSource = interactionSource,
                indication = null,
                value = checked,
                enabled = enabled,
                onClick = onClick,
            )
    ) {
        Box(Modifier.requiredSize(42.dp)) {
            Switch(
                checked = checked,
                onCheckedChange = { onClick() },
                enabled = enabled,
                interactionSource = interactionSource,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = switcherStyle.checkedColor,
                    checkedTrackColor = switcherStyle.checkedColor,
                    checkedTrackAlpha = CheckedTrackAlpha,
                    uncheckedThumbColor = switcherStyle.uncheckedColor,
                    uncheckedTrackColor = switcherStyle.uncheckedColor,
                    uncheckedTrackAlpha = UncheckedTrackAlpha,
                    disabledCheckedThumbColor = switcherStyle.disabledColor,
                    disabledCheckedTrackColor = switcherStyle.disabledColor,
                    disabledUncheckedThumbColor = switcherStyle.disabledColor,
                    disabledUncheckedTrackColor = switcherStyle.disabledColor,
                ),
                modifier = Modifier.clearSemantics()
            )
        }
        if (title != null) {
            Text(
                text = title,
                style = switcherStyle.textStyle,
                color = if (enabled) {
                    switcherStyle.enabledTextColor
                } else {
                    switcherStyle.disabledTextColor
                },
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SwitcherCheckedPreview() {
    PreviewBackground {
        Switcher(
            title = "Switcher",
            checked = true,
        )
    }
}

@PreviewLightDark
@Composable
private fun SwitcherUncheckedPreview() {
    PreviewBackground {
        Switcher(
            title = "Switcher",
            checked = false,
        )
    }
}

@PreviewLightDark
@Composable
private fun SwitcherCheckedDisabledPreview() {
    PreviewBackground {
        Switcher(
            title = "Switcher",
            checked = true,
            enabled = false,
        )
    }
}

@PreviewLightDark
@Composable
private fun SwitcherUncheckedDisabledPreview() {
    PreviewBackground {
        Switcher(
            title = "Switcher",
            checked = false,
            enabled = false,
        )
    }
}