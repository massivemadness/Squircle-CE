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

import android.graphics.Color
import androidx.fragment.app.activityViewModels
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.mvi.EditorIntent
import com.blacksquircle.ui.feature.editor.ui.viewmodel.EditorViewModel
import com.blacksquircle.ui.uikit.ColorPickerDialog
import com.blacksquircle.ui.uikit.extensions.toHexString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsertColorDialog : ColorPickerDialog() {

    override val titleRes = R.string.dialog_title_color_picker
    override val positiveRes = R.string.action_insert
    override val negativeRes = android.R.string.cancel
    override val initialColor = Color.WHITE.toHexString()

    private val viewModel by activityViewModels<EditorViewModel>()

    override fun onColorSelected(color: Int) {
        viewModel.obtainEvent(EditorIntent.InsertColor(color))
    }
}