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
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.exception.DirectoryExpectedException
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.providers.SchedulersProvider
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
    val filesEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Список файлов
    val filesUpdateEvent: SingleLiveEvent<Unit> = SingleLiveEvent() //Обновление после смены фильтрации
    val searchEvent: SingleLiveEvent<List<FileModel>> = SingleLiveEvent() //Отфильтрованый список файлов
    val tabEvent: SingleLiveEvent<FileModel> = SingleLiveEvent() //Добавление новой вкладки

    // endregion EVENTS

    var searchList: List<FileModel> = emptyList()
    var sortMode: Int = FileSorter.SORT_BY_NAME
    var showHidden: Boolean = true

    private var fileSorter: Comparator<in FileModel> = FileSorter.getComparator(sortMode)
    private var foldersOnTop: Boolean = true

    fun provideDefaultDirectory() = provideDirectory(fileRepository.defaultLocation())
    fun provideDirectory(fileModel: FileModel) {
        fileRepository.provideDirectory(fileModel)
            .doOnSubscribe {
                stateNothingFound.set(false)
                stateLoadingFiles.set(true)
            }
            .doOnSuccess {
                stateLoadingFiles.set(false)
                stateNothingFound.set(it.isEmpty())
            }
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
            .map { it.sortedBy { file -> !file.isFolder == foldersOnTop } }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    filesEvent.value = it
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

    fun newTab(fileModel: FileModel?) {
        tabEvent.value = fileModel ?: fileRepository.defaultLocation()
    }

    // region PREFERENCES

    fun setFilterHidden(filter: Boolean) {
        preferenceHandler.setFilterHidden(filter)
    }

    fun setSortMode(mode: String) {
        preferenceHandler.setSortMode(mode)
    }

    fun observePreferences() {

        //Filter Hidden Files
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

        //Sort Mode
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

        //Folders on Top
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
}