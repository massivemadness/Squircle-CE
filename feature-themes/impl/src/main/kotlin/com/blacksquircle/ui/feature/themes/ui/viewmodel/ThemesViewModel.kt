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

package com.blacksquircle.ui.feature.themes.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.contract.FileType
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.fonts.api.interactor.FontsInteractor
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.model.CodePreview
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.feature.themes.ui.fragment.ThemesViewState
import com.blacksquircle.ui.feature.themes.ui.navigation.ThemesScreen
import com.blacksquircle.ui.feature.themes.ui.navigation.ThemesViewEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.cancellation.CancellationException
import com.blacksquircle.ui.ds.R as UiR

internal class ThemesViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val fontsInteractor: FontsInteractor,
    private val themesRepository: ThemesRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ThemesViewState())
    val viewState: StateFlow<ThemesViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var pendingExport: ThemeModel? = null
    private var currentJob: Job? = null

    init {
        loadThemes()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack())
        }
    }

    fun onCodePreviewChanged(extension: String) {
        _viewState.update {
            it.copy(preview = CodePreview.of(extension))
        }
    }

    fun onQueryChanged(query: String) {
        _viewState.update {
            it.copy(searchQuery = query)
        }
        loadThemes(query = query)
    }

    fun onClearQueryClicked() {
        val reload = viewState.value.searchQuery.isNotEmpty()
        if (reload) {
            _viewState.update {
                it.copy(searchQuery = "")
            }
            loadThemes()
        }
    }

    fun onCreateClicked() {
        viewModelScope.launch {
            val screen = ThemesScreen.CreateThemeScreen
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onSelectClicked(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.selectTheme(themeModel)
                _viewState.update {
                    it.copy(selectedTheme = themeModel.uuid)
                }
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_selected,
                            themeModel.name,
                        ),
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(UiR.string.common_error_occurred),
                    ),
                )
            }
        }
    }

    fun onExportClicked(themeModel: ThemeModel) {
        viewModelScope.launch {
            pendingExport = themeModel
            val themeName = themeModel.name + FileType.JSON
            _viewEvent.send(ThemesViewEvent.ChooseExportFile(themeName))
        }
    }

    fun onExportFileSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                if (pendingExport != null) {
                    themesRepository.exportTheme(pendingExport!!, fileUri)
                    pendingExport = null
                }
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_saved)),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(UiR.string.common_error_occurred),
                    ),
                )
            }
        }
    }

    fun onEditClicked(themeModel: ThemeModel) {
        viewModelScope.launch {
            val screen = ThemesScreen.EditThemeScreen(themeModel.uuid)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onRemoveClicked(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.removeTheme(themeModel)
                _viewState.update { state ->
                    state.copy(
                        themes = state.themes.filterNot { it == themeModel },
                        selectedTheme = settingsManager.editorTheme,
                    )
                }
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_theme_removed,
                            themeModel.name,
                        ),
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(UiR.string.common_error_occurred),
                    ),
                )
            }
        }
    }

    fun loadThemes(query: String = "") {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _viewState.update {
                    it.copy(isLoading = true)
                }

                val themes = themesRepository.loadThemes(query)
                val typeface = fontsInteractor.current()
                delay(300L) // too fast, avoid blinking

                _viewState.update {
                    it.copy(
                        themes = themes,
                        selectedTheme = settingsManager.editorTheme,
                        typeface = typeface,
                        isLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewState.update {
                    it.copy(isLoading = false)
                }
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    class Factory : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelProvider: Provider<ThemesViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}