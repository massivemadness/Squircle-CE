/*
 * Copyright 2022 Squircle CE contributors.
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
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.blacksquircle.ui.feature.explorer.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestrictedDialog : DialogFragment() {

    private val navArgs by navArgs<RestrictedDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_storage_access)
            message(R.string.dialog_message_storage_access)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_continue) {
                val intent = Intent(navArgs.action).apply {
                    data = navArgs.data.toUri()
                }
                startActivity(intent)
            }
        }
    }
}