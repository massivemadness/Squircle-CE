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

package com.lightteam.modpeide.ui.explorer.contracts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.*

class StoragePermission : ActivityResultContract<Boolean, Unit>() {

    private var shouldUseSettingsActivity = false

    override fun createIntent(context: Context, input: Boolean?): Intent {
        shouldUseSettingsActivity = input ?: false
        return if (shouldUseSettingsActivity) {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        } else {
            Intent(ACTION_REQUEST_PERMISSIONS).apply {
                putExtra(EXTRA_PERMISSIONS, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }
    }

    /**
     * We'll check permissions in the activity or fragment
     */
    override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}