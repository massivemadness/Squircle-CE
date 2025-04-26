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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.modifier.debounceClickable

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    onClick: () -> Unit = {},
    contentDescription: String? = null,
    enabled: Boolean = true,
    debounce: Boolean = true,
    anchor: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    iconButtonStyle: IconButtonStyle = IconButtonStyleDefaults.Primary,
    iconButtonSize: IconButtonSize = IconButtonSizeDefaults.M,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(iconButtonSize.iconSize)
            .debounceClickable(
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = iconButtonSize.rippleSize,
                ),
                onClick = onClick,
                enabled = enabled,
                debounce = debounce,
                role = Role.Button,
            )
    ) {
        if (iconResId != null) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = contentDescription,
                tint = if (enabled) {
                    iconButtonStyle.iconColor
                } else {
                    iconButtonStyle.disabledIconColor
                },
            )
        }
        anchor?.invoke()
    }
}

@PreviewLightDark
@Composable
private fun IconButtonPreview() {
    PreviewBackground {
        Row {
            IconButton(
                iconResId = R.drawable.ic_pencil,
                iconButtonSize = IconButtonSizeDefaults.S,
            )
            IconButton(
                iconResId = R.drawable.ic_pencil,
                iconButtonSize = IconButtonSizeDefaults.M,
            )
            IconButton(
                iconResId = R.drawable.ic_pencil,
                iconButtonSize = IconButtonSizeDefaults.L,
            )
        }
    }
}