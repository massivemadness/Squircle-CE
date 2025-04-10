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

package com.blacksquircle.ui.feature.themes.ui.thememaker

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.core.provider.resources.StringProvider
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.domain.model.ColorScheme
import com.blacksquircle.ui.feature.themes.domain.model.Property
import com.blacksquircle.ui.feature.themes.domain.model.PropertyItem
import com.blacksquircle.ui.feature.themes.domain.model.ThemeModel
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import com.blacksquircle.ui.feature.themes.ui.themes.ThemesViewEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import com.blacksquircle.ui.ds.R as UiR

internal class EditThemeViewModel @AssistedInject constructor(
    private val stringProvider: StringProvider,
    private val themesRepository: ThemesRepository,
    @Assisted private val themeId: String?,
) : ViewModel() {

    private val _viewState = MutableStateFlow(EditThemeViewState(isEditMode = isEditMode))
    val viewState: StateFlow<EditThemeViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    private val isEditMode: Boolean
        get() = !themeId.isNullOrEmpty()

    init {
        loadTheme()
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onImportClicked() {
        viewModelScope.launch {
            _viewEvent.send(ThemesViewEvent.ChooseImportFile)
        }
    }

    fun onThemeFileSelected(fileUri: Uri) {
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.importTheme(fileUri)
                loadTheme(themeModel)
            } catch (e: CancellationException) {
                throw e
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

    fun onThemeNameChanged(name: String) {
        _viewState.update {
            it.copy(
                name = name,
                invalidName = false,
            )
        }
    }

    fun onThemeAuthorChanged(author: String) {
        _viewState.update {
            it.copy(
                author = author,
                invalidAuthor = false,
            )
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            try {
                val viewState = viewState.value
                val isNameValid = viewState.name.isNotBlank()
                val isAuthorValid = viewState.author.isNotBlank()
                if (!isNameValid || !isAuthorValid) {
                    _viewState.update {
                        it.copy(
                            invalidName = !isNameValid,
                            invalidAuthor = !isAuthorValid,
                        )
                    }
                    return@launch
                }
                val themeModel = ThemeModel(
                    uuid = themeId ?: UUID.randomUUID().toString(),
                    name = viewState.name,
                    author = viewState.author,
                    isExternal = true,
                    colorScheme = ColorScheme(
                        textColor = viewState.getColor(Property.TEXT_COLOR),
                        cursorColor = viewState.getColor(Property.CURSOR_COLOR),
                        backgroundColor = viewState.getColor(Property.BACKGROUND_COLOR),
                        gutterColor = viewState.getColor(Property.GUTTER_COLOR),
                        gutterDividerColor = viewState.getColor(Property.GUTTER_DIVIDER_COLOR),
                        gutterCurrentLineNumberColor = viewState.getColor(Property.GUTTER_CURRENT_LINE_NUMBER_COLOR),
                        gutterTextColor = viewState.getColor(Property.GUTTER_TEXT_COLOR),
                        selectedLineColor = viewState.getColor(Property.SELECTED_LINE_COLOR),
                        selectionColor = viewState.getColor(Property.SELECTION_COLOR),
                        suggestionQueryColor = viewState.getColor(Property.SUGGESTION_QUERY_COLOR),
                        findResultBackgroundColor = viewState.getColor(Property.FIND_RESULT_BACKGROUND_COLOR),
                        delimiterBackgroundColor = viewState.getColor(Property.DELIMITER_BACKGROUND_COLOR),
                        numberColor = viewState.getColor(Property.NUMBER_COLOR),
                        operatorColor = viewState.getColor(Property.OPERATOR_COLOR),
                        keywordColor = viewState.getColor(Property.KEYWORD_COLOR),
                        typeColor = viewState.getColor(Property.TYPE_COLOR),
                        langConstColor = viewState.getColor(Property.LANG_CONST_COLOR),
                        preprocessorColor = viewState.getColor(Property.PREPROCESSOR_COLOR),
                        variableColor = viewState.getColor(Property.VARIABLE_COLOR),
                        methodColor = viewState.getColor(Property.METHOD_COLOR),
                        stringColor = viewState.getColor(Property.STRING_COLOR),
                        commentColor = viewState.getColor(Property.COMMENT_COLOR),
                        tagColor = viewState.getColor(Property.TAG_COLOR),
                        tagNameColor = viewState.getColor(Property.TAG_NAME_COLOR),
                        attrNameColor = viewState.getColor(Property.ATTR_NAME_COLOR),
                        attrValueColor = viewState.getColor(Property.ATTR_VALUE_COLOR),
                        entityRefColor = viewState.getColor(Property.ENTITY_REF_COLOR),
                    ),
                )
                themesRepository.createTheme(themeModel)
                _viewEvent.send(
                    ViewEvent.Toast(
                        stringProvider.getString(
                            R.string.message_new_theme_available,
                            themeModel.name,
                        ),
                    ),
                )
                _viewEvent.send(ThemesViewEvent.SendSaveResult)
            } catch (e: CancellationException) {
                throw e
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

    fun onColorSelected(property: Property, color: String) {
        _viewState.update {
            it.copy(
                properties = it.properties.map { propertyItem ->
                    if (propertyItem.propertyKey == property) {
                        propertyItem.copy(propertyValue = color)
                    } else {
                        propertyItem
                    }
                },
            )
        }
    }

    private fun loadTheme() {
        if (!isEditMode) {
            return
        }
        viewModelScope.launch {
            try {
                val themeModel = themesRepository.loadTheme(themeId.orEmpty())
                loadTheme(themeModel)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, e.message)
                _viewEvent.send(ViewEvent.Toast(e.message.orEmpty()))
            }
        }
    }

    private fun loadTheme(themeModel: ThemeModel) {
        _viewState.update {
            it.copy(
                isEditMode = true,
                name = themeModel.name,
                author = themeModel.author,
                properties = Property.entries.map { property ->
                    PropertyItem(
                        propertyKey = property,
                        propertyValue = when (property) {
                            Property.TEXT_COLOR -> themeModel.colorScheme.textColor
                            Property.CURSOR_COLOR -> themeModel.colorScheme.cursorColor
                            Property.BACKGROUND_COLOR -> themeModel.colorScheme.backgroundColor
                            Property.GUTTER_COLOR -> themeModel.colorScheme.gutterColor
                            Property.GUTTER_DIVIDER_COLOR -> themeModel.colorScheme.gutterDividerColor
                            Property.GUTTER_CURRENT_LINE_NUMBER_COLOR -> themeModel.colorScheme.gutterCurrentLineNumberColor
                            Property.GUTTER_TEXT_COLOR -> themeModel.colorScheme.gutterTextColor
                            Property.SELECTED_LINE_COLOR -> themeModel.colorScheme.selectedLineColor
                            Property.SELECTION_COLOR -> themeModel.colorScheme.selectionColor
                            Property.SUGGESTION_QUERY_COLOR -> themeModel.colorScheme.suggestionQueryColor
                            Property.FIND_RESULT_BACKGROUND_COLOR -> themeModel.colorScheme.findResultBackgroundColor
                            Property.DELIMITER_BACKGROUND_COLOR -> themeModel.colorScheme.delimiterBackgroundColor
                            Property.NUMBER_COLOR -> themeModel.colorScheme.numberColor
                            Property.OPERATOR_COLOR -> themeModel.colorScheme.operatorColor
                            Property.KEYWORD_COLOR -> themeModel.colorScheme.keywordColor
                            Property.TYPE_COLOR -> themeModel.colorScheme.typeColor
                            Property.LANG_CONST_COLOR -> themeModel.colorScheme.langConstColor
                            Property.PREPROCESSOR_COLOR -> themeModel.colorScheme.preprocessorColor
                            Property.VARIABLE_COLOR -> themeModel.colorScheme.variableColor
                            Property.METHOD_COLOR -> themeModel.colorScheme.methodColor
                            Property.STRING_COLOR -> themeModel.colorScheme.stringColor
                            Property.COMMENT_COLOR -> themeModel.colorScheme.commentColor
                            Property.TAG_COLOR -> themeModel.colorScheme.tagColor
                            Property.TAG_NAME_COLOR -> themeModel.colorScheme.tagNameColor
                            Property.ATTR_NAME_COLOR -> themeModel.colorScheme.attrNameColor
                            Property.ATTR_VALUE_COLOR -> themeModel.colorScheme.attrValueColor
                            Property.ENTITY_REF_COLOR -> themeModel.colorScheme.entityRefColor
                        }.toHexString()
                    )
                },
                invalidName = themeModel.name.isBlank(),
                invalidAuthor = themeModel.author.isBlank(),
            )
        }
    }

    class ParameterizedFactory(private val themeId: String?) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(themeId) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted themeId: String?): EditThemeViewModel
    }
}