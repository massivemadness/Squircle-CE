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

package com.blacksquircle.ui.application

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val editorInteractor: EditorInteractor,
) : ViewModel() {

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        settingsManager.setListener(SettingsManager.KEY_FULLSCREEN_MODE) {
            toggleFullscreenMode()
        }

        // Initial value
        toggleFullscreenMode()
    }

    fun handleIntent(intent: Intent?) {
        viewModelScope.launch {
            if (intent != null) {
                val fileUri = intent.data ?: return@launch
                editorInteractor.openFileUri(fileUri)
            }
        }
    }

    private fun toggleFullscreenMode() {
        viewModelScope.launch {
            val fullscreenMode = settingsManager.fullScreenMode
            _viewEvent.send(MainViewEvent.FullScreen(fullscreenMode))
        }
    }
}