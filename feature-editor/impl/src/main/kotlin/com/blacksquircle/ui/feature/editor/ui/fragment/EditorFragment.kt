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

package com.blacksquircle.ui.feature.editor.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.blacksquircle.ui.core.extensions.observeFragmentResult
import com.blacksquircle.ui.ds.SquircleTheme

internal class EditorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SquircleTheme {
                    EditorScreen(navController = findNavController())
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeFragmentResult(KEY_CLOSE_MODIFIED) { bundle ->
            /*val position = bundle.getInt(ARG_POSITION)
            viewModel.obtainEvent(EditorIntent.CloseTab(position, true))*/
        }
        observeFragmentResult(KEY_SELECT_LANGUAGE) { bundle ->
            /*val language = bundle.getString(ARG_LANGUAGE).orEmpty()
            viewModel.obtainEvent(EditorIntent.SelectLanguage(language))*/
        }
        observeFragmentResult(KEY_GOTO_LINE) { bundle ->
            /*val lineNumber = bundle.getInt(ARG_LINE_NUMBER)
            viewModel.obtainEvent(EditorIntent.GotoLineNumber(lineNumber))*/
        }
        observeFragmentResult(KEY_INSERT_COLOR) { bundle ->
            /*val color = bundle.getInt(ARG_COLOR)
            viewModel.obtainEvent(EditorIntent.InsertColor(color))*/
        }
    }

    companion object {

        const val KEY_CLOSE_MODIFIED = "KEY_CLOSE_TAB"
        const val KEY_SELECT_LANGUAGE = "KEY_SELECT_LANGUAGE"
        const val KEY_GOTO_LINE = "KEY_GOTO_LINE"
        const val KEY_INSERT_COLOR = "KEY_INSERT_COLOR"

        const val ARG_POSITION = "ARG_POSITION"
        const val ARG_LANGUAGE = "ARG_LANGUAGE"
        const val ARG_LINE_NUMBER = "ARG_LINE_NUMBER"
        const val ARG_COLOR = "ARG_COLOR"
    }
}