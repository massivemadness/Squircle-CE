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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import com.lightteam.filesystem.model.FileModel
import com.lightteam.modpeide.data.utils.commons.PreferenceHandler
import com.lightteam.modpeide.data.utils.extensions.schedulersIoToMain
import com.lightteam.modpeide.domain.providers.rx.SchedulersProvider
import com.lightteam.modpeide.ui.base.viewmodel.BaseViewModel
import com.lightteam.modpeide.utils.event.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy

class MainViewModel(
    private val schedulersProvider: SchedulersProvider,
    private val preferenceHandler: PreferenceHandler,
    private val appUpdateManager: AppUpdateManager
) : BaseViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val updateEvent: SingleLiveEvent<Triple<AppUpdateManager, AppUpdateInfo, Int>> = SingleLiveEvent()
    val installEvent: SingleLiveEvent<Unit> = SingleLiveEvent()
    val fullscreenEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val backEvent: SingleLiveEvent<Boolean> = SingleLiveEvent()

    val openDrawerEvent: SingleLiveEvent<Unit> = SingleLiveEvent()
    val closeDrawerEvent: SingleLiveEvent<Unit> = SingleLiveEvent()

    // События для связи проводника и редактора
    val openFileEvent: SingleLiveEvent<FileModel> = SingleLiveEvent()
    val propertiesEvent: SingleLiveEvent<FileModel> = SingleLiveEvent()

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            installEvent.call()
        }
    }

    fun checkForUpdates() {
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

    fun observePreferences() {
        preferenceHandler.getFullscreenMode()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { fullscreenEvent.value = it }
            .disposeOnViewModelDestroy()

        preferenceHandler.getConfirmExit()
            .asObservable()
            .schedulersIoToMain(schedulersProvider)
            .subscribeBy { backEvent.value = it }
            .disposeOnViewModelDestroy()
    }

    class Factory(
        private val schedulersProvider: SchedulersProvider,
        private val preferenceHandler: PreferenceHandler,
        private val appUpdateManager: AppUpdateManager
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return when {
                modelClass === MainViewModel::class.java -> {
                    MainViewModel(
                        schedulersProvider,
                        preferenceHandler,
                        appUpdateManager
                    ) as T
                }
                else -> null as T
            }
        }
    }
}