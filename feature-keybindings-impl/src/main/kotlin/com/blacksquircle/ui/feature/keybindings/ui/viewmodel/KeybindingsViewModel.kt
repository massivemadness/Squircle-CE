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

package com.blacksquircle.ui.feature.keybindings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.keybindings.domain.model.KeybindingModel
import com.blacksquircle.ui.feature.keybindings.domain.repository.KeybindingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeybindingsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val keybindingsRepository: KeybindingsRepository,
) : ViewModel() {

    private val _keybindings = MutableStateFlow<List<KeybindingModel>>(emptyList())
    val keybindings: StateFlow<List<KeybindingModel>> = _keybindings.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun loadKeybindings() {
        viewModelScope.launch {
            _keybindings.value = keybindingsRepository.loadKeybindings()
        }
    }
}