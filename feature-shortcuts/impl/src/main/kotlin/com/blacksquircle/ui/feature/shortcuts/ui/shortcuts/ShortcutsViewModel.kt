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

package com.blacksquircle.ui.feature.shortcuts.ui.shortcuts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.api.navigation.ConflictKeyDialog
import com.blacksquircle.ui.feature.shortcuts.api.navigation.EditKeybindingDialog
import com.blacksquircle.ui.feature.shortcuts.domain.ShortcutRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.cancellation.CancellationException
import com.blacksquircle.ui.ds.R as UiR

internal class ShortcutsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val shortcutRepository: ShortcutRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ShortcutsViewState())
    val viewState: StateFlow<ShortcutsViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var shortcuts: List<Keybinding> = emptyList()
    private var pendingKey: Keybinding? = null
    private var conflictKey: Keybinding? = null

    init {
        loadShortcuts()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onRestoreClicked() {
        viewModelScope.launch {
            try {
                shortcutRepository.restoreDefaults()
                loadShortcuts()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    fun onKeyClicked(keybinding: Keybinding) {
        viewModelScope.launch {
            val screen = EditKeybindingDialog(
                shortcut = keybinding.shortcut,
                isCtrl = keybinding.isCtrl,
                isShift = keybinding.isShift,
                isAlt = keybinding.isAlt,
                keyCode = keybinding.key.code,
            )
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onSaveClicked(keybinding: Keybinding) {
        viewModelScope.launch {
            try {
                val existingKey = shortcuts.find {
                    it.shortcut != keybinding.shortcut &&
                        it.key == keybinding.key &&
                        it.isCtrl == keybinding.isCtrl &&
                        it.isShift == keybinding.isShift &&
                        it.isAlt == keybinding.isAlt
                }
                if (existingKey != null) {
                    pendingKey = keybinding
                    conflictKey = existingKey

                    val screen = ConflictKeyDialog
                    _viewEvent.send(ViewEvent.Navigation(screen))
                } else {
                    shortcutRepository.reassign(keybinding)
                    loadShortcuts()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    fun onResolveClicked(reassign: Boolean) {
        viewModelScope.launch {
            try {
                if (reassign) {
                    shortcutRepository.disable(checkNotNull(conflictKey))
                    shortcutRepository.reassign(checkNotNull(pendingKey))
                }
                pendingKey = null
                conflictKey = null
                loadShortcuts()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    private fun loadShortcuts() {
        viewModelScope.launch {
            try {
                shortcuts = shortcutRepository.loadShortcuts()
                _viewState.value = ShortcutsViewState(
                    shortcuts = shortcuts.groupBy { it.shortcut.group },
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<ShortcutsViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}