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

package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.toReadableDate
import com.blacksquircle.ui.feature.explorer.data.utils.toReadableSize
import com.blacksquircle.ui.feature.explorer.databinding.DialogPropertiesBinding
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.Permission
import com.blacksquircle.ui.filesystem.base.utils.hasFlag
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertiesDialog : DialogFragment() {

    private val navArgs by navArgs<PropertiesDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val fileModel = Gson().fromJson(navArgs.data, FileModel::class.java) // FIXME

        val readableSize = fileModel.size.toReadableSize()
        val readableDate = fileModel.lastModified
            .toReadableDate(getString(R.string.properties_date_format))

        val binding = DialogPropertiesBinding.inflate(layoutInflater)

        binding.textFileName.setText(fileModel.name)
        binding.textFilePath.setText(fileModel.path)
        binding.textLastModified.setText(readableDate)
        binding.textFileSize.setText(readableSize)

        binding.readable.isChecked = fileModel.permission hasFlag Permission.OWNER_READ
        binding.writable.isChecked = fileModel.permission hasFlag Permission.OWNER_WRITE
        binding.executable.isChecked = fileModel.permission hasFlag Permission.OWNER_EXECUTE

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_properties)
            .setView(binding.root)
            .create()
    }
}