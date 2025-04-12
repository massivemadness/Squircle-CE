/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.internal.inappupdate

import android.app.Activity
import com.blacksquircle.ui.internal.provider.inappupdate.InAppUpdate
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import timber.log.Timber

internal class InAppUpdateImpl(
    private val activity: Activity,
) : InAppUpdate {

    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(activity)
    }

    private var appUpdateInfo: AppUpdateInfo? = null

    override fun checkForUpdates(onUpdateAvailable: () -> Unit) {
        Timber.d("checkForUpdates")
        if (appUpdateInfo != null) {
            return
        }
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    this.appUpdateInfo = appUpdateInfo
                    onUpdateAvailable()
                }
            }
            .addOnFailureListener { e ->
                Timber.e(e, e.message)
            }
    }

    override fun installUpdate() {
        Timber.d("installUpdate")
        appUpdateInfo?.let { appUpdateInfo ->
            appUpdateManager.startUpdateFlow(
                appUpdateInfo,
                activity,
                AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
            )
        }
    }
}