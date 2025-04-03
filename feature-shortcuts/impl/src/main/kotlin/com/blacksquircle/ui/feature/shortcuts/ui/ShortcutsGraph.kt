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

package com.blacksquircle.ui.feature.shortcuts.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.shortcuts.api.navigation.ConflictKeyDialog
import com.blacksquircle.ui.feature.shortcuts.api.navigation.EditKeybindingDialog
import com.blacksquircle.ui.feature.shortcuts.api.navigation.ShortcutsScreen
import com.blacksquircle.ui.feature.shortcuts.ui.conflict.ConflictKeyScreen
import com.blacksquircle.ui.feature.shortcuts.ui.keybinding.KeybindingScreen
import com.blacksquircle.ui.feature.shortcuts.ui.shortcuts.ShortcutsScreen

fun NavGraphBuilder.shortcutsGraph(navController: NavController) {
    composable<ShortcutsScreen> {
        ShortcutsScreen(navController)
    }
    dialog<EditKeybindingDialog> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<EditKeybindingDialog>()
        KeybindingScreen(navArgs, navController)
    }
    dialog<ConflictKeyDialog> {
        ConflictKeyScreen(navController)
    }
}