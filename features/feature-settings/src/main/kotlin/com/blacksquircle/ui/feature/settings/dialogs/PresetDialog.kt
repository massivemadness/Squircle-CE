/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.feature.settings.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.settings.R
import com.blacksquircle.ui.feature.settings.viewmodel.SettingsViewModel

class PresetDialog : DialogFragment() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.label_keyboard_preset)
            customView(R.layout.dialog_preset)
            negativeButton(R.string.action_reset) {
                viewModel.resetKeyboardPreset()
            }
            positiveButton(R.string.action_save) {
                val inputEditText = getCustomView().findViewById<EditText>(R.id.input)
                val keyboardPreset = inputEditText.text.toString().trim()
                if (keyboardPreset.isNotEmpty()) {
                    viewModel.keyboardPreset = keyboardPreset
                }
            }

            setOnShowListener {
                val inputEditText = getCustomView().findViewById<EditText>(R.id.input)
                if (savedInstanceState == null) {
                    inputEditText.setText(viewModel.keyboardPreset)
                }
            }
        }
    }
}