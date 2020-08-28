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

package com.lightteam.modpeide.ui.editor.viewmodel

import android.util.Log
import androidx.core.text.PrecomputedTextCompat
import androidx.databinding.ObservableBoolean
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.github.gzuliyujiang.chardet.CJKCharsetDetector
import com.lightteam.filesystem.exception.FileNotFoundException
import com.lightteam.language.language.Language
import com.lightteam.language.model.ParseModel
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.converter.PresetConverter
import com.lightteam.modpeide.data.converter.ThemeConverter
import com.lightteam.modpeide.data.repository.CacheRepository
import com.lightteam.modpeide.data.repository.LocalRepository
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.*
import com.lightteam.modpeide.database.AppDatabase
import com.lightteam.modpeide.domain.model.editor.DocumentContent
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.EventsQueue
import com.lightteam.modpeide.utils.event.PreferenceEvent
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy

class EditorViewModel @ViewModelInject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val appDatabase: AppDatabase,
    private val localRepository: LocalRepository,
    private val cacheRepository: CacheRepository
) : BaseViewModel() {

    companion object {
        private const val TAG = "EditorViewModel"
        private const val TAB_LIMIT = 10
    }

    // region UI

    val stateLoadingDocuments: ObservableBoolean = ObservableBoolean(false) // Индикатор загрузки документа
    val stateNothingFound: ObservableBoolean = ObservableBoolean(false) // Сообщение об отсутствии документов

    // endregion UI

    // region EVENTS

    val loadFilesEvent: MutableLiveData<List<DocumentModel>> = MutableLiveData() // Загрузка недавних файлов
    val selectTabEvent: MutableLiveData<Int> = MutableLiveData() // Текущая позиция выбранной вкладки

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() // Отображение сообщений
    val parseEvent: SingleLiveEvent<ParseModel> = SingleLiveEvent() // Проверка ошибок
    val contentEvent: SingleLiveEvent<Pair<DocumentContent, PrecomputedTextCompat>> = SingleLiveEvent() // Контент загруженного файла
    val preferenceEvent: EventsQueue<PreferenceEvent<*>> = EventsQueue() // События с измененными настройками

    // endregion EVENTS

    val tabsList: MutableList<DocumentModel> = mutableListOf()

    val openUnknownFiles: Boolean
        get() = preferenceHandler.getOpenUnknownFiles().get()
    private var selectedDocumentId: String
        get() = preferenceHandler.getSelectedDocumentId().get()
        set(value) = preferenceHandler.getSelectedDocumentId().set(value)

    fun loadFiles() {
        appDatabase.documentDao().loadAll()
            .doOnSubscribe { stateLoadingDocuments.set(true) }
            .doOnSuccess { stateLoadingDocuments.set(false) }
            .map { it.map(DocumentConverter::toModel) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { list ->
                    tabsList.replaceList(list)
                    loadFilesEvent.value = tabsList
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
            .doOnSubscribe { stateLoadingDocuments.set(true) }
            .map { it to PrecomputedTextCompat.create(it.text, params) }
            .doFinally { stateLoadingDocuments.set(false) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { (documentContent, precomputedText) ->
                    selectedDocumentId = documentContent.documentModel.uuid
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

    fun openFile(documentModel: DocumentModel) {
        if (!tabsList.containsDocumentModel(documentModel)) {
            if (tabsList.size < TAB_LIMIT) {
                tabsList.add(documentModel)
                selectedDocumentId = documentModel.uuid
                loadFilesEvent.value = tabsList
            } else {
                toastEvent.value = R.string.message_tab_limit_achieved
            }
        } else {
            selectTabEvent.value = tabsList.indexBy(documentModel)
        }
    }

    fun parse(language: Language, position: Int, sourceCode: String) {
        language.getParser()
            .execute(tabsList[position].name, sourceCode)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parseEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun fetchRecentTab() {
        selectTabEvent.value = if (tabsList.isNotEmpty()) {
            tabsList.indexBy(selectedDocumentId) ?: 0
        } else -1
    }

    fun updatePositions() {
        Completable
            .fromCallable {
                tabsList.forEachIndexed { index, documentModel ->
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

    fun observePreferences() {
        preferenceHandler.getColorScheme()
            .asObservable()
            .flatMapSingle {
                appDatabase.themeDao().load(it)
                    .schedulersIoToMain(schedulersProvider)
            }
            .map(ThemeConverter::toModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.ThemePref(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getFontSize()
            .asObservable()
            .map { it.toFloat() }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.FontSize(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getFontType()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.FontType(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getWordWrap()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.WordWrap(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getCodeCompletion()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.CodeCompletion(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getErrorHighlighting()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.ErrorHighlight(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getPinchZoom()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.PinchZoom(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getHighlightCurrentLine()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.CurrentLine(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getHighlightMatchingDelimiters()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.Delimiters(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getExtendedKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.ExtendedKeys(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getKeyboardPreset()
            .asObservable()
            .flatMapSingle {
                appDatabase.presetDao().load(it)
                    .schedulersIoToMain(schedulersProvider)
            }
            .map(PresetConverter::toModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.KeyboardPreset(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getSoftKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.SoftKeys(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getAutoIndentation()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.AutoIndent(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getAutoCloseBrackets()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.AutoBrackets(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getAutoCloseQuotes()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.AutoQuotes(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getUseSpacesNotTabs()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.UseSpacesNotTabs(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getTabWidth()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.TabWidth(it)) }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES
}