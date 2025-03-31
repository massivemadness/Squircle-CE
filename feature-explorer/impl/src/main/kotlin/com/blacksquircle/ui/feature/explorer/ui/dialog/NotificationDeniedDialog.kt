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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.extensions.showToast
import com.blacksquircle.ui.ds.SquircleTheme
import timber.log.Timber
import com.blacksquircle.ui.ds.R as UiR

internal class NotificationDeniedDialog : DialogFragment() {

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = content {
        SquircleTheme {
            NotificationDeniedScreen(
                onConfirmClicked = {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:${requireContext().packageName}")
                        }
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Timber.e(e, e.message)
                        context?.showToast(UiR.string.common_error_occurred)
                    }
                    navController.popBackStack()
                },
                onCancelClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}