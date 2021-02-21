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

package com.brackeys.ui.feature.editor.viewmodel

import android.util.Log
import androidx.core.text.PrecomputedTextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brackeys.ui.R
import com.brackeys.ui.data.converter.DocumentConverter
import com.brackeys.ui.data.converter.ThemeConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.repository.documents.CacheRepository
import com.brackeys.ui.data.repository.documents.LocalRepository
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.InternalTheme
import com.brackeys.ui.data.utils.containsDocumentModel
import com.brackeys.ui.data.utils.indexBy
import com.brackeys.ui.data.utils.schedulersIoToMain
import com.brackeys.ui.domain.model.editor.DocumentContent
import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.domain.providers.rx.SchedulersProvider
import com.brackeys.ui.feature.base.viewmodel.BaseViewModel
import com.brackeys.ui.filesystem.base.exception.FileNotFoundException
import com.brackeys.ui.language.base.Language
import com.brackeys.ui.language.base.model.ParseResult
import com.brackeys.ui.utils.event.EventsQueue
import com.brackeys.ui.utils.event.SettingsEvent
import com.brackeys.ui.utils.event.SingleLiveEvent
import com.github.gzuliyujiang.chardet.CJKCharsetDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val localRepository: LocalRepository,
    private val cacheRepository: CacheRepository
) : BaseViewModel() {

    companion object {
        private const val TAG = "EditorViewModel"
        private const val TAB_LIMIT = 10
    }

    // region EVENTS

    val loadFilesEvent = MutableLiveData<List<DocumentModel>>() // Загрузка недавних файлов
    val selectTabEvent = MutableLiveData<Int>() // Текущая позиция выбранной вкладки
    val stateLoadingDocuments = MutableLiveData(false) // Индикатор загрузки документа
    val stateNothingFound = MutableLiveData(false) // Сообщение об отсутствии документов

    val toastEvent = SingleLiveEvent<Int>() // Отображение сообщений
    val parseEvent = SingleLiveEvent<ParseResult>() // Проверка ошибок
    val contentEvent = SingleLiveEvent<Pair<DocumentContent, PrecomputedTextCompat>>() // Контент загруженного файла
    val settingsEvent = EventsQueue<SettingsEvent<*>>() // События с измененными настройками

    // endregion EVENTS

    val openUnknownFiles: Boolean
        get() = settingsManager.openUnknownFiles
    val autoSaveFiles: Boolean
        get() = settingsManager.autoSaveFiles

    fun loadFiles() {
        appDatabase.documentDao().loadAll()
            .doOnSubscribe { stateLoadingDocuments.postValue(true) }
            .map { it.map(DocumentConverter::toModel) }
            .doOnSuccess { stateLoadingDocuments.postValue(false) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    loadFilesEvent.value = it
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun loadFile(documentModel: DocumentModel, params: PrecomputedTextCompat.Params) {
        val dataSource = if (cacheRepository.isCached(documentModel)) {
            cacheRepository
        } else localRepository

        dataSource.loadFile(documentModel)
            .doOnSubscribe { stateLoadingDocuments.postValue(true) }
            .map { it to PrecomputedTextCompat.create(it.text, params) }
            .doFinally { stateLoadingDocuments.postValue(false) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { (documentContent, precomputedText) ->
                    settingsManager.selectedDocumentId = documentContent.documentModel.uuid
                    contentEvent.value = documentContent to precomputedText
                    if (CJKCharsetDetector.inWrongEncoding(documentContent.text)) {
                        toastEvent.value = R.string.message_wrong_encoding
                    }
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    when (it) {
                        is FileNotFoundException -> {
                            toastEvent.value = R.string.message_file_not_found
                        }
                        is OutOfMemoryError -> {
                            toastEvent.value = R.string.message_out_of_memory
                        }
                        else -> {
                            toastEvent.value = R.string.message_unknown_exception
                        }
                    }
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveFile(documentContent: DocumentContent, toCache: Boolean = false) {
        val dataSource = if (toCache) {
            cacheRepository
        } else localRepository

        dataSource.saveFile(documentContent)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onComplete = {
                    if (!toCache) { toastEvent.value = R.string.message_saved }
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun deleteCache(documentModel: DocumentModel) {
        cacheRepository.deleteCache(documentModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun openFile(list: List<DocumentModel>, documentModel: DocumentModel) {
        if (!list.containsDocumentModel(documentModel)) {
            if (list.size < TAB_LIMIT) {
                settingsManager.selectedDocumentId = documentModel.uuid
                loadFilesEvent.value = list.toMutableList().apply { add(documentModel) }
            } else {
                toastEvent.value = R.string.message_tab_limit_achieved
            }
        } else {
            selectTabEvent.value = list.indexBy(documentModel)
        }
    }

    fun parse(documentModel: DocumentModel, language: Language?, sourceCode: String) {
        Single
            .create<ParseResult> { emitter ->
                val parser = language?.getParser()
                val parseResult = parser?.execute(documentModel.name, sourceCode)
                parseResult?.let(emitter::onSuccess)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parseEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun findRecentTab(list: List<DocumentModel>) {
        selectTabEvent.value = if (list.isNotEmpty()) {
            list.indexBy(settingsManager.selectedDocumentId) ?: 0
        } else -1
    }

    fun updateDocuments(list: List<DocumentModel>) {
        Completable
            .fromCallable {
                list.forEachIndexed { index, documentModel ->
                    documentModel.position = index
                    val documentEntity = DocumentConverter.toEntity(documentModel)
                    appDatabase.documentDao().update(documentEntity)
                }
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy()
            .disposeOnViewModelDestroy()
    }

    // region PREFERENCES

    fun fetchSettings() {
        viewModelScope.launch {
            val value = settingsManager.colorScheme
            val theme = InternalTheme.getTheme(value) ?: run {
                val themeEntity = appDatabase.themeDao().load(value)
                ThemeConverter.toModel(themeEntity)
            }
            settingsEvent.offer(SettingsEvent.ThemePref(theme))
        }

        val fontSize = settingsManager.fontSize.toFloat()
        settingsEvent.offer(SettingsEvent.FontSize(fontSize))

        val fontType = settingsManager.fontType
        settingsEvent.offer(SettingsEvent.FontType(fontType))

        val wordWrap = settingsManager.wordWrap
        settingsEvent.offer(SettingsEvent.WordWrap(wordWrap))

        val codeCompletion = settingsManager.codeCompletion
        settingsEvent.offer(SettingsEvent.CodeCompletion(codeCompletion))

        val errorHighlighting = settingsManager.errorHighlighting
        settingsEvent.offer(SettingsEvent.ErrorHighlight(errorHighlighting))

        val pinchZoom = settingsManager.pinchZoom
        settingsEvent.offer(SettingsEvent.PinchZoom(pinchZoom))

        val highlightCurrentLine = settingsManager.highlightCurrentLine
        settingsEvent.offer(SettingsEvent.CurrentLine(highlightCurrentLine))

        val highlightMatchingDelimiters = settingsManager.highlightMatchingDelimiters
        settingsEvent.offer(SettingsEvent.Delimiters(highlightMatchingDelimiters))

        val extendedKeyboard = settingsManager.extendedKeyboard
        settingsEvent.offer(SettingsEvent.ExtendedKeys(extendedKeyboard))

        val keyboardPreset = settingsManager.keyboardPreset.toCharArray().map(Char::toString)
        settingsEvent.offer(SettingsEvent.KeyboardPreset(keyboardPreset))

        val softKeyboard = settingsManager.softKeyboard
        settingsEvent.offer(SettingsEvent.SoftKeys(softKeyboard))

        val autoIndentation = settingsManager.autoIndentation
        settingsEvent.offer(SettingsEvent.AutoIndent(autoIndentation))

        val autoCloseBrackets = settingsManager.autoCloseBrackets
        settingsEvent.offer(SettingsEvent.AutoBrackets(autoCloseBrackets))

        val autoCloseQuotes = settingsManager.autoCloseQuotes
        settingsEvent.offer(SettingsEvent.AutoQuotes(autoCloseQuotes))

        val useSpacesInsteadOfTabs = settingsManager.useSpacesInsteadOfTabs
        settingsEvent.offer(SettingsEvent.UseSpacesNotTabs(useSpacesInsteadOfTabs))

        val tabWidth = settingsManager.tabWidth
        settingsEvent.offer(SettingsEvent.TabWidth(tabWidth))
    }

    // endregion PREFERENCES
}