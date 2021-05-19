/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.fonts.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.domain.model.fonts.FontModel
import com.blacksquircle.ui.domain.repository.fonts.FontsRepository
import com.blacksquircle.ui.feature.fonts.R
import com.blacksquircle.ui.utils.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FontsViewModel @Inject constructor(
    private val fontsRepository: FontsRepository
) : ViewModel() {

    companion object {
        private const val TAG = "FontsViewModel"
    }

    val toastEvent = SingleLiveEvent<Int>()
    val fontsEvent = MutableLiveData<List<FontModel>>()
    val validationEvent = MutableLiveData<Boolean>()

    val selectEvent = SingleLiveEvent<String>()
    val insertEvent = SingleLiveEvent<String>()
    val removeEvent = SingleLiveEvent<String>()

    var searchQuery = ""

    fun fetchFonts() {
        viewModelScope.launch {
            try {
                val fonts = fontsRepository.fetchFonts(searchQuery)
                fontsEvent.value = if (searchQuery.isEmpty()) {
                    fonts + internalFonts()
                } else {
                    fonts
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun createFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.createFont(fontModel)
                insertEvent.value = fontModel.fontName
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun removeFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.removeFont(fontModel)
                removeEvent.value = fontModel.fontName
                fetchFonts() // update list
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun selectFont(fontModel: FontModel) {
        viewModelScope.launch {
            try {
                fontsRepository.selectFont(fontModel)
                selectEvent.value = fontModel.fontName
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun validateInput(fontName: String, fontPath: String) {
        val isFontNameValid = fontName.trim().isNotBlank()
        val isFontPathValid = fontPath.trim().isNotBlank() && File(fontPath)
            .run { exists() && name.endsWith(".ttf") }
        validationEvent.value = isFontNameValid && isFontPathValid
    }

    private fun internalFonts(): List<FontModel> {
        return listOf(
            FontModel(
                fontName = "Droid Sans Mono",
                fontPath = "file:///android_asset/fonts/droid_sans_mono.ttf",
                supportLigatures = false,
                isExternal = false
            ),
            FontModel(
                fontName = "JetBrains Mono",
                fontPath = "file:///android_asset/fonts/jetbrains_mono.ttf",
                supportLigatures = true,
                isExternal = false
            ),
            FontModel(
                fontName = "Fira Code",
                fontPath = "file:///android_asset/fonts/fira_code.ttf",
                supportLigatures = true,
                isExternal = false
            ),
            FontModel(
                fontName = "Source Code Pro",
                fontPath = "file:///android_asset/fonts/source_code_pro.ttf",
                supportLigatures = false,
                isExternal = false
            ),
            FontModel(
                fontName = "Anonymous Pro",
                fontPath = "file:///android_asset/fonts/anonymous_pro.ttf",
                supportLigatures = false,
                isExternal = false
            ),
            FontModel(
                fontName = "DejaVu Sans Mono",
                fontPath = "file:///android_asset/fonts/dejavu_sans_mono.ttf",
                supportLigatures = false,
                isExternal = false
            )
        )
    }
}