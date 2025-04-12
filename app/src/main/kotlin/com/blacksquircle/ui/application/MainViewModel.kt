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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_APP_THEME
import com.blacksquircle.ui.core.settings.SettingsManager.Companion.KEY_FULLSCREEN_MODE
import com.blacksquircle.ui.core.theme.Theme
import com.blacksquircle.ui.core.theme.ThemeManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import com.blacksquircle.ui.feature.editor.api.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.themes.api.interactor.ThemeInteractor
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

internal class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val themeManager: ThemeManager,
    private val themeInteractor: ThemeInteractor,
    private val editorInteractor: EditorInteractor,
    private val languageInteractor: LanguageInteractor,
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<MainViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadComponents()
        registerOnPreferenceChangeListeners()
    }

    override fun onCleared() {
        super.onCleared()
        unregisterOnPreferenceChangeListeners()
    }

    fun onUpdateAvailable() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.Navigation(UpdateDialog))
        }
    }

    fun onNewIntent(intent: Intent) {
        viewModelScope.launch {
            val fileUri = intent.data ?: return@launch
            editorInteractor.openFileUri(fileUri)
        }
    }

    private fun loadComponents() {
        viewModelScope.launch {
            // Step 1: Load syntax files
            languageInteractor.loadGrammars()

            // Step 2: Load current theme
            themeInteractor.loadTheme(settingsManager.editorTheme)

            // Done
            _viewState.update {
                it.copy(isLoading = false)
            }
        }
    }

    private fun registerOnPreferenceChangeListeners() {
        settingsManager.registerListener(KEY_APP_THEME) {
            val theme = Theme.of(settingsManager.appTheme)
            themeManager.apply(theme) // change splash background
            _viewState.value = updateViewState()
        }
        settingsManager.registerListener(KEY_FULLSCREEN_MODE) {
            _viewState.value = updateViewState()
        }
    }

    private fun unregisterOnPreferenceChangeListeners() {
        settingsManager.unregisterListener(KEY_APP_THEME)
        settingsManager.unregisterListener(KEY_FULLSCREEN_MODE)
    }

    private fun updateViewState(): MainViewState {
        return MainViewState(
            isLoading = true,
            appTheme = Theme.of(settingsManager.appTheme),
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