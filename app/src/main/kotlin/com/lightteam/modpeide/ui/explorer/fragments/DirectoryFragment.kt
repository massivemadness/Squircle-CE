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
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputEditText
import com.lightteam.filesystem.model.FileModel
import com.lightteam.filesystem.model.FileTree
import com.lightteam.filesystem.model.PropertiesModel
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.extensions.isValidFileName
import com.lightteam.modpeide.databinding.FragmentDirectoryBinding
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.explorer.adapters.FileAdapter
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import com.lightteam.modpeide.utils.extensions.asHtml
import com.lightteam.modpeide.utils.extensions.clipText
import javax.inject.Inject

class DirectoryFragment : BaseFragment(), OnItemClickListener<FileModel> {

    @Inject
    lateinit var sharedViewModel: MainViewModel
    @Inject
    lateinit var viewModel: ExplorerViewModel

    private val args: DirectoryFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentDirectoryBinding
    private lateinit var adapter: FileAdapter
    private lateinit var fileTree: FileTree

    override fun layoutId(): Int = R.layout.fragment_directory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        observeViewModel()

        navController = findNavController()
        adapter = FileAdapter(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        loadDirectory()
    }

    override fun onClick(item: FileModel) {
        if (item.isFolder) {
            val destination = DirectoryFragmentDirections.toDirectoryFragment(item)
            navController.navigate(destination)
        } else {
            sharedViewModel.openFileEvent.value = item
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
            customView(R.layout.dialog_file_action, scrollable = true)

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

    private fun showCreateDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_create)
            customView(R.layout.dialog_create)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_create) {
                val fileName = getCustomView()
                    .findViewById<TextInputEditText>(R.id.input).text.toString()
                val isFolder = getCustomView()
                    .findViewById<CheckBox>(R.id.box_isFolder).isChecked
                val isValid = fileName.isValidFileName()
                if (isValid) {
                    val parent = fileTree.parent
                    val child = parent.copy(
                        path = parent.path + "/$fileName",
                        isFolder = isFolder
                    )
                    viewModel.createFile(child)
                } else {
                    viewModel.toastEvent.value = R.string.message_invalid_file_name
                }
            }
        }
    }

    private fun showRenameDialog(fileModel: FileModel) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_rename)
            customView(R.layout.dialog_rename)

            val fileNameInput = getCustomView()
                .findViewById<TextInputEditText>(R.id.input)
            fileNameInput.setText(fileModel.name)

            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_rename) {
                val fileName = fileNameInput.text.toString()
                val isValid = fileName.isValidFileName()
                if (isValid) {
                    viewModel.renameFile(fileModel, fileName)
                } else {
                    viewModel.toastEvent.value = R.string.message_invalid_file_name
                }
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
            customView(R.layout.dialog_properties, scrollable = true)

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