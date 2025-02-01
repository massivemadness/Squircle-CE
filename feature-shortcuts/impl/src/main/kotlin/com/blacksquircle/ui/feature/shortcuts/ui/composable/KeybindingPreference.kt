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

package com.blacksquircle.ui.feature.shortcuts.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.ds.R as UiR

@Composable
@ReadOnlyComposable
internal fun keybindingResource(keybinding: Keybinding): String {
    val noneSet = stringResource(R.string.shortcut_none)
    val ctrl = stringResource(UiR.string.common_ctrl)
    val shift = stringResource(UiR.string.common_shift)
    val alt = stringResource(UiR.string.common_alt)
    val space = stringResource(UiR.string.common_space)
    val tab = stringResource(UiR.string.common_tab)
    return StringBuilder().apply {
        val isCtrlOrAltPressed = keybinding.isCtrl || keybinding.isAlt
        if (!isCtrlOrAltPressed || keybinding.key == '\u0000') {
            append(noneSet)
        } else {
            if (keybinding.isCtrl) append("$ctrl + ")
            if (keybinding.isShift) append("$shift + ")
            if (keybinding.isAlt) append("$alt + ")
            when (keybinding.key) {
                ' ' -> append(space)
                '\t' -> append(tab)
                else -> append(keybinding.key.uppercaseChar())
            }
        }
    }.toString()
}