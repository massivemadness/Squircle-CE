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
import com.lightteam.modpeide.data.storage.PreferenceHandler
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.providers.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.presentation.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val fileRepository: FileRepository,
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler
) : BaseViewModel() {

    val hasPermission: ObservableBoolean = ObservableBoolean(false)
    val listLoadingIndicator: ObservableBoolean = ObservableBoolean(true)
    val noItemsIndicator: ObservableBoolean = ObservableBoolean(false)

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений
    val hasAccessEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Доступ к хранилищу
    val listEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Обновление списка
    val tabsEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Добавление новой вкладки
    val documentEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Открытие документа
    val deleteFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Удаление файла
    val renameFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Переименование файла

    var sortMode = preferenceHandler.getSortMode()
    var fileSorter: Comparator<in FileModel> = FileSorter.getComparator(sortMode)
    var showHidden: Boolean = preferenceHandler.getFilterHidden()
    var foldersOnTop: Boolean = preferenceHandler.getFoldersOnTop()

    private var filesList: List<FileModel> = emptyList()

    // region FILE_REPOSITORY

    fun getDefaultLocation(): FileModel = fileRepository.getDefaultLocation()

    fun loadFiles(path: FileModel) {
        listLoadingIndicator.set(true)
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
            .subscribeBy(
                onSuccess = { list ->
                    listLoadingIndicator.set(false)
                    noItemsIndicator.set(list.isEmpty())
                    filesList = list
                    listEvent.value = list
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun createFile(parent: FileModel, child: FileModel) {
        fileRepository.createFile(child)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { file ->
                if(file.isFolder) {
                    tabsEvent.value = file
                } else {
                    loadFiles(parent) //update the list
                    documentEvent.value = file
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
                loadFiles(parent) //update the list
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

    fun deleteFile(deletedFile: FileModel) {
        fileRepository.deleteFile(deletedFile)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { parent ->
                deleteFileEvent.value = deletedFile
                loadFiles(parent) //update the list
                toastEvent.value = R.string.message_done
            }
            .disposeOnViewModelDestroy()
    }

    // endregion FILE_REPOSITORY

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

    private fun onSearchQueryFilled(query: CharSequence): Boolean {
        val newQuery = query.toString().toLowerCase()
        val collection: MutableList<FileModel> = mutableListOf()
        if(newQuery.isEmpty()) {
            collection.addAll(filesList)
        } else {
            for(row in filesList) {
                if(row.name.toLowerCase().contains(newQuery)) { //Поиск по названию
                    collection.add(row)
                }
            }
        }
        noItemsIndicator.set(collection.isEmpty())
        listEvent.value = collection
        return true
    }

    fun refreshFilter() {
        sortMode = preferenceHandler.getSortMode()
        fileSorter = FileSorter.getComparator(sortMode)
        showHidden = preferenceHandler.getFilterHidden()
        foldersOnTop = preferenceHandler.getFoldersOnTop()
    }

    fun setFilterHidden(filter: Boolean) {
        preferenceHandler.setFilterHidden(filter)
        showHidden = filter
    }

    fun setSortMode(mode: String) {
        preferenceHandler.setSortMode(mode)
        sortMode = preferenceHandler.getSortMode()
        fileSorter = FileSorter.getComparator(sortMode)
    }
}