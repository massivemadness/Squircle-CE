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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.converter.ThemeConverter
import com.blacksquircle.ui.feature.themes.domain.model.Meta
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.feature.themes.ui.mvi.NewThemeViewState
import com.blacksquircle.ui.feature.themes.ui.mvi.ThemeIntent
import com.blacksquircle.ui.feature.themes.ui.mvi.ThemesViewState
import com.blacksquircle.ui.feature.themes.ui.navigation.ThemesScreen
import com.blacksquircle.ui.uikit.extensions.toHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.blacksquircle.ui.uikit.R as UiR

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
        loadThemes()
    }

    fun obtainEvent(event: ThemeIntent) {
        when (event) {
            is ThemeIntent.LoadThemes -> loadThemes()

            is ThemeIntent.SearchThemes -> loadThemes(event)
            is ThemeIntent.ImportTheme -> importTheme(event)
            is ThemeIntent.ExportTheme -> exportTheme(event)
            is ThemeIntent.SelectTheme -> selectTheme(event)
            is ThemeIntent.RemoveTheme -> removeTheme(event)

            is ThemeIntent.CreateTheme -> createTheme(event)
            is ThemeIntent.ChooseColor -> chooseColor(event)
            is ThemeIntent.LoadProperties -> fetchProperties(event)

            is ThemeIntent.ChangeName -> onThemeNameChanged(event)
            is ThemeIntent.ChangeAuthor -> onThemeAuthorChanged(event)
            is ThemeIntent.ChangeDescription -> onThemeDescriptionChanged(event)
            is ThemeIntent.ChangeColor -> onThemeColorChanged(event)
        }
    }

    private fun loadThemes() {
        viewModelScope.launch {
            try {
                val themes = themesRepository.loadThemes()
                _themesState.update {
                    if (themes.isNotEmpty()) {
                        ThemesViewState.Data(query = "", themes = themes)
                    } else {
                        ThemesViewState.Empty(query = "")
                    }
                }
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

    private fun loadThemes(event: ThemeIntent.SearchThemes) {
        viewModelScope.launch {
            try {
                val themes = themesRepository.loadThemes(event.query)
                _themesState.update {
                    if (themes.isNotEmpty()) {
                        ThemesViewState.Data(event.query, themes)
                    } else {
                        ThemesViewState.Empty(event.query)
                    }
                }
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

    private fun importTheme(event: ThemeIntent.ImportTheme) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.importTheme(event.fileUri)
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

    private fun exportTheme(event: ThemeIntent.ExportTheme) {
        viewModelScope.launch {
            try {
                themesRepository.exportTheme(event.themeModel, event.fileUri)
                _viewEvent.send(
                    ViewEvent.Toast(stringProvider.getString(R.string.message_saved)),
                )
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

    private fun selectTheme(event: ThemeIntent.SelectTheme) {
        viewModelScope.launch {
            try {
                themesRepository.selectTheme(event.themeModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_selected,
                            event.themeModel.name,
                        ),
                    ),
                )
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

    private fun removeTheme(event: ThemeIntent.RemoveTheme) {
        viewModelScope.launch {
            try {
                themesRepository.removeTheme(event.themeModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_theme_removed,
                            event.themeModel.name,
                        ),
                    ),
                )
                loadThemes()
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

    private fun createTheme(event: ThemeIntent.CreateTheme) {
        viewModelScope.launch {
            try {
                themesRepository.createTheme(event.meta, event.properties)
                _viewEvent.send(ViewEvent.PopBackStack())
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_new_theme_available,
                            event.meta.name,
                        ),
                    ),
                )
                loadThemes()
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

    private fun chooseColor(event: ThemeIntent.ChooseColor) {
        viewModelScope.launch {
            try {
                val screen = ThemesScreen.ChooseColor(event.key.key, event.value)
                _viewEvent.send(ViewEvent.Navigation(screen))
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun fetchProperties(event: ThemeIntent.LoadProperties) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.loadTheme(event.uuid ?: "unknown")
                loadProperties(themeModel)
            } catch (e: Exception) {
                val themeModel = ThemeConverter.toModel(null)
                loadProperties(themeModel)
            }
        }
    }

    private fun onThemeNameChanged(event: ThemeIntent.ChangeName) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    name = event.value,
                ),
            )
        }
    }

    private fun onThemeAuthorChanged(event: ThemeIntent.ChangeAuthor) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    author = event.value,
                ),
            )
        }
    }

    private fun onThemeDescriptionChanged(event: ThemeIntent.ChangeDescription) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(
                    description = event.value,
                ),
            )
        }
    }

    private fun onThemeColorChanged(event: ThemeIntent.ChangeColor) {
        val state = newThemeState.value as? NewThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                properties = state.properties.map { propertyItem ->
                    if (propertyItem.propertyKey.key == event.key) {
                        propertyItem.copy(propertyValue = event.value)
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
                    Property.CURSOR_COLOR,
                    themeModel.colorScheme.cursorColor.toHexString(),
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