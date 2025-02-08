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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun OutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    startIconResId: Int? = null,
    endIconResId: Int? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val buttonMinWidth = 64.dp
    val buttonMinHeight = 36.dp
    val buttonShape = RoundedCornerShape(4.dp)
    val buttonColor = if (enabled) {
        SquircleTheme.colors.colorPrimary
    } else {
        SquircleTheme.colors.colorTextAndIconDisabled
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .sizeIn(
                minWidth = buttonMinWidth,
                minHeight = buttonMinHeight,
            )
            .clip(buttonShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
                role = Role.Button,
            )
            .border(
                width = 1.dp,
                color = SquircleTheme.colors.colorOutline,
                shape = buttonShape,
            )
            .padding(8.dp)
    ) {
        if (startIconResId != null) {
            Icon(
                painter = painterResource(startIconResId),
                contentDescription = null,
                tint = buttonColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = text.uppercase(),
            color = buttonColor,
            style = SquircleTheme.typography.text14Medium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        if (endIconResId != null) {
            Icon(
                painter = painterResource(endIconResId),
                contentDescription = null,
                tint = buttonColor,
                modifier = Modifier.size(20.dp),
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
            startIconResId = R.drawable.ic_info,
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
            startIconResId = R.drawable.ic_info,
            onClick = {},
            enabled = false,
        )
    }
}