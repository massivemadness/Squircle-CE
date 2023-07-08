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

package com.blacksquircle.ui.feature.fonts.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.mvi.FontIntent
import com.blacksquircle.ui.feature.fonts.ui.mvi.FontsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.blacksquircle.ui.uikit.R as UiR

@HiltViewModel
class FontsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val fontsRepository: FontsRepository,
) : ViewModel() {

    private val _fontsState = MutableStateFlow<FontsViewState>(FontsViewState.Loading)
    val fontsState: StateFlow<FontsViewState> = _fontsState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        loadFonts()
    }

    fun obtainEvent(event: FontIntent) {
        when (event) {
            is FontIntent.LoadFonts -> loadFonts()

            is FontIntent.SearchFonts -> loadFonts(event)
            is FontIntent.ImportFont -> importFont(event)
            is FontIntent.SelectFont -> selectFont(event)
            is FontIntent.RemoveFont -> removeFont(event)
        }
    }

    private fun loadFonts() {
        viewModelScope.launch {
            try {
                val fonts = fontsRepository.loadFonts()
                _fontsState.update {
                    if (fonts.isNotEmpty()) {
                        FontsViewState.Data(query = "", fonts = fonts)
                    } else {
                        FontsViewState.Empty(query = "")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    private fun loadFonts(event: FontIntent.SearchFonts) {
        viewModelScope.launch {
            try {
                val fonts = fontsRepository.loadFonts(event.query)
                _fontsState.update {
                    if (fonts.isNotEmpty()) {
                        FontsViewState.Data(event.query, fonts)
                    } else {
                        FontsViewState.Empty(event.query)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    private fun importFont(event: FontIntent.ImportFont) {
        viewModelScope.launch {
            try {
                fontsRepository.importFont(event.fileUri)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_new_font_available)),
                )
                loadFonts()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    private fun selectFont(event: FontIntent.SelectFont) {
        viewModelScope.launch {
            try {
                fontsRepository.selectFont(event.fontModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_selected,
                            event.fontModel.fontName,
                        ),
                    ),
                )
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }

    private fun removeFont(event: FontIntent.RemoveFont) {
        viewModelScope.launch {
            try {
                fontsRepository.removeFont(event.fontModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_font_removed,
                            event.fontModel.fontName,
                        ),
                    ),
                )
                loadFonts()
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(UiR.string.common_error_occurred)),
                )
            }
        }
    }
}