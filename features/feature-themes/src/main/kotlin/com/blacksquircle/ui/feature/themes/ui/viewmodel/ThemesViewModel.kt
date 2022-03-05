/*
 * Copyright 2022 Squircle IDE contributors.
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
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.extensions.toHexString
import com.blacksquircle.ui.core.viewstate.ViewState
import com.blacksquircle.ui.domain.providers.resources.StringProvider
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.converter.ThemeConverter
import com.blacksquircle.ui.feature.themes.domain.model.Meta
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.feature.themes.ui.viewstate.NewThemeViewState
import com.blacksquircle.ui.feature.themes.ui.viewstate.ThemesViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemesViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val themesRepository: ThemesRepository
) : ViewModel() {

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent

    private val _popBackStackEvent = MutableSharedFlow<Unit>()
    val popBackStackEvent: SharedFlow<Unit> = _popBackStackEvent

    private val _themesState = MutableStateFlow<ViewState>(ViewState.Loading)
    val themesState: StateFlow<ViewState> = _themesState

    private val _newThemeState = MutableStateFlow<ViewState>(
        NewThemeViewState.MetaData(Meta(), emptyList()))
    val newThemeState: StateFlow<ViewState> = _newThemeState

    init {
        fetchThemes("")
    }

    fun fetchThemes(query: String) {
        viewModelScope.launch {
            try {
                val themes = themesRepository.fetchThemes(query)
                if (themes.isNotEmpty()) {
                    _themesState.value = ThemesViewState.Data(query, themes)
                } else {
                    _themesState.value = ThemesViewState.Empty(query)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
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
                _toastEvent.emit(stringProvider.getString(R.string.message_theme_syntax_exception))
            }
        }
    }

    fun exportTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.exportTheme(themeModel)
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_theme_exported,
                    themeModel.name.lowercase()
                ))
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun removeTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.removeTheme(themeModel)
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_theme_removed,
                    themeModel.name
                ))
                fetchThemes("")
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun selectTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.selectTheme(themeModel)
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_selected,
                    themeModel.name
                ))
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
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
                _toastEvent.emit(stringProvider.getString(
                    R.string.message_new_theme_available,
                    meta.name
                ))
                _popBackStackEvent.emit(Unit)
                fetchThemes("")
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _toastEvent.emit(stringProvider.getString(R.string.message_unknown_exception))
            }
        }
    }

    fun onThemeNameChanged(value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    name = value
                )
            )
        }
    }

    fun onThemeAuthorChanged(value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    author = value
                )
            )
        }
    }

    fun onThemeDescriptionChanged(value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    description = value
                )
            )
        }
    }

    fun onThemePropertyChanged(key: Property, value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                properties = state.properties.map { propertyItem ->
                    if (propertyItem.propertyKey == key) {
                        propertyItem.copy(propertyValue = value)
                    } else {
                        propertyItem
                    }
                }
            )
        }
    }

    private fun loadProperties(themeModel: ThemeModel) {
        _newThemeState.value = NewThemeViewState.MetaData(
            meta = Meta(
                uuid = themeModel.uuid,
                name = themeModel.name,
                author = themeModel.author,
                description = themeModel.description
            ),
            properties = listOf(
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
                    themeModel.colorScheme.gutterDividerColor.toHexString()
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
                    themeModel.colorScheme.numberColor.toHexString()
                ),
                PropertyItem(
                    Property.OPERATOR_COLOR,
                    themeModel.colorScheme.operatorColor.toHexString()
                ),
                PropertyItem(
                    Property.KEYWORD_COLOR,
                    themeModel.colorScheme.keywordColor.toHexString()
                ),
                PropertyItem(
                    Property.TYPE_COLOR,
                    themeModel.colorScheme.typeColor.toHexString()
                ),
                PropertyItem(
                    Property.LANG_CONST_COLOR,
                    themeModel.colorScheme.langConstColor.toHexString()
                ),
                PropertyItem(
                    Property.PREPROCESSOR_COLOR,
                    themeModel.colorScheme.preprocessorColor.toHexString()
                ),
                PropertyItem(
                    Property.VARIABLE_COLOR,
                    themeModel.colorScheme.variableColor.toHexString()
                ),
                PropertyItem(
                    Property.METHOD_COLOR,
                    themeModel.colorScheme.methodColor.toHexString()
                ),
                PropertyItem(
                    Property.STRING_COLOR,
                    themeModel.colorScheme.stringColor.toHexString()
                ),
                PropertyItem(
                    Property.COMMENT_COLOR,
                    themeModel.colorScheme.commentColor.toHexString()
                ),
                PropertyItem(
                    Property.TAG_COLOR,
                    themeModel.colorScheme.tagColor.toHexString()
                ),
                PropertyItem(
                    Property.TAG_NAME_COLOR,
                    themeModel.colorScheme.tagNameColor.toHexString()
                ),
                PropertyItem(
                    Property.ATTR_NAME_COLOR,
                    themeModel.colorScheme.attrNameColor.toHexString()
                ),
                PropertyItem(
                    Property.ATTR_VALUE_COLOR,
                    themeModel.colorScheme.attrValueColor.toHexString()
                ),
                PropertyItem(
                    Property.ENTITY_REF_COLOR,
                    themeModel.colorScheme.entityRefColor.toHexString()
                )
            )
        )
    }

    companion object {
        private const val TAG = "ThemesViewModel"
    }
}