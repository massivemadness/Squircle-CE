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

package com.blacksquircle.ui.feature.shortcuts.ui.keybinding.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.ds.R as UiR

@Composable
@ReadOnlyComposable
internal fun keybindingResource(ctrl: Boolean, shift: Boolean, alt: Boolean, key: Char): String {
    val noneSet = stringResource(R.string.shortcut_none)
    val ctrlLabel = stringResource(UiR.string.common_ctrl)
    val shiftLabel = stringResource(UiR.string.common_shift)
    val altLabel = stringResource(UiR.string.common_alt)
    val spaceLabel = stringResource(UiR.string.common_space)
    val tabLabel = stringResource(UiR.string.common_tab)
    return StringBuilder().apply {
        val isCtrlOrAltPressed = ctrl || alt
        if (!isCtrlOrAltPressed || key == '\u0000') {
            append(noneSet)
        } else {
            if (ctrl) append("$ctrlLabel + ")
            if (shift) append("$shiftLabel + ")
            if (alt) append("$altLabel + ")
            when (key) {
                ' ' -> append(spaceLabel)
                '\t' -> append(tabLabel)
                else -> append(key.uppercaseChar())
            }
        }
    }.toString()
}