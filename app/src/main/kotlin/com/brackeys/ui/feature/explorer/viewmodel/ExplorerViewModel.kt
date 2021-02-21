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

package com.brackeys.ui.feature.explorer.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.brackeys.ui.R
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.data.utils.FileSorter
import com.brackeys.ui.data.utils.containsFileModel
import com.brackeys.ui.data.utils.replaceList
import com.brackeys.ui.data.utils.schedulersIoToMain
import com.brackeys.ui.domain.providers.rx.SchedulersProvider
import com.brackeys.ui.feature.base.viewmodel.BaseViewModel
import com.brackeys.ui.feature.explorer.utils.Operation
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.exception.*
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileTree
import com.brackeys.ui.filesystem.base.model.PropertiesModel
import com.brackeys.ui.utils.event.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val schedulersProvider: SchedulersProvider,
    private val settingsManager: SettingsManager,
    @Named("Local")
    private val filesystem: Filesystem
) : BaseViewModel() {

    companion object {
        private const val TAG = "ExplorerViewModel"
    }

    // region EVENTS

    val toastEvent = SingleLiveEvent<Int>() // Отображение сообщений
    val showAppBarEvent = MutableLiveData<Boolean>() // Отображение вкладок
    val allowPasteFiles = MutableLiveData<Boolean>() // Отображение кнопки "Вставить"
    val stateLoadingFiles = MutableLiveData(true) // Индикатор загрузки файлов
    val stateNothingFound = MutableLiveData(false) // Сообщение что нет файлов

    val filesUpdateEvent = SingleLiveEvent<Unit>() // Запрос на обновление списка файлов
    val selectAllEvent = SingleLiveEvent<Unit>() // Выделить все файлы
    val deselectAllEvent = SingleLiveEvent<Unit>() // Сбросить выделение со всех файлов
    val createEvent = SingleLiveEvent<Unit>() // Создать файл
    val copyEvent = SingleLiveEvent<Unit>() // Скопировать выделенные файлы
    val deleteEvent = SingleLiveEvent<Unit>() // Удалить выделенные файлы
    val cutEvent = SingleLiveEvent<Unit>() // Вырезать выделенные файлы
    val pasteEvent = SingleLiveEvent<Unit>() // Вставить скопированные файлы
    val openAsEvent = SingleLiveEvent<Unit>() // Открыть файл как
    val renameEvent = SingleLiveEvent<Unit>() // Переименовать файл
    val propertiesEvent = SingleLiveEvent<Unit>() // Свойства файла
    val copyPathEvent = SingleLiveEvent<Unit>() // Скопировать путь к файлу
    val archiveEvent = SingleLiveEvent<Unit>() // Архивация файлов в .zip

    val tabsEvent = MutableLiveData<List<FileModel>>() // Список вкладок
    val selectionEvent = MutableLiveData<List<FileModel>>() // Список выделенных файлов
    val progressEvent = SingleLiveEvent<Int>() // Прогресс выполнения операции
    val filesEvent = SingleLiveEvent<FileTree>() // Список файлов
    val searchEvent = SingleLiveEvent<List<FileModel>>() // Отфильтрованый список файлов
    val clickEvent = SingleLiveEvent<FileModel>() // Имитация нажатия на файл
    val propertiesOfEvent = SingleLiveEvent<PropertiesModel>() // Свойства файла

    // endregion EVENTS

    val tabsList = mutableListOf<FileModel>()
    val tempFiles = mutableListOf<FileModel>()
    val cancelableDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    var operation: Operation = Operation.COPY

    var showHidden: Boolean
        get() = settingsManager.filterHidden
        set(value) {
            settingsManager.filterHidden = value
            filesUpdateEvent.call()
        }

    var foldersOnTop: Boolean
        get() = settingsManager.foldersOnTop
        set(value) {
            settingsManager.foldersOnTop = value
            filesUpdateEvent.call()
        }

    val viewMode: Int
        get() = Integer.parseInt(settingsManager.viewMode)

    var sortMode: Int
        get() = Integer.parseInt(settingsManager.sortMode)
        set(value) {
            settingsManager.sortMode = value.toString()
            filesUpdateEvent.call()
        }

    private val searchList = mutableListOf<FileModel>()

    override fun onCleared() {
        super.onCleared()
        cancelableDisposable.dispose()
    }

    fun provideDirectory(fileModel: FileModel?) {
        filesystem.provideDirectory(fileModel)
            .doOnSubscribe {
                stateNothingFound.postValue(false)
                stateLoadingFiles.postValue(true)
            }
            .doOnSuccess {
                stateLoadingFiles.postValue(false)
                stateNothingFound.postValue(it.children.isEmpty())
            }
            .map { fileTree ->
                val newList = mutableListOf<FileModel>()
                fileTree.children.forEach { file ->
                    if (file.isHidden) {
                        if (showHidden) {
                            newList.add(file)
                        }
                    } else {
                        newList.add(file)
                    }
                }
                fileTree.copy(children = newList)
            }
            .map { fileTree ->
                val comparator = FileSorter.getComparator(sortMode)
                val children = fileTree.children.sortedWith(comparator)
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
                    searchList.replaceList(fileTree.children)
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
        stateNothingFound.value = collection.isEmpty()
        searchEvent.value = collection
    }

    fun createFile(fileModel: FileModel) {
        filesystem.createFile(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    filesUpdateEvent.call()
                    clickEvent.value = it
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
                onSuccess = {
                    filesUpdateEvent.call()
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

    fun deleteFiles(fileModels: List<FileModel>) {
        Observable.fromIterable(fileModels)
            .doOnSubscribe { progressEvent.postValue(0) }
            .doOnError { progressEvent.postValue(Int.MAX_VALUE) }
            .concatMapSingle {
                filesystem.deleteFile(it)
                    .delay(20, TimeUnit.MILLISECONDS)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onNext = {
                    progressEvent.value = (progressEvent.value ?: 0) + 1
                },
                onComplete = {
                    filesUpdateEvent.call()
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
            .addTo(cancelableDisposable)
    }

    fun copyFiles(source: List<FileModel>, destPath: String) {
        filesystem.provideFile(destPath)
            .flatMapObservable { dest ->
                Observable.fromIterable(source)
                    .map { it to dest }
            }
            .doOnSubscribe { progressEvent.postValue(0) }
            .doOnError { progressEvent.postValue(Int.MAX_VALUE) }
            .concatMapSingle { (file, dest) ->
                filesystem.copyFile(file, dest)
                    .delay(20, TimeUnit.MILLISECONDS)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onNext = {
                    progressEvent.value = (progressEvent.value ?: 0) + 1
                },
                onComplete = {
                    filesUpdateEvent.call()
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
            .addTo(cancelableDisposable)
    }

    fun cutFiles(source: List<FileModel>, destPath: String) {
        filesystem.provideFile(destPath)
            .flatMapObservable { dest ->
                Observable.fromIterable(source)
                    .map { it to dest }
            }
            .doOnSubscribe { progressEvent.postValue(0) }
            .doOnError { progressEvent.postValue(Int.MAX_VALUE) }
            .concatMapSingle { (file, dest) ->
                filesystem.copyFile(file, dest)
                    .delay(20, TimeUnit.MILLISECONDS)
            }
            .concatMapSingle {
                filesystem.deleteFile(it)
                    .delay(20, TimeUnit.MILLISECONDS)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onNext = {
                    progressEvent.value = (progressEvent.value ?: 0) + 1
                },
                onComplete = {
                    filesUpdateEvent.call()
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
            .addTo(cancelableDisposable)
    }

    fun propertiesOf(fileModel: FileModel) {
        filesystem.propertiesOf(fileModel)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    propertiesOfEvent.value = it
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

    fun compressFiles(source: List<FileModel>, destPath: String, archiveName: String) {
        filesystem.provideFile(destPath)
            .flatMapObservable { filesystem.compress(source, it, archiveName) }
            .doOnSubscribe { progressEvent.postValue(0) }
            .doOnError { progressEvent.postValue(Int.MAX_VALUE) }
            .concatMap {
                Observable.just(it)
                    .delay(20, TimeUnit.MILLISECONDS)
            }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onNext = {
                    progressEvent.value = (progressEvent.value ?: 0) + 1
                },
                onComplete = {
                    filesUpdateEvent.call()
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

    fun extractAll(source: FileModel, destPath: String) {
        filesystem.provideFile(destPath)
            .flatMap { filesystem.extractAll(source, it) }
            .doOnSubscribe { progressEvent.postValue(0) }
            .doOnError { progressEvent.postValue(Int.MAX_VALUE) }
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy(
                onSuccess = {
                    progressEvent.value = (progressEvent.value ?: 0) + 1 // FIXME у диалога всегда будет 1 файл
                    filesUpdateEvent.call()
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
                        is UnsupportedArchiveException -> {
                            toastEvent.value = R.string.message_unsupported_archive
                        }
                        is EncryptedArchiveException -> {
                            toastEvent.value = R.string.message_encrypted_archive
                        }
                        is SplitArchiveException -> {
                            toastEvent.value = R.string.message_split_archive
                        }
                        is InvalidArchiveException -> {
                            toastEvent.value = R.string.message_invalid_archive
                        }
                        else -> {
                            toastEvent.value = R.string.message_unknown_exception
                        }
                    }
                }
            )
            .disposeOnViewModelDestroy()
    }
}