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

package com.lightteam.modpeide.presentation.main.viewmodel

import androidx.appcompat.widget.SearchView
import androidx.databinding.ObservableBoolean
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.converter.FileConverter
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.model.PropertiesModel
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.presentation.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.presentation.main.customview.TextProcessor
import com.lightteam.modpeide.utils.commons.ThemeFactory
import com.lightteam.modpeide.utils.commons.VersionChecker
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val fileRepository: FileRepository,
    private val database: AppDatabase,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val cacheHandler: CacheHandler,
    private val versionChecker: VersionChecker
) : BaseViewModel() {

    val hasPermission: ObservableBoolean = ObservableBoolean(false) //Отображение интерфейса с разрешениями

    val filesLoadingIndicator: ObservableBoolean = ObservableBoolean(true) //Индикатор загрузки файлов
    val noFilesIndicator: ObservableBoolean = ObservableBoolean(false) //Сообщение что нет файлов
    val documentLoadingIndicator: ObservableBoolean = ObservableBoolean(true) //Индикатор загрузки документа
    val noDocumentsIndicator: ObservableBoolean = ObservableBoolean(false) //Сообщение что нет документов

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений
    val hasAccessEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Доступ к хранилищу

    val fileListEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Добавление файлов в проводник
    val fileUpdateListEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Обновление текущей директории
    val fileTabsEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Добавление новой вкладки в проводник

    val documentAllTabsEvent: SingleLiveEvent<List<DocumentModel>> = SingleLiveEvent() //Загрузка кешированных документов
    val documentTabEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Добавление вкладки в список документов
    val documentTextEvent: SingleLiveEvent<String> = SingleLiveEvent() //Чтение файла
    val documentLoadedEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Для загрузки скроллинга/выделения

    val deleteFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Удаление файла
    val renameFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Переименование файла
    val propertiesEvent: SingleLiveEvent<PropertiesModel> = SingleLiveEvent() //Свойства файла (диалог)

    // region PREFERENCES

    val themeEvent: SingleLiveEvent<TextProcessor.Theme> = SingleLiveEvent() //Тема редактора
    val fullscreenEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Полноэкранный режим
    val backEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Подтверждение выхода

    val fontSizeEvent: SingleLiveEvent<Float> = SingleLiveEvent() //Размер шрифта
    val fontTypeEvent: SingleLiveEvent<String> = SingleLiveEvent() //Тип шрифта

    val resumeSessionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Повторное открытие вкладок после выхода
    val tabLimitEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Лимит вкладок

    val extendedKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Отображать доп. символы
    val softKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Упрощенная клавиатура (landscape orientation)ы
    val imeKeyboardEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Отображать подсказки/ошибки при вводе

    // endregion PREFERENCES

    val unopenableExtensions = arrayOf( //Неоткрываемые расширения файлов
        ".apk", ".mp3", ".mp4", ".wav", ".pdf", ".avi", ".wmv", ".m4a", ".png", ".jpg", ".jpeg", ".zip", ".wad",
        ".7z", ".rar", ".gif", ".xls", ".doc", ".dat", ".jar", ".tar", ".torrent", ".xd", ".docx", ".temp"
    )
    var sortMode: Int = FileSorter.SORT_BY_NAME
    var fileSorter: Comparator<in FileModel> = FileSorter.getComparator(sortMode)
    var showHidden: Boolean = true
    var foldersOnTop: Boolean = true

    private var fileList: List<FileModel> = emptyList()

    // region FILE_REPOSITORY

    fun getDefaultLocation(): FileModel
            = fileRepository.getDefaultLocation()

    fun makeList(path: FileModel) {
        filesLoadingIndicator.set(true)
        fileRepository.makeList(path)
            .map { files ->
                val newList = mutableListOf<FileModel>()
                files.forEach { file ->
                    if(file.isHidden) {
                        if(showHidden) {
                            newList.add(file)
                        }
                    } else {
                        newList.add(file)
                    }
                }
                newList.toList()
            }
            .map { it.sortedWith(fileSorter) }
            .map {
                it.sortedBy { file ->
                    !file.isFolder == foldersOnTop
                }
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { list ->
                fileList = list
                fileListEvent.value = list
                filesLoadingIndicator.set(false)
                noFilesIndicator.set(list.isEmpty())
            }
            .disposeOnViewModelDestroy()
    }

    fun createFile(parent: FileModel, child: FileModel) {
        fileRepository.createFile(child)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { file ->
                if(file.isFolder) {
                    fileTabsEvent.value = file
                } else {
                    makeList(parent) //update the list
                    documentTabEvent.value = DocumentConverter.toModel(file)
                }
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

    fun renameFile(renamedFile: FileModel, newName: String) {
        fileRepository.renameFile(renamedFile, newName)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parent ->
                renameFileEvent.value = renamedFile
                makeList(parent) //update the list
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

    fun deleteFile(deletedFile: FileModel) {
        fileRepository.deleteFile(deletedFile)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parent ->
                deleteFileEvent.value = deletedFile
                makeList(parent) //update the list
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

    fun propertiesOf(documentModel: DocumentModel) = propertiesOf(FileConverter.toModel(documentModel))
    fun propertiesOf(fileModel: FileModel) {
        fileRepository.propertiesOf(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { propertiesEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    fun loadFile(documentModel: DocumentModel) {
        documentLoadingIndicator.set(true)
        fileRepository.loadFile(documentModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    documentTextEvent.value = it
                    documentLoadedEvent.value = documentModel
                    documentLoadingIndicator.set(false)
                },
                onError = {
                    toastEvent.value = R.string.message_error
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
                }
            )
            .disposeOnViewModelDestroy()
    }

    // endregion FILE_REPOSITORY

    // region EXPLORER

    fun searchEvents(searchView: SearchView) {
        searchView
            .queryTextChangeEvents()
            .skipInitialValue()
            .debounce(200, TimeUnit.MILLISECONDS)
            .filter { it.queryText.isEmpty() || it.queryText.length >= 2 }
            .distinctUntilChanged()
            .observeOn(schedulersProvider.mainThread())
            .subscribeBy {
                onSearchQueryFilled(it.queryText)
            }
            .disposeOnViewModelDestroy()
    }

    fun setFilterHidden(filter: Boolean) = preferenceHandler.setFilterHidden(filter)
    fun setSortMode(mode: String) = preferenceHandler.setSortMode(mode)

    private fun onSearchQueryFilled(query: CharSequence): Boolean {
        val newQuery = query.toString().toLowerCase()
        val collection: MutableList<FileModel> = mutableListOf()
        if(newQuery.isEmpty()) {
            collection.addAll(fileList)
        } else {
            for(row in fileList) {
                if(row.name.toLowerCase().contains(newQuery)) {
                    collection.add(row)
                }
            }
        }
        noFilesIndicator.set(collection.isEmpty())
        fileListEvent.value = collection
        return true
    }

    // endregion EXPLORER

    // region EDITOR

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
                            .subscribe {
                                cacheHandler.invalidateCaches()
                            }
                            .disposeOnViewModelDestroy()
                        noDocumentsIndicator.set(true)
                    }
                    documentLoadingIndicator.set(false)
                },
                onError = {
                    toastEvent.value = R.string.message_error
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun saveToCache(documentModel: DocumentModel, text: String) {
        Completable
            .fromAction {
                database.documentDao().update(DocumentConverter.toCache(documentModel)) // Save to Database
                cacheHandler.saveToCache(documentModel, text) // Save to Cache
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribe()
            .disposeOnViewModelDestroy()
    }

    /*fun saveUndoRedoStack(undoStack: UndoStack, redoStack: undoStack) {

    }*/

    fun addDocument(file: File) = addDocument(FileConverter.toModel(file))
    fun addDocument(fileModel: FileModel) {
        documentTabEvent.value = DocumentConverter.toModel(fileModel)
    }

    fun removeDocument(documentModel: DocumentModel) {
        Completable
            .fromAction {
                database.documentDao().delete(DocumentConverter.toCache(documentModel)) // Delete from Database
                cacheHandler.invalidateCache(documentModel) // Delete from Cache
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribe()
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

        //IME Keyboard
        preferenceHandler.getImeKeyboard()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { imeKeyboardEvent.value = it }
            .disposeOnViewModelDestroy()

        //Filter Hidden Files
        preferenceHandler.getFilterHidden()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { show ->
                showHidden = show
                if(hasPermission.get()) {
                    fileUpdateListEvent.value = true
                }
            }
            .disposeOnViewModelDestroy()

        //Sort Mode
        preferenceHandler.getSortMode()
            .asObservable()
            .map(Integer::parseInt)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { mode ->
                sortMode = mode
                fileSorter = FileSorter.getComparator(mode)
                if(hasPermission.get()) {
                    fileUpdateListEvent.value = true
                }
            }
            .disposeOnViewModelDestroy()

        //Folders on Top
        preferenceHandler.getFoldersOnTop()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { onTop ->
                foldersOnTop = onTop
                if(hasPermission.get()) {
                    fileUpdateListEvent.value = true
                }
            }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES
}