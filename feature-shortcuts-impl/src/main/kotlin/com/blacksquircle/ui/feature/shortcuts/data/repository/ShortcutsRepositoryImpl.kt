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

package com.blacksquircle.ui.feature.shortcuts.data.repository

import com.blacksquircle.ui.core.data.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.core.domain.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.domain.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.domain.repository.ShortcutsRepository
import kotlinx.coroutines.withContext

class ShortcutsRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
) : ShortcutsRepository {

    override suspend fun loadShortcuts(): List<Keybinding> {
        return withContext(dispatcherProvider.io()) {
            Shortcut.values().map { keybinding ->
                Keybinding(
                    shortcut = keybinding,
                    value = settingsManager.load(keybinding.key, keybinding.defaultValue)
                )
            }
        }
    }
}