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

package com.blacksquircle.ui.feature.themes.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.domain.resources.StringProvider
import com.blacksquircle.ui.core.ui.extensions.toHexString
import com.blacksquircle.ui.core.ui.viewstate.ViewEvent
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ThemesViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val themesRepository: ThemesRepository,
) : ViewModel() {

    private val _themesState = MutableStateFlow<ThemesViewState>(ThemesViewState.Loading)
    val themesState: StateFlow<ThemesViewState> = _themesState.asStateFlow()

    private val _newThemeState = MutableStateFlow<NewThemeViewState>(NewThemeViewState.MetaData(Meta(), emptyList()))
    val newThemeState: StateFlow<NewThemeViewState> = _newThemeState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

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
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(R.string.message_error_occurred),
                    ),
                )
            }
        }
    }

    fun importTheme(uri: Uri) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.importTheme(uri)
                loadProperties(themeModel)
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(R.string.message_theme_syntax_exception),
                    ),
                )
            }
        }
    }

    fun exportTheme(themeModel: ThemeModel, fileUri: Uri) {
        viewModelScope.launch {
            try {
                themesRepository.exportTheme(themeModel, fileUri)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_saved)),
                )
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(R.string.message_error_occurred),
                    ),
                )
            }
        }
    }

    fun removeTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.removeTheme(themeModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_theme_removed,
                            themeModel.name,
                        ),
                    ),
                )
                fetchThemes("")
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(R.string.message_error_occurred),
                    ),
                )
            }
        }
    }

    fun selectTheme(themeModel: ThemeModel) {
        viewModelScope.launch {
            try {
                themesRepository.selectTheme(themeModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_selected,
                            themeModel.name,
                        ),
                    ),
                )
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(R.string.message_error_occurred),
                    ),
                )
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
                _viewEvent.send(ViewEvent.PopBackStack())
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_new_theme_available,
                            meta.name,
                        ),
                    ),
                )
                fetchThemes("")
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(R.string.message_error_occurred),
                    ),
                )
            }
        }
    }

    fun onThemeNameChanged(value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    name = value,
                ),
            )
        }
    }

    fun onThemeAuthorChanged(value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    author = value,
                ),
            )
        }
    }

    fun onThemeDescriptionChanged(value: String) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    description = value,
                ),
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
                },
            )
        }
    }

    private fun loadProperties(themeModel: ThemeModel) {
        _newThemeState.value = NewThemeViewState.MetaData(
            meta = Meta(
                uuid = themeModel.uuid,
                name = themeModel.name,
                author = themeModel.author,
                description = themeModel.description,
            ),
            properties = listOf(
                PropertyItem(
                    Property.TEXT_COLOR,
                    themeModel.colorScheme.textColor.toHexString(),
                ),
                PropertyItem(
                    Property.BACKGROUND_COLOR,
                    themeModel.colorScheme.backgroundColor.toHexString(),
                ),
                PropertyItem(
                    Property.GUTTER_COLOR,
                    themeModel.colorScheme.gutterColor.toHexString(),
                ),
                PropertyItem(
                    Property.GUTTER_DIVIDER_COLOR,
                    themeModel.colorScheme.gutterDividerColor.toHexString(),
                ),
                PropertyItem(
                    Property.GUTTER_CURRENT_LINE_NUMBER_COLOR,
                    themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString(),
                ),
                PropertyItem(
                    Property.GUTTER_TEXT_COLOR,
                    themeModel.colorScheme.gutterTextColor.toHexString(),
                ),
                PropertyItem(
                    Property.SELECTED_LINE_COLOR,
                    themeModel.colorScheme.selectedLineColor.toHexString(),
                ),
                PropertyItem(
                    Property.SELECTION_COLOR,
                    themeModel.colorScheme.selectionColor.toHexString(),
                ),
                PropertyItem(
                    Property.SUGGESTION_QUERY_COLOR,
                    themeModel.colorScheme.suggestionQueryColor.toHexString(),
                ),
                PropertyItem(
                    Property.FIND_RESULT_BACKGROUND_COLOR,
                    themeModel.colorScheme.findResultBackgroundColor.toHexString(),
                ),
                PropertyItem(
                    Property.DELIMITER_BACKGROUND_COLOR,
                    themeModel.colorScheme.delimiterBackgroundColor.toHexString(),
                ),
                PropertyItem(
                    Property.NUMBER_COLOR,
                    themeModel.colorScheme.numberColor.toHexString(),
                ),
                PropertyItem(
                    Property.OPERATOR_COLOR,
                    themeModel.colorScheme.operatorColor.toHexString(),
                ),
                PropertyItem(
                    Property.KEYWORD_COLOR,
                    themeModel.colorScheme.keywordColor.toHexString(),
                ),
                PropertyItem(
                    Property.TYPE_COLOR,
                    themeModel.colorScheme.typeColor.toHexString(),
                ),
                PropertyItem(
                    Property.LANG_CONST_COLOR,
                    themeModel.colorScheme.langConstColor.toHexString(),
                ),
                PropertyItem(
                    Property.PREPROCESSOR_COLOR,
                    themeModel.colorScheme.preprocessorColor.toHexString(),
                ),
                PropertyItem(
                    Property.VARIABLE_COLOR,
                    themeModel.colorScheme.variableColor.toHexString(),
                ),
                PropertyItem(
                    Property.METHOD_COLOR,
                    themeModel.colorScheme.methodColor.toHexString(),
                ),
                PropertyItem(
                    Property.STRING_COLOR,
                    themeModel.colorScheme.stringColor.toHexString(),
                ),
                PropertyItem(
                    Property.COMMENT_COLOR,
                    themeModel.colorScheme.commentColor.toHexString(),
                ),
                PropertyItem(
                    Property.TAG_COLOR,
                    themeModel.colorScheme.tagColor.toHexString(),
                ),
                PropertyItem(
                    Property.TAG_NAME_COLOR,
                    themeModel.colorScheme.tagNameColor.toHexString(),
                ),
                PropertyItem(
                    Property.ATTR_NAME_COLOR,
                    themeModel.colorScheme.attrNameColor.toHexString(),
                ),
                PropertyItem(
                    Property.ATTR_VALUE_COLOR,
                    themeModel.colorScheme.attrValueColor.toHexString(),
                ),
                PropertyItem(
                    Property.ENTITY_REF_COLOR,
                    themeModel.colorScheme.entityRefColor.toHexString(),
                ),
            ),
        )
    }
}