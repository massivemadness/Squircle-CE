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

package com.blacksquircle.ui.feature.shortcuts.data.repository

import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.model.Shortcut
import com.blacksquircle.ui.feature.shortcuts.domain.ShortcutRepository
import kotlinx.coroutines.withContext

internal class ShortcutRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
) : ShortcutRepository {

    override suspend fun loadShortcuts(): List<Keybinding> {
        return withContext(dispatcherProvider.io()) {
            Shortcut.entries.map { shortcut ->
                val value = settingsManager.load(
                    key = shortcut.key,
                    defaultValue = shortcut.defaultValue,
                )
                Keybinding(
                    shortcut = shortcut,
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
            Shortcut.entries.forEach { keybinding ->
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