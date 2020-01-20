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
import com.lightteam.modpeide.data.converter.FileConverter
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.endsWith
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.model.DocumentModel
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.model.PropertiesModel
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.ui.main.adapters.BreadcrumbAdapter
import com.lightteam.modpeide.ui.main.adapters.DocumentAdapter
import com.lightteam.modpeide.utils.commons.VersionChecker
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.util.*

class ExplorerViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val fileRepository: FileRepository,
    private val preferenceHandler: PreferenceHandler,
    private val breadcrumbAdapter: BreadcrumbAdapter,
    private val documentAdapter: DocumentAdapter,
    private val versionChecker: VersionChecker
) : BaseViewModel() {

    companion object {
        private const val TAG = "ExplorerViewModel"
    }

    // region UI

    val hasPermission: ObservableBoolean = ObservableBoolean(false) //Отображение интерфейса с разрешениями

    val filesLoadingIndicator: ObservableBoolean = ObservableBoolean(true) //Индикатор загрузки файлов
    val noFilesIndicator: ObservableBoolean = ObservableBoolean(false) //Сообщение что нет файлов

    // endregion UI

    // region EVENTS

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений
    val hasAccessEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Доступ к хранилищу

    val fileListEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Добавление файлов в проводник
    val fileUpdateListEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Обновление текущей директории
    val fileTabsAddEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Добавление новой вкладки в проводник
    val fileTabsRemoveEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Удаление вкладки из проводника
    val fileTabsSelectEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Выбор вкладки в проводнике
    val fileNotSupportedEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Неподдерживаемый файл

    val deleteFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Удаление файла
    val renameFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Переименование файла
    val propertiesEvent: SingleLiveEvent<PropertiesModel> = SingleLiveEvent() //Свойства файла

    // endregion EVENTS

    var sortMode: Int = FileSorter.SORT_BY_NAME
    var showHidden: Boolean = true

    private var fileSorter: Comparator<in FileModel> = FileSorter.getComparator(sortMode)
    private var foldersOnTop: Boolean = true

    private val openableExtensions = arrayOf( //Открываемые расширения файлов
        ".txt", ".js", ".json", ".java", ".md", ".lua"
    )

    private var fileList: List<FileModel> = emptyList()

    fun openDocument(file: File) = openDocument(FileConverter.toModel(file))
    fun openDocument(fileModel: FileModel) {
        if(fileModel.name.endsWith(openableExtensions)) {
            /*if(documentAdapter.size() < tabLimitEvent.value!!) {
                documentTabEvent.value = DocumentConverter.toModel(fileModel)
            } else {
                toastEvent.value = R.string.message_tab_limit_achieved
            }*/
        } else {
            fileNotSupportedEvent.value = fileModel
        }
    }

    fun addToStack(currPos: Int, fileModel: FileModel) {
        val nextPos = currPos + 1
        val pathPos = breadcrumbAdapter.indexOf(fileModel)

        when {
            currPos == -1 -> {
                breadcrumbAdapter.add(fileModel)
            }
            pathPos == nextPos -> {
                fileTabsSelectEvent.value = nextPos
            }
            pathPos == -1 -> {
                for (pos in breadcrumbAdapter.count() downTo nextPos) {
                    breadcrumbAdapter.removeAt(pos)
                    fileTabsRemoveEvent.value = pos
                }
                breadcrumbAdapter.add(fileModel)
            }
        }
    }

    fun defaultLocation(): FileModel
            = fileRepository.defaultLocation()

    fun provideDirectory(position: Int) {
        val path = breadcrumbAdapter.get(position)
        filesLoadingIndicator.set(true)
        fileRepository.provideDirectory(path)
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
            .subscribeBy(
                onSuccess = { list ->
                    fileList = list
                    fileListEvent.value = list
                    filesLoadingIndicator.set(false)
                    noFilesIndicator.set(list.isEmpty())
                },
                onError = {
                    toastEvent.value = R.string.message_error
                    Log.e(TAG, it.message, it)
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun createFile(parent: FileModel, child: FileModel) {
        fileRepository.createFile(child)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { file ->
                if(file.isFolder) {
                    fileTabsAddEvent.value = file
                } else {
                    //provideDirectory(parent) //update the list
                    //documentTabEvent.value = DocumentConverter.toModel(file)
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
                //provideDirectory(parent) //update the list
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

    fun deleteFile(deletedFile: FileModel) {
        fileRepository.deleteFile(deletedFile)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parent ->
                deleteFileEvent.value = deletedFile
                //provideDirectory(parent) //update the list
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

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

    fun setFilterHidden(filter: Boolean) = preferenceHandler.setFilterHidden(filter)
    fun setSortMode(mode: String) = preferenceHandler.setSortMode(mode)

    fun onSearchQueryFilled(query: CharSequence): Boolean {
        val collection: MutableList<FileModel> = mutableListOf()
        val newQuery = query.toString().toLowerCase(Locale.getDefault())
        if(newQuery.isEmpty()) {
            collection.addAll(fileList)
        } else {
            for(row in fileList) {
                if(row.name.toLowerCase(Locale.getDefault()).contains(newQuery)) {
                    collection.add(row)
                }
            }
        }
        noFilesIndicator.set(collection.isEmpty())
        fileListEvent.value = collection
        return true
    }

    fun isUltimate(): Boolean = versionChecker.isUltimate

    fun observePreferences() {

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
}