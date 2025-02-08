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
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.blacksquircle.ui.ds.dialog.ColorPickerDialog
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.ds.R as UiR

@AndroidEntryPoint
class InsertColorDialog : DialogFragment() {

    private val viewModel by activityViewModels<EditorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ColorPickerDialog(
                    title = stringResource(UiR.string.dialog_title_color_picker),
                    confirmButton = stringResource(R.string.action_insert),
                    dismissButton = stringResource(android.R.string.cancel),
                    onColorSelected = { color ->
                        val colorInt = color.toHexString().toColorInt()
                        viewModel.obtainEvent(EditorIntent.InsertColor(colorInt))
                        dismiss()
                    },
                    onDismissClicked = { dismiss() },
                    onDismiss = { dismiss() },
                )
            }
        }
    }
}