/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.toReadableDate
import com.blacksquircle.ui.feature.explorer.data.utils.toReadableSize
import com.blacksquircle.ui.feature.explorer.databinding.DialogPropertiesBinding
import com.blacksquircle.ui.filesystem.base.model.PropertiesModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertiesDialog : DialogFragment() {

    private val navArgs by navArgs<PropertiesDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val propertiesModel = Gson().fromJson(navArgs.data, PropertiesModel::class.java) // FIXME
        val readableSize = propertiesModel.size.toReadableSize()
        val readableDate = propertiesModel.lastModified
            .toReadableDate(getString(R.string.properties_date_format))

        val properties = StringBuilder().apply {
            append(getString(R.string.properties_name, propertiesModel.name))
            append(getString(R.string.properties_path, propertiesModel.path))
            append(getString(R.string.properties_modified, readableDate))
            append(getString(R.string.properties_size, readableSize))
            propertiesModel.lines?.let { append(getString(R.string.properties_line_count, it)) }
            propertiesModel.words?.let { append(getString(R.string.properties_word_count, it)) }
            propertiesModel.chars?.let { append(getString(R.string.properties_char_count, it)) }
        }

        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_properties)
            message(text = properties) { html() }
            customView(R.layout.dialog_properties, scrollable = true)

            val binding = DialogPropertiesBinding.bind(getCustomView())

            binding.readable.isChecked = propertiesModel.readable
            binding.writable.isChecked = propertiesModel.writable
            binding.executable.isChecked = propertiesModel.executable
        }
    }
}