/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.ui.themes.viewmodel

import android.os.Environment
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.lightteam.filesystem.model.FileModel
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.localfilesystem.utils.isValidFileName
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.ThemeConverter
import com.lightteam.modpeide.data.model.theme.ExternalTheme
import com.lightteam.modpeide.data.model.theme.Meta
import com.lightteam.modpeide.data.model.theme.Property
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.data.utils.extensions.toHexString
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.database.entity.theme.ThemeEntity
import com.lightteam.modpeide.domain.model.theme.ThemeModel
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.ui.themes.adapters.item.PropertyItem
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import javax.inject.Named

class ThemesViewModel @ViewModelInject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val appDatabase: AppDatabase,
    @Named("Local")
    private val filesystem: Filesystem,
    private val gson: Gson
) : BaseViewModel() {

    companion object {
        private const val TAG = "ThemesViewModel"

        private const val FALLBACK_COLOR = "#000000"
    }

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent()
    val themesEvent: MutableLiveData<List<ThemeModel>> = MutableLiveData()

    val validationEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val metaEvent: MutableLiveData<Meta> = MutableLiveData()
    val propertiesEvent: MutableLiveData<List<PropertyItem>> = MutableLiveData()

    val selectEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val exportEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val createEvent: SingleLiveEvent<String> = SingleLiveEvent()
    val removeEvent: SingleLiveEvent<String> = SingleLiveEvent()

    var searchQuery: String = ""

    // region PROPERTIES

    private var textColor: String = FALLBACK_COLOR
    private var backgroundColor: String = FALLBACK_COLOR
    private var gutterColor: String = FALLBACK_COLOR
    private var gutterDividerColor: String = FALLBACK_COLOR
    private var gutterCurrentLineNumberColor: String = FALLBACK_COLOR
    private var gutterTextColor: String = FALLBACK_COLOR
    private var selectedLineColor: String = FALLBACK_COLOR
    private var selectionColor: String = FALLBACK_COLOR
    private var suggestionQueryColor: String = FALLBACK_COLOR
    private var findResultBackgroundColor: String = FALLBACK_COLOR
    private var delimiterBackgroundColor: String = FALLBACK_COLOR
    private var numberColor: String = FALLBACK_COLOR
    private var operatorColor: String = FALLBACK_COLOR
    private var keywordColor: String = FALLBACK_COLOR
    private var typeColor: String = FALLBACK_COLOR
    private var langConstColor: String = FALLBACK_COLOR
    private var methodColor: String = FALLBACK_COLOR
    private var stringColor: String = FALLBACK_COLOR
    private var commentColor: String = FALLBACK_COLOR

    // endregion PROPERTIES

    fun fetchThemes() {
        appDatabase.themeDao().loadAll(searchQuery)
            .map { it.map(ThemeConverter::toModel) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { themesEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun selectTheme(themeModel: ThemeModel) {
        preferenceHandler.getColorScheme().set(themeModel.uuid)
        selectEvent.value = themeModel.name
    }

    fun importTheme(inputStream: InputStream?) {
        try {
            val fileText = inputStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
            val externalTheme = gson.fromJson(fileText, ExternalTheme::class.java)
            val themeModel = ThemeConverter.toModel(externalTheme)
            loadProperties(themeModel)
        } catch (e: JsonParseException) {
            toastEvent.value = R.string.message_theme_syntax_exception
        }
    }

    fun exportTheme(themeModel: ThemeModel) {
        val externalTheme = ThemeConverter.toExternalTheme(themeModel)
        val fileName = "${themeModel.name}.json"
        val fileText = gson.toJson(externalTheme)
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileModel = FileModel(
            name = fileName,
            path = File(directory, fileName).absolutePath,
            size = 0L,
            lastModified = 0L,
            isFolder = false,
            isHidden = false
        )
        filesystem.saveFile(fileModel, fileText, Charsets.UTF_8)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onComplete = {
                    exportEvent.value = fileModel.path
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun removeTheme(themeModel: ThemeModel) {
        Completable
            .fromAction {
                appDatabase.themeDao().delete(ThemeConverter.toEntity(themeModel))
                if (preferenceHandler.getColorScheme().get() == themeModel.uuid) {
                    preferenceHandler.getColorScheme().delete()
                }
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                removeEvent.value = themeModel.name
                fetchThemes() // Update list
            }
            .disposeOnViewModelDestroy()
    }

    fun validateInput(name: String, author: String, description: String) {
        val isNameValid = name.trim().isValidFileName()
        val isAuthorValid = author.trim().isNotBlank()
        val isDescriptionValid = description.trim().isNotBlank()
        validationEvent.value = isNameValid && isAuthorValid && isDescriptionValid
    }

    fun fetchProperties(uuid: String?) {
        appDatabase.themeDao().load(uuid ?: "unknown")
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    val themeModel = ThemeConverter.toModel(it)
                    loadProperties(themeModel)
                },
                onError = {
                    val themeModel = ThemeConverter.toModel(null)
                    loadProperties(themeModel)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun createTheme(meta: Meta, properties: List<PropertyItem>) {
        for (property in properties) {
            when (property.propertyKey) {
                Property.TEXT_COLOR -> textColor = property.propertyValue
                Property.BACKGROUND_COLOR -> backgroundColor = property.propertyValue
                Property.GUTTER_COLOR -> gutterColor = property.propertyValue
                Property.GUTTER_DIVIDER_COLOR -> gutterDividerColor = property.propertyValue
                Property.GUTTER_CURRENT_LINE_NUMBER_COLOR -> gutterCurrentLineNumberColor = property.propertyValue
                Property.GUTTER_TEXT_COLOR -> gutterTextColor = property.propertyValue
                Property.SELECTED_LINE_COLOR -> selectedLineColor = property.propertyValue
                Property.SELECTION_COLOR -> selectionColor = property.propertyValue
                Property.SUGGESTION_QUERY_COLOR -> suggestionQueryColor = property.propertyValue
                Property.FIND_RESULT_BACKGROUND_COLOR -> findResultBackgroundColor = property.propertyValue
                Property.DELIMITER_BACKGROUND_COLOR -> delimiterBackgroundColor = property.propertyValue
                Property.NUMBER_COLOR -> numberColor = property.propertyValue
                Property.OPERATOR_COLOR -> operatorColor = property.propertyValue
                Property.KEYWORD_COLOR -> keywordColor = property.propertyValue
                Property.TYPE_COLOR -> typeColor = property.propertyValue
                Property.LANG_CONST_COLOR -> langConstColor = property.propertyValue
                Property.METHOD_COLOR -> methodColor = property.propertyValue
                Property.STRING_COLOR -> stringColor = property.propertyValue
                Property.COMMENT_COLOR -> commentColor = property.propertyValue
            }
        }
        Completable
            .fromAction {
                val themeEntity = ThemeEntity(
                    uuid = meta.uuid,
                    name = meta.name,
                    author = meta.author,
                    description = meta.description,
                    isExternal = meta.isExternal,
                    isPaid = meta.isPaid,
                    textColor = textColor,
                    backgroundColor = backgroundColor,
                    gutterColor = gutterColor,
                    gutterDividerColor = gutterDividerColor,
                    gutterCurrentLineNumberColor = gutterCurrentLineNumberColor,
                    gutterTextColor = gutterTextColor,
                    selectedLineColor = selectedLineColor,
                    selectionColor = selectionColor,
                    suggestionQueryColor = suggestionQueryColor,
                    findResultBackgroundColor = findResultBackgroundColor,
                    delimiterBackgroundColor = delimiterBackgroundColor,
                    numberColor = numberColor,
                    operatorColor = operatorColor,
                    keywordColor = keywordColor,
                    typeColor = typeColor,
                    langConstColor = langConstColor,
                    methodColor = methodColor,
                    stringColor = stringColor,
                    commentColor = commentColor
                )
                appDatabase.themeDao().insert(themeEntity)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                createEvent.value = meta.name
                textColor = FALLBACK_COLOR
                backgroundColor = FALLBACK_COLOR
                gutterColor = FALLBACK_COLOR
                gutterDividerColor = FALLBACK_COLOR
                gutterCurrentLineNumberColor = FALLBACK_COLOR
                gutterTextColor = FALLBACK_COLOR
                selectedLineColor = FALLBACK_COLOR
                selectionColor = FALLBACK_COLOR
                suggestionQueryColor = FALLBACK_COLOR
                findResultBackgroundColor = FALLBACK_COLOR
                delimiterBackgroundColor = FALLBACK_COLOR
                numberColor = FALLBACK_COLOR
                operatorColor = FALLBACK_COLOR
                keywordColor = FALLBACK_COLOR
                typeColor = FALLBACK_COLOR
                langConstColor = FALLBACK_COLOR
                methodColor = FALLBACK_COLOR
                stringColor = FALLBACK_COLOR
                commentColor = FALLBACK_COLOR
            }
            .disposeOnViewModelDestroy()
    }

    private fun loadProperties(themeModel: ThemeModel) {
        metaEvent.value = Meta(
            uuid = themeModel.uuid,
            name = themeModel.name,
            author = themeModel.author,
            description = themeModel.description,
            isExternal = themeModel.isExternal,
            isPaid = themeModel.isPaid
        )
        propertiesEvent.value = listOf(
            PropertyItem(
                Property.TEXT_COLOR,
                themeModel.colorScheme.textColor.toHexString(),
                R.string.theme_property_text_color
            ),
            PropertyItem(
                Property.BACKGROUND_COLOR,
                themeModel.colorScheme.backgroundColor.toHexString(),
                R.string.theme_property_background_color
            ),
            PropertyItem(
                Property.GUTTER_COLOR,
                themeModel.colorScheme.gutterColor.toHexString(),
                R.string.theme_property_gutter_color
            ),
            PropertyItem(
                Property.GUTTER_DIVIDER_COLOR,
                themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString(),
                R.string.theme_property_gutter_divider_color
            ),
            PropertyItem(
                Property.GUTTER_CURRENT_LINE_NUMBER_COLOR,
                themeModel.colorScheme.gutterCurrentLineNumberColor.toHexString(),
                R.string.theme_property_gutter_divider_current_line_number_color
            ),
            PropertyItem(
                Property.GUTTER_TEXT_COLOR,
                themeModel.colorScheme.gutterTextColor.toHexString(),
                R.string.theme_property_gutter_text_color
            ),
            PropertyItem(
                Property.SELECTED_LINE_COLOR,
                themeModel.colorScheme.selectedLineColor.toHexString(),
                R.string.theme_property_selected_line_color
            ),
            PropertyItem(
                Property.SELECTION_COLOR,
                themeModel.colorScheme.selectionColor.toHexString(),
                R.string.theme_property_selection_color
            ),
            PropertyItem(
                Property.SUGGESTION_QUERY_COLOR,
                themeModel.colorScheme.suggestionQueryColor.toHexString(),
                R.string.theme_property_suggestion_query_color
            ),
            PropertyItem(
                Property.FIND_RESULT_BACKGROUND_COLOR,
                themeModel.colorScheme.findResultBackgroundColor.toHexString(),
                R.string.theme_property_find_result_background_color
            ),
            PropertyItem(
                Property.DELIMITER_BACKGROUND_COLOR,
                themeModel.colorScheme.delimiterBackgroundColor.toHexString(),
                R.string.theme_property_delimiter_background_color
            ),
            PropertyItem(
                Property.NUMBER_COLOR,
                themeModel.colorScheme.numberColor.toHexString(),
                R.string.theme_property_numbers_color
            ),
            PropertyItem(
                Property.OPERATOR_COLOR,
                themeModel.colorScheme.operatorColor.toHexString(),
                R.string.theme_property_operators_color
            ),
            PropertyItem(
                Property.KEYWORD_COLOR,
                themeModel.colorScheme.keywordColor.toHexString(),
                R.string.theme_property_keywords_color
            ),
            PropertyItem(
                Property.TYPE_COLOR,
                themeModel.colorScheme.typeColor.toHexString(),
                R.string.theme_property_types_color
            ),
            PropertyItem(
                Property.LANG_CONST_COLOR,
                themeModel.colorScheme.langConstColor.toHexString(),
                R.string.theme_property_lang_const_color
            ),
            PropertyItem(
                Property.METHOD_COLOR,
                themeModel.colorScheme.methodColor.toHexString(),
                R.string.theme_property_methods_color
            ),
            PropertyItem(
                Property.STRING_COLOR,
                themeModel.colorScheme.stringColor.toHexString(),
                R.string.theme_property_strings_color
            ),
            PropertyItem(
                Property.COMMENT_COLOR,
                themeModel.colorScheme.commentColor.toHexString(),
                R.string.theme_property_comments_color
            )
        )
    }
}