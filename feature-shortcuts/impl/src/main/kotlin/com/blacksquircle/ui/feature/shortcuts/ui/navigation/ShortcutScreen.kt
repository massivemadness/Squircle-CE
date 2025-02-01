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

package com.blacksquircle.ui.feature.shortcuts.ui.navigation

import androidx.navigation.NavDirections
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.feature.shortcuts.data.mapper.ShortcutMapper
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.ui.fragment.ShortcutsFragmentDirections

sealed class ShortcutScreen(route: NavDirections) : Screen<NavDirections>(route) {
    class Edit(keybinding: Keybinding) : ShortcutScreen(
        ShortcutsFragmentDirections.toShortcutDialog(ShortcutMapper.toBundle(keybinding))
    )
    class Conflict : ShortcutScreen(
        ShortcutsFragmentDirections.toConflictKeyDialog()
    )
}