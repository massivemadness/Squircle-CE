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

package com.brackeys.ui.feature.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.domain.model.editor.DocumentModel
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.utils.event.SingleLiveEvent
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.installStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val appUpdateManager: AppUpdateManager
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    val updateEvent = SingleLiveEvent<Triple<AppUpdateManager, AppUpdateInfo, Int>>()
    val installEvent = SingleLiveEvent<Unit>()

    val openDrawerEvent = SingleLiveEvent<Unit>()
    val closeDrawerEvent = SingleLiveEvent<Unit>()

    // События для связи проводника и редактора
    val openEvent = SingleLiveEvent<DocumentModel>()
    val propertiesEvent = SingleLiveEvent<FileModel>()

    val fullScreenMode: Boolean
        get() = settingsManager.fullScreenMode
    val confirmExit: Boolean
        get() = settingsManager.confirmExit

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus == InstallStatus.DOWNLOADED) {
            installEvent.call()
        }
    }

    fun checkForUpdates() {
        // TODO: 2020/8/5  Google Play is not available in Chinese mainland
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
}