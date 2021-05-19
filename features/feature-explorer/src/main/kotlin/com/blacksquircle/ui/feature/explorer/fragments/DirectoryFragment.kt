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

package com.blacksquircle.ui.feature.explorer.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.DefaultSelectionTracker
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.data.converter.DocumentConverter
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.adapters.FileAdapter
import com.blacksquircle.ui.feature.explorer.databinding.DialogPropertiesBinding
import com.blacksquircle.ui.feature.explorer.databinding.FragmentDirectoryBinding
import com.blacksquircle.ui.feature.explorer.utils.*
import com.blacksquircle.ui.feature.explorer.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import com.blacksquircle.ui.filesystem.base.model.FileTree
import com.blacksquircle.ui.filesystem.base.model.FileType
import com.blacksquircle.ui.filesystem.base.model.PropertiesModel
import com.blacksquircle.ui.filesystem.base.utils.isValidFileName
import com.blacksquircle.ui.utils.adapters.OnItemClickListener
import com.blacksquircle.ui.utils.delegate.navController
import com.blacksquircle.ui.utils.delegate.viewBinding
import com.blacksquircle.ui.utils.extensions.*
import com.blacksquircle.ui.utils.interfaces.DrawerHandler
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException

@AndroidEntryPoint
class DirectoryFragment : Fragment(R.layout.fragment_directory), OnItemClickListener<FileModel> {

    private val viewModel: ExplorerViewModel by activityViewModels()
    private val binding: FragmentDirectoryBinding by viewBinding()
    private val navController: NavController by navController()
    private val navArgs: DirectoryFragmentArgs by navArgs()
    private val drawerHandler: DrawerHandler by lazy { activity as DrawerHandler }

    private lateinit var tracker: SelectionTracker<String>
    private lateinit var adapter: FileAdapter
    private lateinit var fileTree: FileTree

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        @SuppressLint("RestrictedApi")
        tracker = DefaultSelectionTracker(
            navArgs.path ?: "root",
            FileKeyProvider(binding.recyclerView),
            SelectionPredicates.createSelectAnything(),
            StorageStrategy.createStringStorage()
        ).also {
            it.addObserver(
                object : SelectionTracker.SelectionObserver<String>() {
                    override fun onSelectionChanged() {
                        viewModel.selectionEvent.value = adapter.getSelectedFiles(tracker.selection)
                    }
                }
            )
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = FileAdapter(
            selectionTracker = tracker,
            onItemClickListener = this,
            viewMode = viewModel.viewMode
        ).also {
            adapter = it
        }

        loadDirectory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.selectionEvent.value = emptyList()
    }

    override fun onClick(item: FileModel) {
        if (!tracker.hasSelection()) {
            if (item.isFolder) {
                val destination = DirectoryFragmentDirections.toDirectoryFragment(item.path)
                navController.navigate(destination)
            } else {
                if (item.getType() == FileType.ARCHIVE) {
                    viewModel.operation = Operation.EXTRACT
                    viewModel.tempFiles.replaceList(listOf(item))
                    viewModel.allowPasteFiles.value = false
                    executeOperation()
                } else {
                    val type = item.getType()
                    if (type == FileType.DEFAULT || type == FileType.TEXT) {
                        drawerHandler.closeDrawer()
                        viewModel.openFileEvent.value = DocumentConverter.toModel(item)
                    } else {
                        openAs(item)
                    }
                }
            }
        } else {
            val index = adapter.currentList.indexOf(item)
            if (tracker.isSelected(item.path)) {
                tracker.deselect(item.path)
            } else {
                tracker.select(item.path)
            }
            adapter.notifyItemChanged(index)
        }
    }

    override fun onLongClick(item: FileModel): Boolean {
        val index = adapter.currentList.indexOf(item)
        if (tracker.isSelected(item.path)) {
            tracker.deselect(item.path)
        } else {
            tracker.select(item.path)
        }
        adapter.notifyItemChanged(index)
        return true
    }

