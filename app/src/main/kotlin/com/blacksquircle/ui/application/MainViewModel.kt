/*
 * Copyright Squircle CE contributors.
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_EDITOR_THEME
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_FULLSCREEN_MODE
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import com.blacksquircle.ui.navigation.api.Navigator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

internal class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val themeInteractor: ThemeInteractor,
    private val editorInteractor: EditorInteractor,
    private val navigator: Navigator,
) : ViewModel() {

    private val _viewState = MutableStateFlow(initialViewState())
    val viewState: StateFlow<MainViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadTheme()

        settingsManager.collect(KEY_EDITOR_THEME)
            .onEach { loadTheme() }
            .launchIn(viewModelScope)

        settingsManager.collect(KEY_FULLSCREEN_MODE)
            .onEach {
                _viewState.update {
                    it.copy(fullscreenMode = settingsManager.fullScreenMode)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUpdateAvailable() {
        navigator.navigate(UpdateRoute)
    }

    fun onNewIntent(intent: Intent) {
        viewModelScope.launch {
            val fileUri = intent.data ?: return@launch
            editorInteractor.openFileUri(fileUri)
        }
    }

    private fun loadTheme() {
        viewModelScope.launch {
            try {
                val colorScheme = themeInteractor.loadTheme(settingsManager.editorTheme)
                _viewState.update {
                    it.copy(colorScheme = colorScheme)
                }

                /**
                 * Wait until new theme applied.
                 * Remove when pausable composition is enabled by default?
                 */
                delay(800)

                _viewState.update {
                    it.copy(isLoading = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))

                _viewState.update {
                    it.copy(
                        colorScheme = null,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun initialViewState(): MainViewState {
        return MainViewState(
            isLoading = true,
            colorScheme = null,
            fullscreenMode = settingsManager.fullScreenMode,
        )
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<MainViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}