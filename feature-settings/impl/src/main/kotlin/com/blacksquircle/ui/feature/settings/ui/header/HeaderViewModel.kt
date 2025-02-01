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

package com.blacksquircle.ui.feature.settings.ui.header

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.navigation.Screen
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HeaderViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _viewState = MutableStateFlow(HeaderListState())
    val viewState: StateFlow<HeaderListState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    val fullscreenMode: Boolean
        get() = settingsManager.fullScreenMode

    fun selectHeader(screen: Screen<*>) {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun popBackStack() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack())
        }
    }
}