    private fun observeViewModel() {
        viewModel.loadingBar.observe(viewLifecycleOwner) {
            binding.loadingBar.isVisible = it
        }
        viewModel.emptyView.observe(viewLifecycleOwner) {
            binding.emptyViewImage.isVisible = it
            binding.emptyViewText.isVisible = it
        }
        viewModel.filesUpdateEvent.observe(viewLifecycleOwner) {
            loadDirectory()
        }
        viewModel.selectAllEvent.observe(viewLifecycleOwner) {
            tracker.setItemsSelected(adapter.currentList.map(FileModel::path), true)
            adapter.notifyDataSetChanged()
        }
        viewModel.deselectAllEvent.observe(viewLifecycleOwner) {
            tracker.clearSelection()
            adapter.notifyDataSetChanged()
        }
        viewModel.createEvent.observe(viewLifecycleOwner) {
            showCreateDialog()
        }
        viewModel.copyEvent.observe(viewLifecycleOwner) {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.operation = Operation.COPY
                viewModel.deselectAllEvent.call()
                viewModel.tempFiles.replaceList(it)
                viewModel.allowPasteFiles.value = true
            }
        }
        viewModel.deleteEvent.observe(viewLifecycleOwner) {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.deselectAllEvent.call()
                viewModel.tempFiles.replaceList(it)
                showDeleteDialog(it)
            }
        }
        viewModel.cutEvent.observe(viewLifecycleOwner) {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.operation = Operation.CUT
                viewModel.deselectAllEvent.call()
                viewModel.tempFiles.replaceList(it)
                viewModel.allowPasteFiles.value = true
            }
        }
        viewModel.pasteEvent.observe(viewLifecycleOwner) {
            executeOperation()
        }
        viewModel.openAsEvent.observe(viewLifecycleOwner) {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                openAs(it)
            }
        }
        viewModel.renameEvent.observe(viewLifecycleOwner) {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                showRenameDialog(it)
            }
        }
        viewModel.propertiesEvent.observe(viewLifecycleOwner) {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                viewModel.propertiesOf(it)
            }
        }
        viewModel.copyPathEvent.observe(viewLifecycleOwner) {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                copyPath(it)
            }
        }
        viewModel.archiveEvent.observe(viewLifecycleOwner) {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.operation = Operation.COMPRESS
                viewModel.deselectAllEvent.call()
                viewModel.tempFiles.replaceList(it)
                if (it.size > 1) {
                    MaterialDialog(requireContext()).show {
                        title(R.string.dialog_title_archive_name)
                        customView(R.layout.dialog_archive)

                        val fileNameInput = getCustomView()
                            .findViewById<TextInputEditText>(R.id.input)

                        negativeButton(R.string.action_cancel)
                        positiveButton(R.string.action_create_zip) {
                            val fileName = fileNameInput.text.toString()
                            val isValid = fileName.isValidFileName()
                            if (isValid) {
                                executeOperation(fileName)
                            } else {
                                context.showToast(R.string.message_invalid_file_name)
                            }
                        }
                    }
                } else {
                    executeOperation()
                }
            }
        }

        viewModel.filesEvent.observe(viewLifecycleOwner) {
            fileTree = it
            adapter.submitList(fileTree.children)
        }
        viewModel.searchEvent.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.clickEvent.observe(viewLifecycleOwner, ::onClick)
        viewModel.propertiesOfEvent.observe(viewLifecycleOwner) {
            showPropertiesDialog(it)
        }

        viewModel.openPropertiesEvent.observe(viewLifecycleOwner) {
            viewModel.propertiesOf(it)
        }
    }

    private fun loadDirectory() {
        viewModel.provideDirectory(navArgs.path)
    }

    private fun openAs(fileModel: FileModel) {
        try {
            val file = File(fileModel.path)
            if (!file.exists()) {
                throw FileNotFoundException(file.path)
            }

            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${context?.packageName}.provider",
                file
            )

            val mime = context?.contentResolver?.getType(uri)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, mime)
            }
            startActivity(intent)
        } catch (e: Exception) {
            context?.showToast(R.string.message_cannot_be_opened)
        }
    }

    private fun copyPath(fileModel: FileModel) {
        fileModel.path.clipText(context)
        context?.showToast(R.string.message_done)
    }

    // region DIALOGS

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
                    context.showToast(R.string.message_invalid_file_name)
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
                    context.showToast(R.string.message_invalid_file_name)
                }
            }
        }
    }

    private fun showDeleteDialog(fileModels: List<FileModel>) {
        val isMultiDelete = fileModels.size > 1

        val dialogTitle = if (isMultiDelete) {
            getString(R.string.dialog_title_multi_delete)
        } else fileModels.first().name

        val dialogMessage = if (isMultiDelete) {
            R.string.dialog_message_multi_delete
        } else R.string.dialog_message_delete

        MaterialDialog(requireContext()).show {
            title(text = dialogTitle)
            message(dialogMessage)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_delete) {
                viewModel.operation = Operation.DELETE
                executeOperation()
            }
        }
    }

    private fun showPropertiesDialog(propertiesModel: PropertiesModel) {
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

        MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_properties)
            message(text = properties) { html() }
            customView(R.layout.dialog_properties, scrollable = true)

            val binding = DialogPropertiesBinding.bind(getCustomView())

            binding.readable.isChecked = propertiesModel.readable
            binding.writable.isChecked = propertiesModel.writable
            binding.executable.isChecked = propertiesModel.executable
        }
    }

    private fun executeOperation(archiveName: String? = null) {
        val destination = DirectoryFragmentDirections.toProgressDialog(
            parentPath = fileTree.parent.path,
            archiveName = archiveName
        )
        navController.navigate(destination)
    }

    // endregion DIALOGS
}