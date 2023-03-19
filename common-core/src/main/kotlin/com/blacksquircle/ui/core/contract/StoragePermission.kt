/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.core.contract

import android.Manifest
import android.os.Build
import android.os.Environment
import androidx.fragment.app.Fragment

class StoragePermission(
    fragment: Fragment,
    private val onResult: (PermissionResult) -> Unit,
) {

    private val permission: String
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

    private val requestPermission = fragment.registerForActivityResult(
        PermissionContract(fragment, permission)
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                onResult(PermissionResult.GRANTED)
            } else {
                onResult(PermissionResult.DENIED_FOREVER)
            }
        } else {
            onResult(result)
        }
    }

    fun launch() {
        requestPermission.launch(permission)
    }
}