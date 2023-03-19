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

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.blacksquircle.ui.core.extensions.isPermissionGranted
import com.blacksquircle.ui.core.extensions.shouldShowRequestDialog

class PermissionContract(
    private val fragment: Fragment,
    private val permission: String,
) : ActivityResultContract<String, PermissionResult>() {

    private val contract = ActivityResultContracts.RequestPermission()

    override fun createIntent(context: Context, input: String): Intent {
        return contract.createIntent(context, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PermissionResult {
        return when {
            contract.parseResult(resultCode, intent) -> PermissionResult.GRANTED
            else -> {
                val showRequestRationale = fragment.activity
                    ?.shouldShowRequestDialog(permission)
                    ?: return PermissionResult.DENIED
                if (!showRequestRationale) {
                    PermissionResult.DENIED_FOREVER
                } else {
                    PermissionResult.DENIED
                }
            }
        }
    }

    override fun getSynchronousResult(
        context: Context,
        input: String
    ): SynchronousResult<PermissionResult>? {
        return if (context.isPermissionGranted(input)) {
            SynchronousResult(PermissionResult.GRANTED)
        } else {
            null
        }
    }
}