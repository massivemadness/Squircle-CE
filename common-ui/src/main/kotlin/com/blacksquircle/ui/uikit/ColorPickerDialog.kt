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

package com.blacksquircle.ui.uikit

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.blacksquircle.ui.uikit.databinding.DialogColorPickerBinding
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode

abstract class ColorPickerDialog : DialogFragment() {

    abstract val titleRes: Int
    abstract val positiveRes: Int
    abstract val negativeRes: Int
    abstract val initialColor: Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogColorPickerBinding.inflate(layoutInflater)
        binding.colorPicker.setInitialColor(initialColor)
        binding.colorPicker.attachAlphaSlider(binding.alphaSlideBar)
        binding.colorPicker.attachBrightnessSlider(binding.brightnessSlideBar)
        binding.colorPicker.flagView = BubbleFlag(requireContext()).apply {
            flagMode = FlagMode.FADE
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(titleRes)
            .setView(binding.root)
            .setPositiveButton(positiveRes) { _, _ -> onColorSelected(binding.colorPicker.color) }
            .setNegativeButton(negativeRes, null)
            .create()
    }

    abstract fun onColorSelected(color: Int)
}