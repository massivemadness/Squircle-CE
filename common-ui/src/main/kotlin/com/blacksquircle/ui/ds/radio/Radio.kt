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

package com.blacksquircle.ui.ds.radio

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.extensions.clearSemantics
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.ds.modifier.debounceSelectable

@Composable
fun Radio(
    modifier: Modifier = Modifier,
    title: String? = null,
    onClick: () -> Unit = {},
    checked: Boolean = true,
    enabled: Boolean = true,
    radioStyle: RadioStyle = RadioStyleDefaults.Primary,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() },
    indication: Indication? = ripple(bounded = false, radius = 24.dp)
) {
    val strokeWidth = 2.dp
    val radioRadius = 12.dp
    val dotRadius = animateDpAsState(
        targetValue = if (checked) radioRadius / 2 else 0.dp,
        animationSpec = tween(durationMillis = 100)
    )
    val radioColor = when {
        !enabled -> radioStyle.disabledColor
        !checked -> radioStyle.uncheckedColor
        else -> radioStyle.checkedColor
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .mergeSemantics()
            .debounceSelectable(
                interactionSource = interactionSource,
                indication = null,
                selected = checked,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            )
    ) {
        Canvas(
            modifier = Modifier
                .clearSemantics()
                .debounceSelectable(
                    selected = checked,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.RadioButton,
                    interactionSource = interactionSource,
                    indication = indication,
                )
                .wrapContentSize(Alignment.Center)
                .requiredSize(32.dp)
        ) {
            drawCircle(
                color = radioColor,
                radius = radioRadius.toPx() - strokeWidth.toPx(),
                style = Stroke(strokeWidth.toPx())
            )
            if (dotRadius.value > 0.dp) {
                drawCircle(
                    color = radioColor,
                    radius = dotRadius.value.toPx() - strokeWidth.toPx() / 2,
                    style = Fill
                )
            }
        }
        if (title != null) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = radioStyle.textStyle,
                color = if (enabled) {
                    radioStyle.enabledTextColor
                } else {
                    radioStyle.disabledTextColor
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
private fun RadioCheckedPreview() {
    PreviewBackground {
        Radio(
            title = "Radio",
            checked = true,
        )
    }
}

@PreviewLightDark
@Composable
private fun RadioUncheckedPreview() {
    PreviewBackground {
        Radio(
            title = "Radio",
            checked = false,
        )
    }
}

@PreviewLightDark
@Composable
private fun RadioCheckedDisabledPreview() {
    PreviewBackground {
        Radio(
            title = "Radio",
            checked = true,
            enabled = false,
        )
    }
}

@PreviewLightDark
@Composable
private fun RadioUncheckedDisabledPreview() {
    PreviewBackground {
        Radio(
            title = "Radio",
            checked = false,
            enabled = false,
        )
    }
}