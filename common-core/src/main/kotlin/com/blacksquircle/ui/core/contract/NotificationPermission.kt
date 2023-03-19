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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

class NotificationPermission(
    fragment: Fragment,
    private val onResult: (PermissionResult) -> Unit,
) {

    private val permission: String
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        get() = Manifest.permission.POST_NOTIFICATIONS

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermission = fragment.registerForActivityResult(
        PermissionContract(fragment, permission)
    ) { result ->
        onResult(result)
    }

    fun launch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission.launch(permission)
        } else {
            onResult(PermissionResult.GRANTED)
        }
    }
}