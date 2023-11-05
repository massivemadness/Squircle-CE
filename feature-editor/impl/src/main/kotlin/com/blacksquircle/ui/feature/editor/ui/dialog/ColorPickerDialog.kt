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

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ColorPickerDialog : DialogFragment() {

    private val viewModel by activityViewModels<EditorViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ColorPickerDialog.Builder(requireContext()).apply {
            setTitle(R.string.dialog_title_color_picker)
            setPositiveButton(R.string.action_insert, ColorEnvelopeListener { envelope, _ ->
                viewModel.obtainEvent(EditorIntent.InsertColor(envelope.color))
            })
            setNegativeButton(android.R.string.cancel, null)
            attachAlphaSlideBar(false)
            attachBrightnessSlideBar(true)

            colorPickerView.flagView = BubbleFlag(requireContext()).apply {
                flagMode = FlagMode.FADE
            }
        }.create()
    }
}