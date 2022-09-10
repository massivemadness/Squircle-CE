/*
 * Copyright 2022 Squircle CE contributors.
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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.feature.fonts.domain.model.FontModel
import com.blacksquircle.ui.feature.fonts.domain.repository.FontsRepository
import com.blacksquircle.ui.feature.fonts.ui.viewstate.ExternalFontViewState
import com.blacksquircle.ui.feature.fonts.ui.viewstate.FontsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FontsViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val fontsRepository: FontsRepository
) : ViewModel() {

    private val _fontsState = MutableStateFlow<FontsViewState>(FontsViewState.Loading)
    val fontsState: StateFlow<FontsViewState> = _fontsState.asStateFlow()

    private val _externalFontState = MutableStateFlow<ExternalFontViewState>(ExternalFontViewState.Invalid)
    val externalFontState: StateFlow<ExternalFontViewState> = _externalFontState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    init {
        fetchFonts("")
    }

    fun fetchFonts(query: String) {
        viewModelScope.launch {
            try {
                val fonts = fontsRepository.fetchFonts(query)
                if (fonts.isNotEmpty()) {
                    _fontsState.value = FontsViewState.Data(query, fonts)
                } else {
                    _fontsState.value = FontsViewState.Empty(query)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(R.string.message_error_occurred)
                ))
            }
        }
    }

    fun createFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.createFont(fontModel)
                _viewEvent.send(ViewEvent.PopBackStack())
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(
                        R.string.message_new_font_available,
                        fontModel.fontName
                    )
                ))
                fetchFonts("")
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(R.string.message_error_occurred)
                ))
            }
        }
    }

    fun removeFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.removeFont(fontModel)
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(
                        R.string.message_font_removed,
                        fontModel.fontName
                    )
                ))
                fetchFonts("")
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(R.string.message_error_occurred)
                ))
            }
        }
    }

    fun selectFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.selectFont(fontModel)
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(
                        R.string.message_selected,
                        fontModel.fontName
                    )
                ))
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _viewEvent.send(ViewEvent.Toast(
                    stringProvider.getString(R.string.message_error_occurred)
                ))
            }
        }
    }

    fun validateInput(fontName: String, fontPath: String) {
        val isFontNameValid = fontName.trim().isNotBlank()
        val isFontPathValid = fontPath.trim().isNotBlank() && File(fontPath)
            .run { exists() && name.endsWith(TTF, ignoreCase = true) }

        if (isFontNameValid && isFontPathValid) {
            _externalFontState.value = ExternalFontViewState.Valid
        } else {
            _externalFontState.value = ExternalFontViewState.Invalid
        }
    }

    companion object {
        private const val TAG = "FontsViewModel"
        private const val TTF = ".ttf"
    }
}