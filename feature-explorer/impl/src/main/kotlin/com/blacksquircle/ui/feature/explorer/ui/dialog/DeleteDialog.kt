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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.explorer.ui.fragment.ExplorerFragment

internal class DeleteDialog : DialogFragment() {

    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<DeleteDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = content {
        SquircleTheme {
            DeleteScreen(
                fileName = navArgs.fileName,
                fileCount = navArgs.fileCount,
                onConfirmClicked = {
                    sendNavigationResult(
                        key = ExplorerFragment.KEY_DELETE_FILE,
                        result = Bundle.EMPTY,
                    )
                    navController.popBackStack()
                },
                onCancelClicked = {
                    navController.popBackStack()
                }
            )
        }
    }

    companion object {
        const val ARG_FILE_NAME = "fileName"
        const val ARG_FILE_COUNT = "fileCount"
    }
}