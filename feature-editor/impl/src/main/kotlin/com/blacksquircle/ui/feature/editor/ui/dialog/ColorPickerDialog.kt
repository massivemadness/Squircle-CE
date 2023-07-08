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

package com.blacksquircle.ui.feature.editor.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ColorPickerDialog : DialogFragment() {

    private val viewModel by activityViewModels<EditorViewModel>()

    @SuppressLint("CheckResult")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_color_picker)
            colorChooser(
                colors = ColorPalette.Primary,
                subColors = ColorPalette.PrimarySub,
                allowCustomArgb = true,
                showAlphaSelector = true,
            ) { _, color ->
                viewModel.obtainEvent(EditorIntent.InsertColor(color))
            }
            positiveButton(R.string.action_insert)
            negativeButton(android.R.string.cancel)
        }
    }
}