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
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.collection.UndoStack
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.exception.FileNotFoundException
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.ui.editor.customview.TextProcessor
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import com.lightteam.modpeide.utils.theming.ThemeFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy

class EditorViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val fileRepository: FileRepository,
    private val cacheHandler: CacheHandler,
    private val appDatabase: AppDatabase,
    private val preferenceHandler: PreferenceHandler
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

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений
    val documentsEvent: SingleLiveEvent<List<DocumentModel>> = SingleLiveEvent() //Список документов
    val documentEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Получение документа из проводника

    val textEvent: SingleLiveEvent<String> = SingleLiveEvent() //Контент загруженного файла
    val loadedEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Для загрузки скроллинга/выделения
    val stacksEvent: SingleLiveEvent<Pair<UndoStack, UndoStack>> = SingleLiveEvent() //Для загрузки Undo/Redo

    // endregion EVENTS

    // region PREFERENCES

    val themeEvent: SingleLiveEvent<TextProcessor.Theme> = SingleLiveEvent() //Тема редактора
    val fullscreenEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Полноэкранный режим
    val backEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подтверждение выхода

    val fontSizeEvent: SingleLiveEvent<Float> = SingleLiveEvent() //Размер шрифта
    val fontTypeEvent: SingleLiveEvent<String> = SingleLiveEvent() //Тип шрифта

    val resumeSessionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Повторное открытие вкладок после выхода
    val tabLimitEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Лимит вкладок

    val wordWrapEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Смещать текст на новую строку если нет места
    val codeCompletionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Автодополнение кода
    val pinchZoomEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Жест масштабирования текста
    val highlightLineEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подсветка текущей строки
    val highlightDelimitersEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подсветка ближайших скобок

    val extendedKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Отображать доп. символы
    val softKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Упрощенная клавиатура (landscape orientation)

    val autoIndentationEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Отступы при переходе на новую строку
    val autoCloseBracketsEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Автоматическое закрытие скобок
    val autoCloseQuotesEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Автоматическое закрытие кавычек

    // endregion PREFERENCES

    fun loadFiles() {
        if (resumeSessionEvent.value!!) {
            appDatabase.documentDao().loadAll()
                .doOnSubscribe { stateLoadingDocuments.set(true) }
                .doOnSuccess {
                    stateLoadingDocuments.set(false)
                    stateNothingFound.set(it.isEmpty())
                }
                .map { it.map(DocumentConverter::toModel) }
                .schedulersIoToMain(schedulersProvider)
                .subscribeBy(
                    onSuccess = {
                        documentsEvent.value = it
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
                .subscribeBy {
                    cacheHandler.deleteAllCaches()
                }
                .disposeOnViewModelDestroy()
        }
    }

    fun loadFile(documentModel: DocumentModel) {
        val dataSource = if (cacheHandler.isCached(documentModel)) {
            cacheHandler.loadFromCache(documentModel)
        } else {
            fileRepository.loadFile(documentModel)
        }
        Singles
            .zip(
                dataSource,
                cacheHandler.loadUndoStack(documentModel),
                cacheHandler.loadRedoStack(documentModel)
            )
            .doOnSubscribe { stateLoadingDocuments.set(true) }
            .doOnSuccess { stateLoadingDocuments.set(false) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { triple ->
                    textEvent.value = triple.first //Text
                    loadedEvent.value = documentModel //Selection, scroll position
                    stacksEvent.value = triple.second to triple.third //Undo & Redo stacks
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
        deleteDocument(documentModel)
        cacheHandler.deleteCache(documentModel)
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
        updateDocument(documentModel)
        cacheHandler.saveToCache(documentModel, text)
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
        cacheHandler.saveUndoStack(documentModel, undoStack)
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
        cacheHandler.saveRedoStack(documentModel, redoStack)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    Log.e(TAG, it.message, it)
                    toastEvent.value = R.string.message_unknown_exception
                }
            )
            .disposeOnViewModelDestroy()
    }

    private fun updateDocument(documentModel: DocumentModel) {
        Completable
            .fromAction {
                appDatabase.documentDao().update(DocumentConverter.toEntity(documentModel)) // Save to Database
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy()
            .disposeOnViewModelDestroy()
    }

    private fun deleteDocument(documentModel: DocumentModel) {
        Completable
            .fromAction {
                appDatabase.documentDao().delete(DocumentConverter.toEntity(documentModel)) // Delete from Database
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy()
            .disposeOnViewModelDestroy()
    }

    // region PREFERENCES

    fun observePreferences() {

        //Theme
        preferenceHandler.getTheme()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { themeEvent.value = ThemeFactory.create(it) }
            .disposeOnViewModelDestroy()

        //Fullscreen Mode
        preferenceHandler.getFullscreenMode()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fullscreenEvent.value = it }
            .disposeOnViewModelDestroy()

        //Confirm Exit
        preferenceHandler.getConfirmExit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { backEvent.value = it }
            .disposeOnViewModelDestroy()

        //Font Size
        preferenceHandler.getFontSize()
            .asObservable()
            .map { it.toFloat() }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fontSizeEvent.value = it }
            .disposeOnViewModelDestroy()

        //Font Type
        preferenceHandler.getFontType()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fontTypeEvent.value = it }
            .disposeOnViewModelDestroy()

        //Resume Session
        preferenceHandler.getResumeSession()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy {
                resumeSessionEvent.value = it
                if (documentsEvent.value == null) {
                    loadFiles()
                }
            }
            .disposeOnViewModelDestroy()

        //Tab Limit
        preferenceHandler.getTabLimit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { tabLimitEvent.value = it }
            .disposeOnViewModelDestroy()

        //Word Wrap
        preferenceHandler.getWordWrap()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { wordWrapEvent.value = it }
            .disposeOnViewModelDestroy()

        //Code Completion
        preferenceHandler.getCodeCompletion()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { codeCompletionEvent.value = it }
            .disposeOnViewModelDestroy()

        //Pinch Zoom
        preferenceHandler.getPinchZoom()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { pinchZoomEvent.value = it }
            .disposeOnViewModelDestroy()

        //Highlight Current Line
        preferenceHandler.getHighlightCurrentLine()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { highlightLineEvent.value = it }
            .disposeOnViewModelDestroy()

        //Highlight Matching Delimiters
        preferenceHandler.getHighlightMatchingDelimiters()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { highlightDelimitersEvent.value = it }
            .disposeOnViewModelDestroy()

        //Extended Keyboard
        preferenceHandler.getExtendedKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { extendedKeyboardEvent.value = it }
            .disposeOnViewModelDestroy()

        //Soft Keyboard
        preferenceHandler.getSoftKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { softKeyboardEvent.value = it }
            .disposeOnViewModelDestroy()

        //Auto Indentation
        preferenceHandler.getAutoIndentation()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { autoIndentationEvent.value = it }
            .disposeOnViewModelDestroy()

        //Auto-close Brackets
        preferenceHandler.getAutoCloseBrackets()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { autoCloseBracketsEvent.value = it }
            .disposeOnViewModelDestroy()

        //Auto-close Quotes
        preferenceHandler.getAutoCloseQuotes()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { autoCloseQuotesEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES
}