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

package com.blacksquircle.ui.feature.shortcuts.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.shortcuts.domain.model.Keybinding
import com.blacksquircle.ui.feature.shortcuts.domain.repository.ShortcutsRepository
import com.blacksquircle.ui.uikit.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShortcutsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val shortcutsRepository: ShortcutsRepository,
) : ViewModel() {

    private val _shortcuts = MutableStateFlow<List<Keybinding>>(emptyList())
    val shortcuts: StateFlow<List<Keybinding>> = _shortcuts.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadShortcuts()
    }

    fun obtainEvent(event: ShortcutIntent) {
        when (event) {
            is ShortcutIntent.LoadShortcuts -> loadShortcuts()
            is ShortcutIntent.SaveShortcut -> saveShortcut(event)
        }
    }

    private fun loadShortcuts() {
        viewModelScope.launch {
            try {
                _shortcuts.value = shortcutsRepository.loadShortcuts()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.common_error_occurred)),
                )
            }
        }
    }

    private fun saveShortcut(event: ShortcutIntent.SaveShortcut) {
        viewModelScope.launch {
            try {
                shortcutsRepository.saveShortcut(event.keybinding)
                loadShortcuts()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.common_error_occurred)),
                )
            }
        }
    }
}