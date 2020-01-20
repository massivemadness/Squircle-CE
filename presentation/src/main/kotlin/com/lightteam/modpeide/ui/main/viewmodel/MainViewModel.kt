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

package com.lightteam.modpeide.ui.main.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.converter.FileConverter
import com.lightteam.modpeide.data.parser.ScriptEngine
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.collection.UndoStack
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.model.AnalysisModel
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.model.PropertiesModel
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.ui.main.adapters.DocumentAdapter
import com.lightteam.modpeide.ui.main.customview.TextProcessor
import com.lightteam.modpeide.utils.commons.VersionChecker
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import com.lightteam.modpeide.utils.theming.ThemeFactory
import io.reactivex.Completable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val fileRepository: FileRepository,
    private val database: AppDatabase,
    private val preferenceHandler: PreferenceHandler,
    private val cacheHandler: CacheHandler,
    private val documentAdapter: DocumentAdapter,
    private val versionChecker: VersionChecker
) : BaseViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    // region UI

    val documentLoadingIndicator: ObservableBoolean = ObservableBoolean(true) //Индикатор загрузки документа
    val noDocumentsIndicator: ObservableBoolean = ObservableBoolean(false) //Сообщение что нет документов

    val canUndo: ObservableBoolean = ObservableBoolean(false) //Кликабельность кнопки Undo
    val canRedo: ObservableBoolean = ObservableBoolean(false) //Кликабельность кнопки Redo

    // endregion UI

    // region EVENTS

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений

    val documentAllTabsEvent: SingleLiveEvent<List<DocumentModel>> = SingleLiveEvent() //Загрузка всех кешированных документов
    val documentTabEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Добавление документа во вкладки
    val documentTextEvent: SingleLiveEvent<String> = SingleLiveEvent() //Чтение текста файла
    val documentLoadedEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Для загрузки скроллинга/выделения
    val documentStacksEvent: SingleLiveEvent<Pair<UndoStack, UndoStack>> = SingleLiveEvent() //Для загрузки Undo/Redo

    val propertiesEvent: SingleLiveEvent<PropertiesModel> = SingleLiveEvent() //Свойства файла
    val analysisEvent: SingleLiveEvent<AnalysisModel> = SingleLiveEvent() //Анализ кода

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

    // region FILE_REPOSITORY

    fun propertiesOf(documentModel: DocumentModel) = propertiesOf(FileConverter.toModel(documentModel))
    fun propertiesOf(fileModel: FileModel) {
        fileRepository.propertiesOf(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    propertiesEvent.value = it
                },
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun loadFile(documentModel: DocumentModel) {
        documentLoadingIndicator.set(true)

        val dataSource = if(cacheHandler.isCached(documentModel)) {
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
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { triple ->
                    documentTextEvent.value = triple.first //Text
                    documentLoadedEvent.value = documentModel //Selection, scroll position
                    documentStacksEvent.value = triple.second to triple.third //Undo & Redo stacks
                    documentLoadingIndicator.set(false)
                },
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
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
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    // endregion FILE_REPOSITORY

    // region EDITOR

    fun analyze(sourceName: String, sourceCode: String) {
        ScriptEngine.analyze(sourceName, sourceCode)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { analysisEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun loadAllFiles() {
        documentLoadingIndicator.set(true)
        database.documentDao().loadAll()
            .map { it.map(DocumentConverter::toModel) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { list ->
                    if(resumeSessionEvent.value!!) {
                        documentAllTabsEvent.value = list
                        noDocumentsIndicator.set(list.isEmpty())
                    } else {
                        database.documentDao().deleteAll()
                            .schedulersIoToMain(schedulersProvider)
                            .subscribeBy {
                                cacheHandler.deleteAllCaches()
                            }
                            .disposeOnViewModelDestroy()
                        noDocumentsIndicator.set(documentAdapter.isEmpty())
                    }
                    documentLoadingIndicator.set(false)
                },
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun loadDocument(index: Int) {
        val document = documentAdapter.get(index)
        document?.let {
            loadFile(it)
        }
    }

    fun saveToCache(documentModel: DocumentModel, text: String) {
        updateDocument(documentModel)
        documentAdapter.add(documentModel)
        cacheHandler.saveToCache(documentModel, text)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveUndoStack(documentModel: DocumentModel, undoStack: UndoStack) {
        cacheHandler.saveUndoStack(documentModel, undoStack)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveRedoStack(documentModel: DocumentModel, redoStack: UndoStack) {
        cacheHandler.saveRedoStack(documentModel, redoStack)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun getDocument(position: Int): DocumentModel? = documentAdapter.get(position)

    fun addDocument(documentModel: DocumentModel): Int {
        val index = documentAdapter.indexOf(documentModel)
        return if(index == -1) {
            documentAdapter.add(documentModel)
            noDocumentsIndicator.set(documentAdapter.isEmpty())
            -1
        } else {
            index
        }
    }

    fun removeDocument(index: Int): Int {
        val documentModel = documentAdapter.get(index)
        documentModel?.let {
            deleteDocument(documentModel)
            documentAdapter.removeAt(index)
            cacheHandler.deleteCache(documentModel) // Delete from Cache
                .schedulersIoToMain(schedulersProvider)
                .subscribeBy(
                    onError = {
                        toastEvent.value = R.string.message_error
                        Log.e(TAG, it.message, it)
                    }
                )
            noDocumentsIndicator.set(documentAdapter.isEmpty())
        }
        return index
    }

    private fun updateDocument(documentModel: DocumentModel) {
        Completable
            .fromAction {
                database.documentDao().update(DocumentConverter.toCache(documentModel)) // Save to Database
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy()
            .disposeOnViewModelDestroy()
    }

    private fun deleteDocument(documentModel: DocumentModel) {
        Completable
            .fromAction {
                database.documentDao().delete(DocumentConverter.toCache(documentModel)) // Delete from Database
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy()
            .disposeOnViewModelDestroy()
    }

    // endregion EDITOR

    // region PREFERENCES

    fun isUltimate(): Boolean = versionChecker.isUltimate

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
            .subscribeBy { resumeSessionEvent.value = it }
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