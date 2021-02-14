/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.feature.fonts.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brackeys.ui.domain.model.font.FontModel
import com.brackeys.ui.domain.repository.fonts.FontsRepository
import com.brackeys.ui.feature.base.viewmodel.BaseViewModel
import com.brackeys.ui.utils.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FontsViewModel @Inject constructor(
    private val fontsRepository: FontsRepository
) : BaseViewModel() {

    val fontsEvent: MutableLiveData<List<FontModel>> = MutableLiveData()
    val validationEvent: MutableLiveData<Boolean> = MutableLiveData()

    val selectEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val insertEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val removeEvent: SingleLiveEvent<String> = SingleLiveEvent()

    var searchQuery = ""

    fun fetchFonts() {
        viewModelScope.launch {
            val fonts = fontsRepository.fetchFonts(searchQuery)
            fontsEvent.value = if (searchQuery.isEmpty()) {
                fonts + internalFonts()
            } else {
                fonts
            }
        }
    }

    fun createFont(fontModel: FontModel) {
        viewModelScope.launch {
            fontsRepository.createFont(fontModel)
            insertEvent.value = fontModel.fontName
        }
    }

    fun removeFont(fontModel: FontModel) {
        viewModelScope.launch {
            fontsRepository.removeFont(fontModel)
            removeEvent.value = fontModel.fontName
            fetchFonts() // update list
        }
    }

    fun selectFont(fontModel: FontModel) {
        viewModelScope.launch {
            fontsRepository.selectFont(fontModel)
            selectEvent.value = fontModel.fontName
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