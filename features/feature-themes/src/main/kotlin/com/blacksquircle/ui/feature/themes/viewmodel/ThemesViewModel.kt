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

package com.blacksquircle.ui.feature.themes.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.data.converter.ThemeConverter
import com.blacksquircle.ui.data.utils.InternalTheme
import com.blacksquircle.ui.data.utils.toHexString
import com.blacksquircle.ui.domain.model.themes.Meta
import com.blacksquircle.ui.domain.model.themes.Property
import com.blacksquircle.ui.domain.model.themes.PropertyItem
import com.blacksquircle.ui.domain.model.themes.ThemeModel
import com.blacksquircle.ui.domain.repository.themes.ThemesRepository
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.utils.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemesViewModel @Inject constructor(
    private val themesRepository: ThemesRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ThemesViewModel"
    }

    val toastEvent = SingleLiveEvent<Int>()
    val themesEvent = MutableLiveData<List<ThemeModel>>()

    val validationEvent = SingleLiveEvent<Boolean>()
    val metaEvent = MutableLiveData<Meta>()
    val propertiesEvent = MutableLiveData<List<PropertyItem>>()

    val selectEvent = SingleLiveEvent<String>()
    val exportEvent = SingleLiveEvent<String>()
    val createEvent = SingleLiveEvent<String>()
    val removeEvent = SingleLiveEvent<String>()

    var searchQuery: String = ""

    fun fetchThemes() {
        viewModelScope.launch {
            try {
                val themes = themesRepository.fetchThemes(searchQuery)
                themesEvent.value = if (searchQuery.isEmpty()) {
                    themes + InternalTheme.getThemes()
                } else {
                    themes
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun importTheme(uri: Uri) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.importTheme(uri)
                loadProperties(themeModel)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_theme_syntax_exception
            }
        }
    }

    fun exportTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.exportTheme(themeModel)
                exportEvent.value = themeModel.name.lowercase()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun removeTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.removeTheme(themeModel)
                removeEvent.value = themeModel.name
                fetchThemes() // update list
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun selectTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.selectTheme(themeModel)
                selectEvent.value = themeModel.name
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun fetchProperties(uuid: String?) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.fetchTheme(uuid ?: "unknown")
                loadProperties(themeModel)
            } catch (e: Exception) {
                val themeModel = ThemeConverter.toModel(null)
                loadProperties(themeModel)
            }
        }
    }

    fun createTheme(meta: Meta, properties: List<PropertyItem>) {
        viewModelScope.launch {
            try {
                themesRepository.createTheme(meta, properties)
                createEvent.value = meta.name
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                toastEvent.value = R.string.message_unknown_exception
            }
        }
    }

    fun validateInput(name: String, author: String, description: String) {
        val isNameValid = name.trim().isValidFileName()
        val isAuthorValid = author.trim().isNotBlank()
        val isDescriptionValid = description.trim().isNotBlank()
        validationEvent.value = isNameValid && isAuthorValid && isDescriptionValid
    }

    private fun loadProperties(themeModel: ThemeModel) {
        metaEvent.value = Meta(
            uuid = themeModel.uuid,
            name = themeModel.name,
            author = themeModel.author,
            description = themeModel.description
        )
        propertiesEvent.value = listOf(
            PropertyItem(
                Property.TEXT_COLOR,
                themeModel.colorScheme.textColor.toHexString()
            ),
            PropertyItem(
                Property.BACKGROUND_COLOR,
                themeModel.colorScheme.backgroundColor.toHexString()
            ),
            PropertyItem(
                Property.GUTTER_COLOR,
                themeModel.colorScheme.gutterColor.toHexString(),
            ),
            PropertyItem(
                Property.GUTTER_DIVIDER_COLOR,
                themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString()
            ),
            PropertyItem(
                Property.GUTTER_CURRENT_LINE_NUMBER_COLOR,
                themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString()
            ),
            PropertyItem(
                Property.GUTTER_TEXT_COLOR,
                themeModel.colorScheme.gutterTextColor.toHexString()
            ),
            PropertyItem(
                Property.SELECTED_LINE_COLOR,
                themeModel.colorScheme.selectedLineColor.toHexString()
            ),
            PropertyItem(
                Property.SELECTION_COLOR,
                themeModel.colorScheme.selectionColor.toHexString()
            ),
            PropertyItem(
                Property.SUGGESTION_QUERY_COLOR,
                themeModel.colorScheme.suggestionQueryColor.toHexString()
            ),
            PropertyItem(
                Property.FIND_RESULT_BACKGROUND_COLOR,
                themeModel.colorScheme.findResultBackgroundColor.toHexString()
            ),
            PropertyItem(
                Property.DELIMITER_BACKGROUND_COLOR,
                themeModel.colorScheme.delimiterBackgroundColor.toHexString()
            ),
            PropertyItem(
                Property.NUMBER_COLOR,
                themeModel.colorScheme.syntaxScheme.numberColor.toHexString()
            ),
            PropertyItem(
                Property.OPERATOR_COLOR,
                themeModel.colorScheme.syntaxScheme.operatorColor.toHexString()
            ),
            PropertyItem(
                Property.KEYWORD_COLOR,
                themeModel.colorScheme.syntaxScheme.keywordColor.toHexString()
            ),
            PropertyItem(
                Property.TYPE_COLOR,
                themeModel.colorScheme.syntaxScheme.typeColor.toHexString()
            ),
            PropertyItem(
                Property.LANG_CONST_COLOR,
                themeModel.colorScheme.syntaxScheme.langConstColor.toHexString()
            ),
            PropertyItem(
                Property.PREPROCESSOR_COLOR,
                themeModel.colorScheme.syntaxScheme.preprocessorColor.toHexString()
            ),
            PropertyItem(
                Property.VARIABLE_COLOR,
                themeModel.colorScheme.syntaxScheme.variableColor.toHexString()
            ),
            PropertyItem(
                Property.METHOD_COLOR,
                themeModel.colorScheme.syntaxScheme.methodColor.toHexString()
            ),
            PropertyItem(
                Property.STRING_COLOR,
                themeModel.colorScheme.syntaxScheme.stringColor.toHexString()
            ),
            PropertyItem(
                Property.COMMENT_COLOR,
                themeModel.colorScheme.syntaxScheme.commentColor.toHexString()
            ),
            PropertyItem(
                Property.TAG_COLOR,
                themeModel.colorScheme.syntaxScheme.tagColor.toHexString()
            ),
            PropertyItem(
                Property.TAG_NAME_COLOR,
                themeModel.colorScheme.syntaxScheme.tagNameColor.toHexString()
            ),
            PropertyItem(
                Property.ATTR_NAME_COLOR,
                themeModel.colorScheme.syntaxScheme.attrNameColor.toHexString()
            ),
            PropertyItem(
                Property.ATTR_VALUE_COLOR,
                themeModel.colorScheme.syntaxScheme.attrValueColor.toHexString()
            ),
            PropertyItem(
                Property.ENTITY_REF_COLOR,
                themeModel.colorScheme.syntaxScheme.entityRefColor.toHexString()
            )
        )
    }
}