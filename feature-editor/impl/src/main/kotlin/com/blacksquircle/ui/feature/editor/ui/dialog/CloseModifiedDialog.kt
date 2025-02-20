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

package com.blacksquircle.ui.feature.editor.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.extensions.sendFragmentResult
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorFragment

internal class CloseModifiedDialog : DialogFragment() {

    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<CloseModifiedDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    CloseModifiedScreen(
                        fileName = navArgs.fileName,
                        onConfirmClicked = {
                            sendFragmentResult(
                                resultKey = EditorFragment.KEY_CLOSE_MODIFIED,
                                bundle = bundleOf(
                                    EditorFragment.ARG_POSITION to navArgs.position,
                                )
                            )
                        },
                        onCancelClicked = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val ARG_FILE_NAME = "fileName"
        const val ARG_POSITION = "position"
    }
}