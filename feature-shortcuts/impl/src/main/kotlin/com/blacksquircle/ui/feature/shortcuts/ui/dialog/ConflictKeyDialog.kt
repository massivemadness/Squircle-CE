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

package com.blacksquircle.ui.feature.shortcuts.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.extensions.sendFragmentResult
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.feature.shortcuts.ui.fragment.ShortcutsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ConflictKeyDialog : DialogFragment() {

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    ConflictKeyScreen(
                        onReassignClicked = { reassign ->
                            sendFragmentResult(
                                resultKey = ShortcutsFragment.KEY_RESOLVE,
                                ShortcutsFragment.ARG_REASSIGN to reassign
                            )
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}