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

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
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
                val value = settingsManager.load(
                    key = keybinding.key,
                    defaultValue = keybinding.defaultValue,
                )
                Keybinding(
                    shortcut = keybinding,
                    isCtrl = value[0] == '1',
                    isShift = value[1] == '1',
                    isAlt = value[2] == '1',
                    key = value[3],
                )
            }
        }
    }

    override suspend fun restoreDefaults() {
        withContext(dispatcherProvider.io()) {
            Shortcut.values().forEach { keybinding ->
                settingsManager.remove(keybinding.key)
            }
        }
    }

    override suspend fun reassign(keybinding: Keybinding) {
        withContext(dispatcherProvider.io()) {
            val value = StringBuilder().apply {
                val isValid = keybinding.isCtrl || keybinding.isAlt
                if (keybinding.isCtrl) append('1') else append('0')
                if (keybinding.isShift) append('1') else append('0')
                if (keybinding.isAlt) append('1') else append('0')
                if (isValid) append(keybinding.key) else append('\u0000')
            }
            settingsManager.update(keybinding.shortcut.key, value.toString())
        }
    }

    override suspend fun disable(keybinding: Keybinding) {
        withContext(dispatcherProvider.io()) {
            val value = StringBuilder().apply {
                append('0') // ctrl
                append('0') // shift
                append('0') // alt
                append('\u0000') // none
            }
            settingsManager.update(keybinding.shortcut.key, value.toString())
        }
    }
}