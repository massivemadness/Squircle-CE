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
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.data.mapper.ThemeMapper
import com.blacksquircle.ui.feature.themes.data.model.CodePreview
import com.blacksquircle.ui.feature.themes.domain.model.Meta
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.feature.themes.ui.fragment.EditThemeViewState
import com.blacksquircle.ui.feature.themes.ui.fragment.ThemesViewState
import com.blacksquircle.ui.feature.themes.ui.navigation.ThemesScreen
import com.blacksquircle.ui.feature.themes.ui.navigation.ThemesViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import com.blacksquircle.ui.ds.R as UiR

@HiltViewModel
internal class ThemesViewModel @Inject constructor(
    private val stringProvider: StringProvider,
    private val themesRepository: ThemesRepository,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ThemesViewState())
    val viewState: StateFlow<ThemesViewState> = _viewState.asStateFlow()

    // TODO move to other viewmodel
    private val _newThemeState = MutableStateFlow<EditThemeViewState>(EditThemeViewState.MetaData(Meta(), emptyList()))
    val newThemeState: StateFlow<EditThemeViewState> = _newThemeState.asStateFlow()

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
            it.copy(query = query)
        }
        loadThemes(query = query)
    }

    fun onClearQueryClicked() {
        val reload = viewState.value.query.isNotEmpty()
        if (reload) {
            _viewState.update {
                it.copy(query = "")
            }
            loadThemes()
        }
    }

    fun onCreateClicked() {
        viewModelScope.launch {
            val screen = ThemesScreen.Create
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onSelectClicked(themeModel: ThemeModel) {
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
                        stringProvider.getString(UiR.string.common_error_occurred),
                    ),
                )
            }
        }
    }

    fun onExportClicked(themeModel: ThemeModel) {
        viewModelScope.launch {
            pendingExport = themeModel
            val themeName = themeModel.name + ".json"
            _viewEvent.send(ThemesViewEvent.ChooseExportFile(themeName))
        }
    }

    fun onExportFileSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                if (pendingExport == null) {
                    throw IllegalStateException("Theme is not selected")
                }
                themesRepository.exportTheme(pendingExport!!, fileUri)
                pendingExport = null

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

    fun onEditClicked(themeModel: ThemeModel) {

    }

    fun onRemoveClicked(themeModel: ThemeModel) {
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

    private fun loadThemes(query: String = "") {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            try {
                _viewState.update {
                    it.copy(isLoading = true)
                }
                val themes = themesRepository.loadThemes(query)
                delay(300L) // too fast, avoid blinking
                _viewState.update {
                    it.copy(
                        themes = themes,
                        fontPath = settingsManager.fontType,
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

    private fun importTheme(fileUri: Uri) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.importTheme(fileUri)
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

    private fun createTheme(meta: Meta, properties: List<PropertyItem>) {
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

    private fun chooseColor(property: PropertyItem) {
        viewModelScope.launch {
            try {
                val screen = ThemesScreen.ChooseColor(property.propertyKey.key, property.propertyValue)
                _viewEvent.send(ViewEvent.Navigation(screen))
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun fetchProperties(uuid: String?) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.loadTheme(uuid ?: "unknown")
                loadProperties(themeModel)
            } catch (e: Exception) {
                val themeModel = ThemeMapper.toModel(null)
                loadProperties(themeModel)
            }
        }
    }

    private fun onThemeNameChanged(name: String) {
        val state = newThemeState.value as? EditThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(name = name),
            )
        }
    }

    private fun onThemeAuthorChanged(author: String) {
        val state = newThemeState.value as? EditThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(author = author),
            )
        }
    }

    private fun onThemeDescriptionChanged(description: String) {
        val state = newThemeState.value as? EditThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                meta = state.meta.copy(description = description),
            )
        }
    }

    private fun onThemeColorChanged(property: PropertyItem) {
        val state = newThemeState.value as? EditThemeViewState.MetaData
        if (state != null) {
            _newThemeState.value = state.copy(
                properties = state.properties.map { propertyItem ->
                    if (propertyItem.propertyKey == property.propertyKey) {
                        propertyItem.copy(propertyValue = property.propertyValue)
                    } else {
                        propertyItem
                    }
                },
            )
        }
    }

    private fun loadProperties(themeModel: ThemeModel) {
        _newThemeState.value = EditThemeViewState.MetaData(
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