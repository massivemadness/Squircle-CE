/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.utils.inappupdate

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.*

class InAppUpdateImpl(context: Context) : InAppUpdate {

    companion object {

        private const val TAG = "InAppUpdateImpl"

        private const val PRIORITY_HIGH = 5
        private const val PRIORITY_M_HIGH = 4
        private const val PRIORITY_MEDIUM = 3
        private const val PRIORITY_M_LOW = 2
        private const val PRIORITY_LOW = 1

        private const val UPDATE_REQUEST_CODE = 500
    }

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(context) }

    override fun checkForUpdates(activity: Activity, onComplete: () -> Unit) {
        appUpdateManager.registerListener(object : InstallStateUpdatedListener {
            override fun onStateUpdate(state: InstallState) {
                if (state.installStatus == InstallStatus.DOWNLOADED) {
                    appUpdateManager.unregisterListener(this)
                    onComplete.invoke()
                }
            }
        })
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    val clientStalenessDays = info.clientVersionStalenessDays ?: 0
                    when (info.updatePriority) {
                        PRIORITY_HIGH -> {
                            startUpdate(activity, info, AppUpdateType.IMMEDIATE)
                        }
                        PRIORITY_M_HIGH -> {
                            if (clientStalenessDays >= 5 && info.isImmediateUpdateAllowed) {
                                startUpdate(activity, info, AppUpdateType.IMMEDIATE)
                            } else if (info.isFlexibleUpdateAllowed) {
                                startUpdate(activity, info, AppUpdateType.FLEXIBLE)
                            }
                        }
                        PRIORITY_MEDIUM -> {
                            if (clientStalenessDays >= 30 && info.isImmediateUpdateAllowed) {
                                startUpdate(activity, info, AppUpdateType.IMMEDIATE)
                            } else if (info.isFlexibleUpdateAllowed) {
                                startUpdate(activity, info, AppUpdateType.FLEXIBLE)
                            }
                        }
                        PRIORITY_M_LOW -> {
                            if (clientStalenessDays >= 90 && info.isImmediateUpdateAllowed) {
                                startUpdate(activity, info, AppUpdateType.IMMEDIATE)
                            } else if (info.isFlexibleUpdateAllowed) {
                                startUpdate(activity, info, AppUpdateType.FLEXIBLE)
                            }
                        }
                        PRIORITY_LOW -> {
                            startUpdate(activity, info, AppUpdateType.FLEXIBLE)
                        }
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message, it)
            }
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun startUpdate(activity: Activity, info: AppUpdateInfo, type: Int) {
        appUpdateManager.startUpdateFlowForResult(info, type, activity, UPDATE_REQUEST_CODE)
    }
}