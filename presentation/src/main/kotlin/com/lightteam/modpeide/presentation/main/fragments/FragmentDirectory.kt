/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.modpeide.presentation.main.fragments

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.extensions.isValidFileName
import com.lightteam.modpeide.presentation.main.viewmodel.MainViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import com.lightteam.modpeide.databinding.FragmentDirectoryBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.presentation.main.adapters.FileAdapter
import com.lightteam.modpeide.presentation.main.adapters.interfaces.RecyclerSelection
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import com.lightteam.modpeide.domain.model.PropertiesModel
import com.lightteam.modpeide.utils.extensions.asHtml
import java.io.File

class FragmentDirectory : DaggerFragment(), RecyclerSelection {

    @Inject
    lateinit var viewModel: MainViewModel
    @Inject
    lateinit var adapter: FileAdapter

    private lateinit var binding: FragmentDirectoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_directory, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
    }

    override fun onClick(fileModel: FileModel) {
        if(fileModel.isFolder) {
            viewModel.fileTabsEvent.value = fileModel
        } else {
            viewModel.openDocument(fileModel)
        }
    }

    override fun onLongClick(fileModel: FileModel): Boolean {
        showChooseDialog(fileModel)
        return true
    }

    private fun setupObservers() {
        viewModel.fileListEvent.observe(viewLifecycleOwner, Observer { list ->
            adapter.setData(list)
        })
        viewModel.fileNotSupportedEvent.observe(viewLifecycleOwner, Observer { fileModel ->
            try { //Открытие файла через соответствующую программу
                context?.let {
                    val uri = FileProvider.getUriForFile(it, "${it.packageName}.provider", File(fileModel.path))
                    val mime = it.contentResolver.getType(uri)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, mime)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(intent)
                }
            } catch (e: ActivityNotFoundException) {
                viewModel.toastEvent.value = R.string.message_cannot_be_opened
            }
        })
        viewModel.propertiesEvent.observe(viewLifecycleOwner, Observer {
            showPropertiesDialog(it)
        })
    }

    private fun copyPath(fileModel: FileModel) {
        val clipboardManager = getSystemService(activity!!, ClipboardManager::class.java)
        val clip = ClipData.newPlainText("COPY", fileModel.path)
        clipboardManager?.primaryClip = clip
        viewModel.toastEvent.value = R.string.message_done
    }

    // region DIALOGS

    private fun showChooseDialog(fileModel: FileModel) {
        MaterialDialog(context!!).show {
            title(R.string.dialog_title_choose_action)
            customView(R.layout.dialog_file_action)

            val actionCopyPath = getCustomView().findViewById<View>(R.id.action_copy_path)
            val actionProperties = getCustomView().findViewById<View>(R.id.action_properties)
            val actionRename = getCustomView().findViewById<View>(R.id.action_rename)
            val actionDelete = getCustomView().findViewById<View>(R.id.action_delete)

            actionCopyPath.setOnClickListener {
                dismiss()
                copyPath(fileModel)
            }
            actionProperties.setOnClickListener {
                dismiss()
                viewModel.propertiesOf(fileModel)
            }
            actionRename.setOnClickListener {
                dismiss()
                showRenameDialog(fileModel)
            }
            actionDelete.setOnClickListener {
                dismiss()
                showDeleteDialog(fileModel)
            }
        }
    }

    private fun showPropertiesDialog(properties: PropertiesModel) {
        MaterialDialog(context!!).show {
            title(R.string.dialog_title_properties)
            message(text = (getString(R.string.properties_name).format(properties.name) +
                    getString(R.string.properties_path).format(properties.path) +
                    getString(R.string.properties_modified).format(properties.lastModified) +
                    getString(R.string.properties_size).format(properties.size) +
                    getString(R.string.properties_line_count).format(properties.lines) +
                    getString(R.string.properties_word_count).format(properties.words) +
                    getString(R.string.properties_char_count).format(properties.chars)).asHtml())
            customView(R.layout.dialog_properties)

            val readable = this.findViewById<CheckBox>(R.id.readable)
            val writable = this.findViewById<CheckBox>(R.id.writable)
            val executable = this.findViewById<CheckBox>(R.id.executable)

            readable.isChecked = properties.readable
            writable.isChecked = properties.writable
            executable.isChecked = properties.executable
        }
    }

    private fun showRenameDialog(fileModel: FileModel) {
        MaterialDialog(context!!).show {
            title(R.string.dialog_title_rename)
            input(
                waitForPositiveButton = false,
                hintRes = R.string.hint_enter_file_name,
                prefill = fileModel.name
            ) { dialog, text ->
                val inputField = dialog.getInputField()
                val isValid = text.toString().isValidFileName()

                inputField.error = if(isValid) {
                    null
                } else {
                    getString(R.string.message_invalid_file_name)
                }
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
            }
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_rename, click = {
                val fileName = getInputField().text.toString()
                viewModel.renameFile(fileModel, fileName)
            })
        }
    }

    private fun showDeleteDialog(fileModel: FileModel) {
        MaterialDialog(context!!).show {
            title(text = fileModel.name)
            message(R.string.dialog_message_delete)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_delete, click = {
                viewModel.deleteFile(fileModel)
            })
        }
    }

    // endregion DIALOGS
}