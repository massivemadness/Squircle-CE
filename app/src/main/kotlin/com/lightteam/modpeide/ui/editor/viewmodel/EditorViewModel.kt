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
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lightteam.language.language.Language
import com.lightteam.language.model.ParseModel
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.repository.CacheRepository
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.*
import com.lightteam.filesystem.exception.FileNotFoundException
import com.lightteam.modpeide.data.converter.ThemeConverter
import com.lightteam.modpeide.data.repository.FileRepository
import com.lightteam.modpeide.domain.editor.DocumentContent
import com.lightteam.modpeide.domain.editor.DocumentModel
import com.lightteam.modpeide.domain.feature.undoredo.UndoStack
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.EventsQueue
import com.lightteam.modpeide.utils.event.PreferenceEvent
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy

class EditorViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val appDatabase: AppDatabase,
    private val fileRepository: FileRepository,
    private val cacheRepository: CacheRepository
) : BaseViewModel() {

    companion object {
        private const val TAG = "EditorViewModel"
    }

    // region UI

    val stateLoadingDocuments: ObservableBoolean = ObservableBoolean(false) //Индикатор загрузки документа
    val stateNothingFound: ObservableBoolean = ObservableBoolean(false) //Сообщение что нет документов

    val canUndo: ObservableBoolean = ObservableBoolean(false) //Кликабельность кнопки Undo
    val canRedo: ObservableBoolean = ObservableBoolean(false) //Кликабельность кнопки Redo

    // endregion UI

    // region EVENTS

    val tabsEvent: MutableLiveData<MutableList<DocumentModel>> = MutableLiveData() //Обновление списка вкладок
    val tabSelectionEvent: MutableLiveData<Int> = MutableLiveData() //Текущая позиция выбранной вкладки

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений
    val parseEvent: SingleLiveEvent<ParseModel> = SingleLiveEvent() //Проверка ошибок
    val contentEvent: SingleLiveEvent<DocumentContent> = SingleLiveEvent() //Контент загруженного файла
    val preferenceEvent: EventsQueue<PreferenceEvent<*>> = EventsQueue() //События с измененными настройками

    // endregion EVENTS

    // region PREFERENCES

    private val resumeSessionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val tabLimitEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    // endregion PREFERENCES

    val tabsList: MutableList<DocumentModel> = mutableListOf()

    private fun loadFiles() {
        if (resumeSessionEvent.value!!) { // must receive value before calling
            appDatabase.documentDao().loadAll()
                .doOnSubscribe { stateLoadingDocuments.set(true) }
                .doOnSuccess {
                    stateLoadingDocuments.set(false)
                    stateNothingFound.set(it.isEmpty())
                }
                .map { it.map(DocumentConverter::toModel) }
                .schedulersIoToMain(schedulersProvider)
                .subscribeBy(
                    onSuccess = { list ->
                        tabsList.replaceList(list)
                        tabsEvent.value = tabsList
                        tabSelectionEvent.value = if (list.isNotEmpty()) {
                            tabsList.indexBy(getSelectedDocumentId()) ?: 0
                        } else {
                            -1
                        }
                    },
                    onError = {
                        Log.e(TAG, it.message, it)
                        toastEvent.value = R.string.message_unknown_exception
                    }
                )
                .disposeOnViewModelDestroy()
        } else {
            appDatabase.documentDao().deleteAll()
                .doOnSubscribe { stateLoadingDocuments.set(true) }
                .doOnComplete {
                    stateLoadingDocuments.set(false)
                    stateNothingFound.set(true)
                }
                .schedulersIoToMain(schedulersProvider)
                .subscribeBy { cacheRepository.deleteAllCaches() }
                .disposeOnViewModelDestroy()
        }
    }

    fun loadFile(documentModel: DocumentModel) {
        val dataSource = if (cacheRepository.isCached(documentModel)) {
            cacheRepository
        } else {
            fileRepository
        }
        dataSource.loadFile(documentModel)
            .doOnSubscribe { stateLoadingDocuments.set(true) }
            .doOnSuccess { stateLoadingDocuments.set(false) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    contentEvent.value = it
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    when (it) {
                        is FileNotFoundException -> {
                            toastEvent.value = R.string.message_file_not_found
                        }
                        else -> {
                            toastEvent.value = R.string.message_unknown_exception
                        }
                    }
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveFile(documentModel: DocumentModel, text: String) {
        fileRepository.saveFile(documentModel, text)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onComplete = {
                    toastEvent.value = R.string.message_saved
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

    fun saveToCache(documentModel: DocumentModel, text: String) {
        cacheRepository.saveFile(documentModel, text)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveUndoStack(documentModel: DocumentModel, undoStack: UndoStack) {
        cacheRepository.saveUndoStack(documentModel, undoStack)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveRedoStack(documentModel: DocumentModel, redoStack: UndoStack) {
        cacheRepository.saveRedoStack(documentModel, redoStack)
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
            if (tabsList.size < tabLimitEvent.value!!) {
                tabsList.add(documentModel)
                stateNothingFound.set(tabsList.isEmpty())
                tabsEvent.value = tabsList
                tabSelectionEvent.value = tabsList.size - 1
            } else {
                toastEvent.value = R.string.message_tab_limit_achieved
            }
        } else {
            tabSelectionEvent.value = tabsList.indexBy(documentModel)
        }
    }

    fun parse(language: Language, position: Int, sourceCode: String) {
        language.getParser()
            .execute(tabsList[position].name, sourceCode)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parseEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    // region PREFERENCES

    fun getSelectedDocumentId(): String {
        return preferenceHandler.getSelectedDocumentId().get()
    }

    fun setSelectedDocumentId(uuid: String) {
        preferenceHandler.getSelectedDocumentId().set(uuid)
    }

    fun observePreferences() {
        preferenceHandler.getTheme()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                appDatabase.themeDao().load(it)
                    .map(ThemeConverter::toModel)
                    .schedulersIoToMain(schedulersProvider)
                    .subscribeBy { theme ->
                        preferenceEvent.offer(PreferenceEvent.ThemePref(theme))
                    }
                    .disposeOnViewModelDestroy()
            }
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

        preferenceHandler.getResumeSession()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                resumeSessionEvent.value = it
                if (tabsEvent.value == null) {
                    loadFiles()
                }
            }
            .disposeOnViewModelDestroy()

        preferenceHandler.getTabLimit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { tabLimitEvent.value = it }
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
    }

    // endregion PREFERENCES

    class Factory(
        private val schedulersProvider: SchedulersProvider,
        private val preferenceHandler: PreferenceHandler,
        private val appDatabase: AppDatabase,
        private val fileRepository: FileRepository,
        private val cacheRepository: CacheRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when {
                modelClass === EditorViewModel::class.java ->
                    EditorViewModel(
                        schedulersProvider,
                        preferenceHandler,
                        appDatabase,
                        fileRepository,
                        cacheRepository
                    ) as T
                else -> null as T
            }
        }
    }
}