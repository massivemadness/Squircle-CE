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
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.parser.ScriptEngine
import com.lightteam.modpeide.data.storage.cache.CacheHandler
import com.lightteam.modpeide.data.storage.database.AppDatabase
import com.lightteam.modpeide.data.storage.keyvalue.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.*
import com.lightteam.modpeide.domain.feature.undoredo.UndoStack
import com.lightteam.modpeide.domain.exception.FileNotFoundException
import com.lightteam.modpeide.domain.model.explorer.AnalysisModel
import com.lightteam.modpeide.domain.model.editor.DocumentModel
import com.lightteam.modpeide.domain.model.editor.DocumentContent
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.domain.repository.FileRepository
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.EventsQueue
import com.lightteam.modpeide.utils.event.PreferenceEvent
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import com.lightteam.modpeide.utils.theming.ThemeFactory
import io.reactivex.rxkotlin.subscribeBy

class EditorViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val appUpdateManager: AppUpdateManager,
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
    val selectionEvent: SingleLiveEvent<Int> = SingleLiveEvent() //Выделение вкладки уже открытого файла
    val unopenableEvent: SingleLiveEvent<DocumentModel> = SingleLiveEvent() //Неподдерживаемый файл
    val analysisEvent: SingleLiveEvent<AnalysisModel> = SingleLiveEvent() //Анализ кода
    val contentEvent: SingleLiveEvent<DocumentContent> = SingleLiveEvent() //Контент загруженного файла
    val updateEvent: SingleLiveEvent<Triple<AppUpdateManager, AppUpdateInfo, Int>> = SingleLiveEvent()
    val installEvent: SingleLiveEvent<Unit> = SingleLiveEvent()

    // endregion EVENTS

    // region PREFERENCES

    val preferenceEvent: EventsQueue = EventsQueue() //События с измененными настройками

    val backEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val resumeSessionEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    private val tabLimitEvent: SingleLiveEvent<Int> = SingleLiveEvent()

    // endregion PREFERENCES

    val tabsList: MutableList<DocumentModel> = mutableListOf()

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            installEvent.call()
        }
    }

    // region IN-APP UPDATES

    fun checkUpdate() {
        appUpdateManager.registerListener(installStateUpdatedListener)
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        updateEvent.value = Triple(appUpdateManager, appUpdateInfo, AppUpdateType.FLEXIBLE)
                    } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        updateEvent.value = Triple(appUpdateManager, appUpdateInfo, AppUpdateType.IMMEDIATE)
                    }
                } else {
                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message, it)
            }
    }

    fun completeUpdate() {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
        appUpdateManager.completeUpdate()
    }

    // endregion IN-APP UPDATES

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
                    onSuccess = {
                        tabsList.replaceList(it)
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
                .subscribeBy { cacheHandler.deleteAllCaches() }
                .disposeOnViewModelDestroy()
        }
    }

    fun loadFile(documentModel: DocumentModel) {
        val dataSource = if (cacheHandler.isCached(documentModel)) {
            cacheHandler.loadFromCache(documentModel)
        } else {
            fileRepository.loadFile(documentModel)
        }
        dataSource
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

    fun openFile(documentModel: DocumentModel) {
        if (documentModel.isOpenable()) {
            if (!tabsList.containsDocumentModel(documentModel)) {
                if (tabsList.size < tabLimitEvent.value!!) {
                    tabsList.add(documentModel)
                    stateNothingFound.set(tabsList.isEmpty())
                    documentEvent.value = documentModel
                } else {
                    toastEvent.value = R.string.message_tab_limit_achieved
                }
            } else {
                selectionEvent.value = tabsList.index(documentModel)
            }
        } else {
            unopenableEvent.value = documentModel
        }
    }

    fun analyze(position: Int, sourceCode: String) {
        ScriptEngine.analyze(tabsList[position].name, sourceCode)
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { analysisEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    // region PREFERENCES

    fun observePreferences() {
        preferenceHandler.getTheme()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.Theme(ThemeFactory.create(it))) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getFullscreenMode()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { preferenceEvent.offer(PreferenceEvent.Fullscreen(it)) }
            .disposeOnViewModelDestroy()

        preferenceHandler.getConfirmExit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { backEvent.value = it }
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
                if (documentsEvent.value == null) {
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
}