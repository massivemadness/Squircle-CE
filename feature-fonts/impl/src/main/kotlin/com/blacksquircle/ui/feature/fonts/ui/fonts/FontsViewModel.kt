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

package com.blacksquircle.ui.feature.fonts.ui.fonts

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import com.blacksquircle.ui.ds.R as UiR

internal class FontsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val fontsRepository: FontsRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _viewState = MutableStateFlow(FontsViewState())
    val viewState: StateFlow<FontsViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private var currentJob: Job? = null

    init {
        loadFonts()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onQueryChanged(query: String) {
        _viewState.update {
            it.copy(searchQuery = query)
        }
        loadFonts(query = query)
    }

    fun onClearQueryClicked() {
        val reload = viewState.value.searchQuery.isNotEmpty()
        if (reload) {
            _viewState.update {
                it.copy(searchQuery = "")
            }
            loadFonts()
        }
    }

    fun onSelectClicked(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.selectFont(fontModel)
                _viewState.update { state ->
                    state.copy(selectedFont = fontModel.uuid)
                }
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_selected,
                            fontModel.name,
                        ),
                    ),
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

    fun onRemoveClicked(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.removeFont(fontModel)
                _viewState.update { state ->
                    state.copy(
                        fonts = state.fonts.filterNot { it == fontModel },
                        selectedFont = settingsManager.fontType,
                    )
                }
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_font_removed,
                            fontModel.name,
                        ),
                    ),
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

    fun onImportClicked() {
        viewModelScope.launch {
            _viewEvent.send(FontsViewEvent.ChooseFont)
        }
    }

    fun onFontLoaded(fileUri: Uri) {
        viewModelScope.launch {
            try {
                fontsRepository.importFont(fileUri)
                _viewState.update {
                    it.copy(searchQuery = "")
                }
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_new_font_available)),
                )
                loadFonts()
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

    private fun loadFonts(query: String = "") {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _viewState.update {
                    it.copy(isLoading = true)
                }

                val fonts = fontsRepository.loadFonts(query = query)
                delay(300L) // too fast, avoid blinking

                _viewState.update {
                    it.copy(
                        fonts = fonts,
                        selectedFont = settingsManager.fontType,
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
        lateinit var viewModelProvider: Provider<FontsViewModel>

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }
}