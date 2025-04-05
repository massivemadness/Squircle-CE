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
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.core.theme.Theme
import com.blacksquircle.ui.core.theme.ThemeManager
import com.blacksquircle.ui.feature.editor.api.interactor.EditorInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val themeManager: ThemeManager,
    private val editorInteractor: EditorInteractor,
) : ViewModel() {

    private val _viewState = MutableStateFlow(updateViewState())
    val viewState: StateFlow<MainViewState> = _viewState.asStateFlow()

    init {
        settingsManager.setListener(SettingsManager.KEY_APP_THEME) {
            val theme = Theme.of(settingsManager.appTheme)
            themeManager.apply(theme)
        }
        settingsManager.setListener(SettingsManager.KEY_FULLSCREEN_MODE) {
            _viewState.value = updateViewState()
        }
    }

    fun handleIntent(intent: Intent?) {
        viewModelScope.launch {
            if (intent != null) {
                val fileUri = intent.data ?: return@launch
                editorInteractor.openFileUri(fileUri)
            }
        }
    }

    private fun updateViewState(): MainViewState {
        return MainViewState(
            fullscreenMode = settingsManager.fullScreenMode,
        )
    }
}