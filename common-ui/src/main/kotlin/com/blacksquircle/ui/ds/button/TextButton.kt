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

package com.blacksquircle.ui.ds.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.modifier.debounceClickable

@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    debounce: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    textButtonStyle: TextButtonStyle = TextButtonStyleDefaults.Primary,
    textButtonSize: TextButtonSize = TextButtonSizeDefaults.S,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(
                minWidth = textButtonSize.minWidth,
                minHeight = textButtonSize.minHeight,
            )
            .clip(RoundedCornerShape(textButtonSize.cornerRadius))
            .debounceClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                debounce = debounce,
                onClick = onClick,
                role = Role.Button,
            )
            .padding(textButtonSize.padding)
    ) {
        Text(
            text = text.uppercase(),
            color = if (enabled) {
                textButtonStyle.enabledTextColor
            } else {
                textButtonStyle.disabledTextColor
            },
            style = textButtonStyle.textStyle,
        )
    }
}

@PreviewLightDark
@Composable
private fun TextButtonEnabledPreview() {
    PreviewBackground {
        TextButton(
            text = "Text Button",
            onClick = {},
            enabled = true,
        )
    }
}

@PreviewLightDark
@Composable
private fun TextButtonDisabledPreview() {
    PreviewBackground {
        TextButton(
            text = "Text Button",
            onClick = {},
            enabled = false,
        )
    }
}