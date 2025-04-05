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

package com.blacksquircle.ui.feature.shortcuts.ui.keybinding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.shortcuts.api.model.Keybinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class KeybindingViewModel @AssistedInject constructor(
    @Assisted private val keybinding: Keybinding,
) : ViewModel() {

    private val _viewState = MutableStateFlow(initialViewState())
    val viewState: StateFlow<KeybindingViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onKeyPressed(key: Char) {
        _viewState.update { state ->
            state.copy(
                isShift = key.isUpperCase(),
                key = key.uppercaseChar(),
            )
        }
    }

    fun onMultiKeyPressed(ctrl: Boolean, shift: Boolean, alt: Boolean, key: Char) {
        _viewState.update { state ->
            state.copy(
                isCtrl = ctrl,
                isShift = shift,
                isAlt = alt,
                key = key,
            )
        }
    }

    fun onCtrlClicked() {
        _viewState.update { state ->
            state.copy(isCtrl = !state.isCtrl)
        }
    }

    fun onShiftClicked() {
        _viewState.update { state ->
            state.copy(isShift = !state.isShift)
        }
    }

    fun onAltClicked() {
        _viewState.update { state ->
            state.copy(isAlt = !state.isAlt)
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            val viewState = viewState.value
            val keybinding = Keybinding(
                shortcut = viewState.shortcut,
                isCtrl = viewState.isCtrl,
                isShift = viewState.isShift,
                isAlt = viewState.isAlt,
                key = viewState.key,
            )
            _viewEvent.send(KeybindingViewEvent.SendSaveResult(keybinding))
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    private fun initialViewState(): KeybindingViewState {
        return KeybindingViewState(
            shortcut = keybinding.shortcut,
            isCtrl = keybinding.isCtrl,
            isShift = keybinding.isShift,
            isAlt = keybinding.isAlt,
            key = keybinding.key,
        )
    }

    class ParameterizedFactory(private val keybinding: Keybinding) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(keybinding) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted keybinding: Keybinding): KeybindingViewModel
    }
}