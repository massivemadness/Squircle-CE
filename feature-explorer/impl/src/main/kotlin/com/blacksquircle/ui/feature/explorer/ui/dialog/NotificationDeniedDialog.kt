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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.feature.explorer.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.blacksquircle.ui.ds.R as UiR

@AndroidEntryPoint
internal class NotificationDeniedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_notification_permission)
            .setMessage(R.string.dialog_message_notification_permission)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(UiR.string.common_continue) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${requireContext().packageName}")
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Timber.d(e, e.message)
                    requireContext().showToast(UiR.string.common_error_occurred)
                }
            }
            .create()
    }
}