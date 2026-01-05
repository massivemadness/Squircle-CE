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

package com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.compose.keybindingResource

@Composable
internal fun Combination(
    keybinding: Keybinding,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = modifier
            .background(SquircleTheme.colors.colorBackgroundSecondary, shape)
            .border(1.dp, SquircleTheme.colors.colorOutline, shape)
    ) {
        Text(
            text = keybindingResource(
                ctrl = keybinding.isCtrl,
                shift = keybinding.isShift,
                alt = keybinding.isAlt,
                key = keybinding.key,
            ),
            style = SquircleTheme.typography.text14Regular,
            color = SquircleTheme.colors.colorTextAndIconPrimary,
            overflow = TextOverflow.Clip,
            maxLines = 1,
            modifier = Modifier.padding(
                vertical = 2.dp,
                horizontal = 6.dp,
            )
        )
    }
}