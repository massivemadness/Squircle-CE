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

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.modifier.debounceClickable

@Composable
fun OutlinedButton(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit = {},
    startIconResId: Int? = null,
    endIconResId: Int? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    outlinedButtonStyle: OutlinedButtonStyle = OutlinedButtonStyleDefaults.Primary,
    outlinedButtonSize: OutlinedButtonSize = OutlinedButtonSizeDefaults.S,
) {
    val buttonShape = RoundedCornerShape(outlinedButtonSize.cornerRadius)
    val buttonColor = if (enabled) {
        outlinedButtonStyle.enabledTextColor
    } else {
        outlinedButtonStyle.disabledTextColor
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .defaultMinSize(
                minWidth = outlinedButtonSize.minWidth,
                minHeight = outlinedButtonSize.minHeight,
            )
            .clip(buttonShape)
            .debounceClickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
            )
            .border(
                width = outlinedButtonSize.borderSize,
                color = outlinedButtonStyle.borderColor,
                shape = buttonShape,
            )
            .padding(outlinedButtonSize.innerPadding)
    ) {
        if (startIconResId != null) {
            Icon(
                painter = painterResource(startIconResId),
                contentDescription = null,
                tint = buttonColor,
                modifier = Modifier.size(outlinedButtonSize.iconSize),
            )
        }
        Text(
            text = text.uppercase(),
            color = buttonColor,
            style = outlinedButtonStyle.textStyle,
            modifier = Modifier.padding(outlinedButtonSize.textPadding)
        )
        if (endIconResId != null) {
            Icon(
                painter = painterResource(endIconResId),
                contentDescription = null,
                tint = buttonColor,
                modifier = Modifier.size(outlinedButtonSize.iconSize),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OutlinedButtonEnabledPreview() {
    PreviewBackground {
        OutlinedButton(
            text = "Outlined Button",
            startIconResId = R.drawable.ic_plus,
            onClick = {},
            enabled = true,
        )
    }
}

@PreviewLightDark
@Composable
private fun OutlinedButtonDisabledPreview() {
    PreviewBackground {
        OutlinedButton(
            text = "Outlined Button",
            startIconResId = R.drawable.ic_plus,
            onClick = {},
            enabled = false,
        )
    }
}