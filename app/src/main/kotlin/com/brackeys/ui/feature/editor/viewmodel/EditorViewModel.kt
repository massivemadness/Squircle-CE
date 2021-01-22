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
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.brackeys.ui.R
import com.brackeys.ui.data.converter.DocumentConverter
import com.brackeys.ui.data.converter.ThemeConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.repository.CacheRepository
import com.brackeys.ui.data.repository.LocalRepository
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

    // region UI

    val stateLoadingDocuments: ObservableBoolean = ObservableBoolean(false) // Индикатор загрузки документа
    val stateNothingFound: ObservableBoolean = ObservableBoolean(false) // Сообщение об отсутствии документов

    // endregion UI

    // region EVENTS

    val loadFilesEvent: MutableLiveData<List<DocumentModel>> = MutableLiveData() // Загрузка недавних файлов
    val selectTabEvent: MutableLiveData<Int> = MutableLiveData() // Текущая позиция выбранной вкладки

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() // Отображение сообщений
    val parseEvent: SingleLiveEvent<ParseResult> = SingleLiveEvent() // Проверка ошибок
    val contentEvent: SingleLiveEvent<Pair<DocumentContent, PrecomputedTextCompat>> = SingleLiveEvent() // Контент загруженного файла
    val settingsEvent: EventsQueue<SettingsEvent<*>> = EventsQueue() // События с измененными настройками

    // endregion EVENTS

    val openUnknownFiles: Boolean
        get() = settingsManager.getOpenUnknownFiles().get()
    private var selectedDocumentId: String
        get() = settingsManager.getSelectedDocumentId().get()
        set(value) = settingsManager.getSelectedDocumentId().set(value)

    fun loadFiles() {
        appDatabase.documentDao().loadAll()
            .doOnSubscribe { stateLoadingDocuments.set(true) }
            .map { it.map(DocumentConverter::toModel) }
            .doOnSuccess { stateLoadingDocuments.set(false) }
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

    fun openFile(list: List<DocumentModel>, documentModel: DocumentModel) {
        if (!list.containsDocumentModel(documentModel)) {
            if (list.size < TAB_LIMIT) {
                selectedDocumentId = documentModel.uuid
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
            list.indexBy(selectedDocumentId) ?: 0
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

    fun observeSettings() {
        settingsManager.getColorScheme()
            .asObservable()
            .flatMapSingle {
                InternalTheme.fetchTheme(it)?.let { themeModel ->
                    Single.just(themeModel)
                } ?: run {
                    appDatabase.themeDao().load(it)
                        .map(ThemeConverter::toModel)
                        .schedulersIoToMain(schedulersProvider)
                }
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.ThemePref(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getFontSize()
            .asObservable()
            .map { it.toFloat() }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.FontSize(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getFontType()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.FontType(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getWordWrap()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.WordWrap(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getCodeCompletion()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.CodeCompletion(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getErrorHighlighting()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.ErrorHighlight(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getPinchZoom()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.PinchZoom(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getHighlightCurrentLine()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.CurrentLine(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getHighlightMatchingDelimiters()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.Delimiters(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getExtendedKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.ExtendedKeys(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getKeyboardPreset()
            .asObservable()
            .map { it.toCharArray().map(Char::toString) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.KeyboardPreset(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getSoftKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.SoftKeys(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getAutoIndentation()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.AutoIndent(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getAutoCloseBrackets()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.AutoBrackets(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getAutoCloseQuotes()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.AutoQuotes(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getUseSpacesNotTabs()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.UseSpacesNotTabs(it)) }
            .disposeOnViewModelDestroy()

        settingsManager.getTabWidth()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { settingsEvent.offer(SettingsEvent.TabWidth(it)) }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES
}