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

package com.blacksquircle.ui.feature.themes.ui.dialog

import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.feature.themes.R
import com.blacksquircle.ui.feature.themes.ui.mvi.ThemeIntent
import com.blacksquircle.ui.feature.themes.ui.viewmodel.ThemesViewModel
import com.blacksquircle.ui.uikit.ColorPickerDialog
import com.blacksquircle.ui.uikit.extensions.toHexString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseColorDialog : ColorPickerDialog() {

    override val titleRes = R.string.dialog_title_color_picker
    override val positiveRes = R.string.action_select
    override val negativeRes = android.R.string.cancel
    override val initialColor: String
        get() = navArgs.value

    private val viewModel by hiltNavGraphViewModels<ThemesViewModel>(R.id.themes_graph)
    private val navArgs by navArgs<ChooseColorDialogArgs>()

    override fun onColorSelected(color: Int) {
        val event = ThemeIntent.ChangeColor(
            key = navArgs.key,
            value = color.toHexString()
        )
        viewModel.obtainEvent(event)
    }
}