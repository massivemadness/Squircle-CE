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

package com.lightteam.modpeide.ui.explorer.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.converter.DocumentConverter
import com.lightteam.modpeide.data.utils.extensions.isValidFileName
import com.lightteam.modpeide.databinding.FragmentDirectoryBinding
import com.lightteam.modpeide.domain.model.FileModel
import com.lightteam.modpeide.domain.model.FileTree
import com.lightteam.modpeide.domain.model.PropertiesModel
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.editor.viewmodel.EditorViewModel
import com.lightteam.modpeide.ui.explorer.adapters.FileAdapter
import com.lightteam.modpeide.ui.explorer.adapters.interfaces.ItemCallback
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.utils.extensions.asHtml
import com.lightteam.modpeide.utils.extensions.clipText
import javax.inject.Inject

class DirectoryFragment : BaseFragment(), ItemCallback<FileModel> {

    @Inject
    lateinit var viewModel: ExplorerViewModel
    @Inject
    lateinit var editorViewModel: EditorViewModel
    @Inject
    lateinit var adapter: FileAdapter

    private val args: DirectoryFragmentArgs by navArgs()

    private lateinit var binding: FragmentDirectoryBinding
    private lateinit var navController: NavController
    private lateinit var fileTree: FileTree

    override fun layoutId(): Int = R.layout.fragment_directory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        navController = findNavController()
        observeViewModel()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        loadDirectory()
    }

    override fun onClick(item: FileModel) {
        if (item.isFolder) {
            val action = DirectoryFragmentDirections.toDirectoryFragment(item)
            navController.navigate(action)
        } else {
            editorViewModel.openFile(DocumentConverter.toModel(item))
        }
    }

    override fun onLongClick(item: FileModel): Boolean {
        showChooseDialog(item)
        return true
    }

    private fun observeViewModel() {
        viewModel.filesEvent.observe(viewLifecycleOwner, Observer {
            fileTree = it
            adapter.submitList(fileTree.children)
        })
        viewModel.filesUpdateEvent.observe(viewLifecycleOwner, Observer {
            loadDirectory()
        })
        viewModel.searchEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.fabEvent.observe(viewLifecycleOwner, Observer {
            showCreateDialog()
        })
        viewModel.createEvent.observe(viewLifecycleOwner, Observer {
            onClick(it)
        })
        viewModel.propertiesEvent.observe(viewLifecycleOwner, Observer {
            showPropertiesDialog(it)
        })
    }

    private fun loadDirectory() {
        viewModel.provideDirectory(args.fileModel)
    }

    private fun copyPath(fileModel: FileModel) {
        fileModel.path.clipText(context)
        viewModel.toastEvent.value = R.string.message_done
    }

    // region DIALOGS

    private fun showChooseDialog(fileModel: FileModel) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_choose_action)
            customView(R.layout.dialog_file_action)

            val actionCopyPath = getCustomView().findViewById<View>(R.id.action_copy_path)
            val actionProperties = getCustomView().findViewById<View>(R.id.action_properties)
            val actionRename = getCustomView().findViewById<View>(R.id.action_rename)
            val actionDelete = getCustomView().findViewById<View>(R.id.action_delete)

            actionCopyPath.setOnClickListener {
                dismiss()
                copyPath(fileModel) // copy path
            }
            actionProperties.setOnClickListener {
                dismiss()
                viewModel.propertiesOf(fileModel) // properties
            }
            actionRename.setOnClickListener {
                dismiss()
                showRenameDialog(fileModel) // rename file
            }
            actionDelete.setOnClickListener {
                dismiss()
                showDeleteDialog(fileModel) // delete file
            }
        }
    }

    private fun showCreateDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_create)
            input(
                waitForPositiveButton = false,
                hintRes = R.string.hint_enter_file_name
            ) { dialog, text ->
                val inputField = dialog.getInputField()
                val isValid = text.toString().isValidFileName()

                inputField.error = if (isValid) {
                    null
                } else {
                    getString(R.string.message_invalid_file_name)
                }
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
            }
            checkBoxPrompt(R.string.action_folder) {}
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_create) {
                val fileName = getInputField().text.toString()
                val isFolder = getCheckBoxPrompt().isChecked

                val parent = fileTree.parent
                val child = parent.copy(
                    path = parent.path + "/$fileName",
                    isFolder = isFolder
                )
                viewModel.createFile(child)
            }
        }
    }

    private fun showRenameDialog(fileModel: FileModel) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_rename)
            input(
                waitForPositiveButton = false,
                hintRes = R.string.hint_enter_file_name,
                prefill = fileModel.name
            ) { dialog, text ->
                val inputField = dialog.getInputField()
                val isValid = text.toString().isValidFileName()

                inputField.error = if (isValid) {
                    null
                } else {
                    getString(R.string.message_invalid_file_name)
                }
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
            }
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_rename) {
                val fileName = getInputField().text.toString()
                viewModel.renameFile(fileModel, fileName)
            }
        }
    }

    private fun showDeleteDialog(fileModel: FileModel) {
        MaterialDialog(requireContext()).show {
            title(text = fileModel.name)
            message(R.string.dialog_message_delete)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_delete) {
                viewModel.deleteFile(fileModel)
            }
        }
    }

    private fun showPropertiesDialog(propertiesModel: PropertiesModel) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_properties)
            message(text = (
                    getString(R.string.properties_name).format(propertiesModel.name) +
                    getString(R.string.properties_path).format(propertiesModel.path) +
                    getString(R.string.properties_modified).format(propertiesModel.lastModified) +
                    getString(R.string.properties_size).format(propertiesModel.size) +
                    getString(R.string.properties_line_count).format(propertiesModel.lines) +
                    getString(R.string.properties_word_count).format(propertiesModel.words) +
                    getString(R.string.properties_char_count).format(propertiesModel.chars)).asHtml()
            )
            customView(R.layout.dialog_properties)

            val readable = this.findViewById<CheckBox>(R.id.readable)
            val writable = this.findViewById<CheckBox>(R.id.writable)
            val executable = this.findViewById<CheckBox>(R.id.executable)

            readable.isChecked = propertiesModel.readable
            writable.isChecked = propertiesModel.writable
            executable.isChecked = propertiesModel.executable
        }
    }

    // endregion DIALOGS
}