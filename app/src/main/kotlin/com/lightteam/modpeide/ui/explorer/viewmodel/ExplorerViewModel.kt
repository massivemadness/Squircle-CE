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

package com.lightteam.modpeide.ui.explorer.viewmodel

import android.util.Log
import androidx.databinding.ObservableBoolean
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.containsFileModel
import com.lightteam.modpeide.data.utils.extensions.replaceList
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.exception.DirectoryExpectedException
import com.lightteam.modpeide.domain.exception.FileAlreadyExistsException
import com.lightteam.modpeide.domain.exception.FileNotFoundException
import com.lightteam.modpeide.domain.model.explorer.FileModel
import com.lightteam.modpeide.domain.model.explorer.FileTree
import com.lightteam.modpeide.domain.model.explorer.PropertiesModel
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class ExplorerViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val fileRepository: FileRepository,
    private val preferenceHandler: PreferenceHandler
) : BaseViewModel() {

    companion object {
        private const val TAG = "ExplorerViewModel"
    }

    // region UI

    val hasPermission: ObservableBoolean = ObservableBoolean(false) //Отображение интерфейса с разрешениями

    val stateLoadingFiles: ObservableBoolean = ObservableBoolean(true) //Индикатор загрузки файлов
    val stateNothingFound: ObservableBoolean = ObservableBoolean(false) //Сообщение что нет файлов

    // endregion UI

    // region EVENTS

    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Отображение сообщений
    val hasAccessEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() //Доступ к хранилищу
    val tabsEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Полное обновление списка вкладок
    val filesEvent: SingleLiveEvent<FileTree> = SingleLiveEvent() //Список файлов
    val filesUpdateEvent: SingleLiveEvent<Unit> = SingleLiveEvent() //Запрос на загрузку списка файлов
    val searchEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Отфильтрованый список файлов
    val fabEvent: SingleLiveEvent<Unit> = SingleLiveEvent() //Кнопка "+"
    val createEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Создание файла
    val propertiesEvent: SingleLiveEvent<PropertiesModel> = SingleLiveEvent() //Свойства файла

    // endregion EVENTS

    var sortMode: Int = FileSorter.SORT_BY_NAME
    var showHidden: Boolean = true

    private val tabsList: MutableList<FileModel> = mutableListOf()
    private val searchList: MutableList<FileModel> = mutableListOf()
    private var fileSorter: Comparator<in FileModel> = FileSorter.getComparator(sortMode)
    private var foldersOnTop: Boolean = true

    fun provideDirectory(fileModel: FileModel?) {
        fileRepository.provideDirectory(fileModel)
            .doOnSubscribe {
                stateNothingFound.set(false)
                stateLoadingFiles.set(true)
            }
            .doOnSuccess {
                stateLoadingFiles.set(false)
                stateNothingFound.set(it.children.isEmpty())
            }
            .map { fileTree ->
                val newList = mutableListOf<FileModel>()
                fileTree.children.forEach { file ->
                    if(file.isHidden) {
                        if(showHidden) {
                            newList.add(file)
                        }
                    } else {
                        newList.add(file)
                    }
                }
                fileTree.copy(children = newList)
            }
            .map { fileTree ->
                val children = fileTree.children.sortedWith(fileSorter)
                fileTree.copy(children = children)
            }
            .map { fileTree ->
                val children = fileTree.children.sortedBy { file -> !file.isFolder == foldersOnTop }
                fileTree.copy(children = children)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { fileTree ->
                    if (!tabsList.containsFileModel(fileTree.parent)) {
                        tabsList.add(fileTree.parent)
                        tabsEvent.value = tabsList
                    }
                    searchList.replaceList(fileTree.children) //Фильтрация по текущему списку
                    filesEvent.value = fileTree
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    when (it) {
                        is DirectoryExpectedException -> {
                            toastEvent.value = R.string.message_directory_expected
                        }
                        else -> {
                            toastEvent.value = R.string.message_unknown_exception
                        }
                    }
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun searchFile(query: CharSequence) {
        val collection: MutableList<FileModel> = mutableListOf()
        val newQuery = query.toString().toLowerCase(Locale.getDefault())
        if (newQuery.isEmpty()) {
            collection.addAll(searchList)
        } else {
            for (row in searchList) {
                if (row.name.toLowerCase(Locale.getDefault()).contains(newQuery)) {
                    collection.add(row)
                }
            }
        }
        stateNothingFound.set(collection.isEmpty())
        searchEvent.value = collection
    }

    fun createFile(fileModel: FileModel) {
        fileRepository.createFile(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    createEvent.value = it
                    toastEvent.value = R.string.message_done
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    when (it) {
                        is FileAlreadyExistsException -> {
                            toastEvent.value = R.string.message_file_already_exists
                        }
                        else -> {
                            toastEvent.value = R.string.message_unknown_exception
                        }
                    }
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun renameFile(fileModel: FileModel, newName: String) {
        fileRepository.renameFile(fileModel, newName)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { parent ->
                    provideDirectory(parent)
                    toastEvent.value = R.string.message_done
                },
                onError = {
                    Log.e(TAG, it.message, it)
                    when (it) {
                        is FileNotFoundException -> {
                            toastEvent.value = R.string.message_file_not_found
                        }
                        is FileAlreadyExistsException -> {
                            toastEvent.value = R.string.message_file_already_exists
                        }
                        else -> {
                            toastEvent.value = R.string.message_unknown_exception
                        }
                    }
                }
            )
            .disposeOnViewModelDestroy()
    }

    fun deleteFile(fileModel: FileModel) {
        fileRepository.deleteFile(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = { parent ->
                    provideDirectory(parent)
                    toastEvent.value = R.string.message_done
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

    fun propertiesOf(fileModel: FileModel) {
        fileRepository.propertiesOf(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    propertiesEvent.value = it
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

    fun removeLastTabs(n: Int) {
        tabsList.subList(tabsList.size - n, tabsList.size).clear()
        tabsEvent.value = tabsList
    }

    // region PREFERENCES

    fun setFilterHidden(filter: Boolean) {
        preferenceHandler.getFilterHidden().set(filter)
    }

    fun setSortMode(mode: String) {
        preferenceHandler.getSortMode().set(mode)
    }

    fun observePreferences() {
        preferenceHandler.getFilterHidden()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { show ->
                showHidden = show
                if (hasPermission.get() && tabsList.isNotEmpty()) {
                    filesUpdateEvent.call()
                }
            }
            .disposeOnViewModelDestroy()

        preferenceHandler.getSortMode()
            .asObservable()
            .map(Integer::parseInt)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { mode ->
                sortMode = mode
                fileSorter = FileSorter.getComparator(mode)
                if (hasPermission.get() && tabsList.isNotEmpty()) {
                    filesUpdateEvent.call()
                }
            }
            .disposeOnViewModelDestroy()

        preferenceHandler.getFoldersOnTop()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { onTop ->
                foldersOnTop = onTop
                if (hasPermission.get() && tabsList.isNotEmpty()) {
                    filesUpdateEvent.call()
                }
            }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES
}