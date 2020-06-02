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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.data.utils.commons.FileSorter
import com.lightteam.modpeide.data.utils.extensions.containsFileModel
import com.lightteam.modpeide.data.utils.extensions.replaceList
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.filesystem.exception.DirectoryExpectedException
import com.lightteam.filesystem.exception.FileAlreadyExistsException
import com.lightteam.filesystem.exception.FileNotFoundException
import com.lightteam.filesystem.model.FileModel
import com.lightteam.filesystem.model.FileTree
import com.lightteam.filesystem.model.PropertiesModel
import com.lightteam.filesystem.repository.Filesystem
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class ExplorerViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val filesystem: Filesystem
) : BaseViewModel() {

    companion object {
        private const val TAG = "ExplorerViewModel"
    }

    // region UI

    val hasPermission: ObservableBoolean = ObservableBoolean(false) // Отображение интерфейса с разрешениями

    val stateLoadingFiles: ObservableBoolean = ObservableBoolean(true) // Индикатор загрузки файлов
    val stateNothingFound: ObservableBoolean = ObservableBoolean(false) // Сообщение что нет файлов

    // endregion UI

    // region EVENTS

    val tabsEvent: MutableLiveData<List<FileModel>> = MutableLiveData() // Список вкладок
    val toastEvent: SingleLiveEvent<Int> = SingleLiveEvent() // Отображение сообщений
    val hasAccessEvent: SingleLiveEvent<Boolean> = SingleLiveEvent() // Доступ к хранилищу

    val fabEvent: SingleLiveEvent<Unit> = SingleLiveEvent() // Кнопка "+"
    val filesUpdateEvent: SingleLiveEvent<Unit> = SingleLiveEvent() // Запрос на загрузку списка файлов
    val selectionEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() // Список выделенных файлов
    val clearSelectionEvent: SingleLiveEvent<Unit> = SingleLiveEvent() // Очистка выделенных файлов

    val filesEvent: SingleLiveEvent<FileTree> = SingleLiveEvent() // Список файлов
    val searchEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() // Отфильтрованый список файлов
    val createEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() // Создание файла
    val propertiesEvent: SingleLiveEvent<PropertiesModel> = SingleLiveEvent() // Свойства файла

    // endregion EVENTS

    var sortMode: Int = FileSorter.SORT_BY_NAME
    var showHidden: Boolean = true

    val tabsList: MutableList<FileModel> = mutableListOf()
    private val searchList: MutableList<FileModel> = mutableListOf()

    private var fileSorter: Comparator<in FileModel> = FileSorter.getComparator(sortMode)
    private var foldersOnTop: Boolean = true

    fun provideDirectory(fileModel: FileModel?) {
        filesystem.provideDirectory(fileModel)
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
        filesystem.createFile(fileModel)
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
        filesystem.renameFile(fileModel, newName)
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
        filesystem.deleteFile(fileModel)
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
        filesystem.propertiesOf(fileModel)
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
                if (hasPermission.get()) {
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
                if (hasPermission.get()) {
                    filesUpdateEvent.call()
                }
            }
            .disposeOnViewModelDestroy()

        preferenceHandler.getFoldersOnTop()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { onTop ->
                foldersOnTop = onTop
                if (hasPermission.get()) {
                    filesUpdateEvent.call()
                }
            }
            .disposeOnViewModelDestroy()
    }

    // endregion PREFERENCES

    class Factory(
        private val schedulersProvider: SchedulersProvider,
        private val preferenceHandler: PreferenceHandler,
        private val filesystem: Filesystem
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when {
                modelClass === ExplorerViewModel::class.java ->
                    ExplorerViewModel(
                        schedulersProvider,
                        preferenceHandler,
                        filesystem
                    ) as T
                else -> null as T
            }
        }
    }
}