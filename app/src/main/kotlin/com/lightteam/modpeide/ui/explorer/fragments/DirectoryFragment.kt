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

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.DefaultSelectionTracker
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.progressindicator.ProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.lightteam.filesystem.model.FileModel
import com.lightteam.filesystem.model.FileTree
import com.lightteam.filesystem.model.PropertiesModel
import com.lightteam.localfilesystem.utils.isValidFileName
import com.lightteam.modpeide.R
import com.lightteam.modpeide.data.utils.extensions.replaceList
import com.lightteam.modpeide.databinding.FragmentDirectoryBinding
import com.lightteam.modpeide.ui.base.adapters.OnItemClickListener
import com.lightteam.modpeide.ui.base.fragments.BaseFragment
import com.lightteam.modpeide.ui.explorer.adapters.FileAdapter
import com.lightteam.modpeide.ui.explorer.utils.FileKeyProvider
import com.lightteam.modpeide.ui.explorer.utils.Operation
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import com.lightteam.modpeide.ui.main.viewmodel.MainViewModel
import com.lightteam.modpeide.utils.extensions.clipText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DirectoryFragment : BaseFragment(R.layout.fragment_directory), OnItemClickListener<FileModel> {

    @Inject
    lateinit var sharedViewModel: MainViewModel
    @Inject
    lateinit var viewModel: ExplorerViewModel

    private val args: DirectoryFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var binding: FragmentDirectoryBinding

    private lateinit var tracker: SelectionTracker<FileModel>
    private lateinit var adapter: FileAdapter
    private lateinit var fileTree: FileTree

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        observeViewModel()

        navController = findNavController()

        @SuppressLint("RestrictedApi")
        tracker = DefaultSelectionTracker(
            args.fileModel?.path ?: "root",
            FileKeyProvider(binding.recyclerView),
            SelectionPredicates.createSelectAnything(),
            StorageStrategy.createParcelableStorage(FileModel::class.java)
        ).also {
            it.addObserver(
                object : SelectionTracker.SelectionObserver<FileModel>() {
                    override fun onSelectionChanged() {
                        viewModel.selectionEvent.value = tracker.selection.toList()
                    }
                }
            )
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = FileAdapter(
            selectionTracker = tracker,
            onItemClickListener = this,
            viewMode = viewModel.viewMode
        ).also { adapter = it }

        loadDirectory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.selectionEvent.value = emptyList()
    }

    override fun onClick(item: FileModel) {
        if (!tracker.hasSelection()) {
            if (item.isFolder) {
                val destination = DirectoryFragmentDirections.toDirectoryFragment(item)
                navController.navigate(destination)
            } else {
                sharedViewModel.openEvent.value = item
            }
        } else {
            val index = adapter.currentList.indexOf(item)
            if (tracker.isSelected(item)) {
                tracker.deselect(item)
            } else {
                tracker.select(item)
            }
            adapter.notifyItemChanged(index)
        }
    }

    override fun onLongClick(item: FileModel): Boolean {
        val index = adapter.currentList.indexOf(item)
        if (tracker.isSelected(item)) {
            tracker.deselect(item)
        } else {
            tracker.select(item)
        }
        adapter.notifyItemChanged(index)
        return true
    }

    private fun observeViewModel() {
        viewModel.filesUpdateEvent.observe(viewLifecycleOwner, Observer {
            loadDirectory()
        })
        viewModel.selectAllEvent.observe(viewLifecycleOwner, Observer {
            tracker.setItemsSelected(adapter.currentList, true)
            adapter.notifyDataSetChanged()
        })
        viewModel.deselectAllEvent.observe(viewLifecycleOwner, Observer {
            tracker.clearSelection()
            adapter.notifyDataSetChanged()
        })
        viewModel.createEvent.observe(viewLifecycleOwner, Observer {
            showCreateDialog()
        })
        viewModel.copyEvent.observe(viewLifecycleOwner, Observer {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.deselectAllEvent.call()
                viewModel.filesToCopy.replaceList(it)
                viewModel.allowPasteFiles.set(true)
            }
        })
        viewModel.deleteEvent.observe(viewLifecycleOwner, Observer {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.deselectAllEvent.call()
                showDeleteDialog(it)
            }
        })
        viewModel.cutEvent.observe(viewLifecycleOwner, Observer {
            val fileModels = viewModel.selectionEvent.value
            fileModels?.let {
                viewModel.deselectAllEvent.call()
                viewModel.filesToCopy.replaceList(it)
                viewModel.allowPasteFiles.set(true)
            }
        })
        viewModel.pasteEvent.observe(viewLifecycleOwner, Observer {
            executeProcess(viewModel.filesToCopy, it) {
                viewModel.allowPasteFiles.set(false)
                viewModel.filesToCopy.clear()
            }
        })
        viewModel.openAsEvent.observe(viewLifecycleOwner, Observer {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                openAs(it)
            }
        })
        viewModel.renameEvent.observe(viewLifecycleOwner, Observer {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                showRenameDialog(it)
            }
        })
        viewModel.propertiesEvent.observe(viewLifecycleOwner, Observer {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                viewModel.propertiesOf(it)
            }
        })
        viewModel.copyPathEvent.observe(viewLifecycleOwner, Observer {
            val fileModel = viewModel.selectionEvent.value?.first()
            fileModel?.let {
                viewModel.deselectAllEvent.call()
                copyPath(it)
            }
        })

        viewModel.filesEvent.observe(viewLifecycleOwner, Observer {
            fileTree = it
            adapter.submitList(fileTree.children)
        })
        viewModel.searchEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.clickEvent.observe(viewLifecycleOwner, Observer {
            onClick(it) // select file
        })
        viewModel.propertiesOfEvent.observe(viewLifecycleOwner, Observer {
            showPropertiesDialog(it)
        })

        sharedViewModel.openAsEvent.observe(viewLifecycleOwner, Observer {
            openAs(it)
        })
        sharedViewModel.propertiesEvent.observe(viewLifecycleOwner, Observer {
            viewModel.propertiesOf(it)
        })
    }

    private fun loadDirectory() {
        viewModel.provideDirectory(args.fileModel)
    }

    private fun openAs(fileModel: FileModel) {
        try { // Открытие файла через подходящую программу
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${context?.packageName}.provider",
                File(fileModel.path)
            )
            val mime = context?.contentResolver?.getType(uri)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(uri, mime)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast(R.string.message_cannot_be_opened)
        }
    }

    private fun copyPath(fileModel: FileModel) {
        fileModel.path.clipText(context)
        showToast(R.string.message_done)
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
                    showToast(R.string.message_invalid_file_name)
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
                    showToast(R.string.message_invalid_file_name)
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
                executeProcess(fileModels, Operation.DELETE)
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
                    getString(R.string.properties_char_count).format(propertiesModel.chars))
            ) { html() }
            customView(R.layout.dialog_properties, scrollable = true)

            val readable = findViewById<CheckBox>(R.id.readable)
            val writable = findViewById<CheckBox>(R.id.writable)
            val executable = findViewById<CheckBox>(R.id.executable)

            readable.isChecked = propertiesModel.readable
            writable.isChecked = propertiesModel.writable
            executable.isChecked = propertiesModel.executable
        }
    }

    private fun executeProcess(
        fileModels: List<FileModel>,
        operation: Operation,
        actionAfter: () -> Unit = {}
    ) {
        val dialogTitle: Int
        val dialogMessage: String
        val dialogAction: () -> Unit
        when (operation) {
            Operation.DELETE -> {
                dialogTitle = R.string.dialog_title_deleting
                dialogMessage = getString(R.string.message_deleting)
                dialogAction = {
                    viewModel.deleteFiles(fileModels)
                }
            }
            Operation.COPY -> {
                dialogTitle = R.string.dialog_title_copying
                dialogMessage = getString(R.string.message_copying)
                dialogAction = {
                    viewModel.copyFiles(fileModels, fileTree.parent)
                }
            }
            Operation.CUT -> {
                dialogTitle = R.string.dialog_title_copying
                dialogMessage = getString(R.string.message_copying)
                dialogAction = {
                    viewModel.cutFiles(fileModels, fileTree.parent)
                }
            }
        }

        MaterialDialog(requireContext()).show {
            title(dialogTitle)
            customView(R.layout.dialog_process)
            cancelOnTouchOutside(false)
            positiveButton(R.string.action_run_in_background) {
                actionAfter.invoke()
            }
            negativeButton(R.string.action_cancel) {
                viewModel.cancelableDisposable.dispose()
                actionAfter.invoke()
            }

            val textElapsedTime = findViewById<TextView>(R.id.text_elapsed_time)
            val textDetails = findViewById<TextView>(R.id.text_details)
            val textOfTotal = findViewById<TextView>(R.id.text_of_total)
            val progressIndicator = findViewById<ProgressIndicator>(R.id.progress)

            formatElapsedTime(textElapsedTime, 0L) // 00:00

            val then = System.currentTimeMillis()
            val timer = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    val difference = System.currentTimeMillis() - then
                    formatElapsedTime(textElapsedTime, difference)
                }
                .disposeOnFragmentDestroyView()

            val totalProgress = fileModels.size
            progressIndicator.max = totalProgress

            val progressObserver = Observer<Int> { currentProgress ->
                if (currentProgress < fileModels.size) {
                    val fileModel = fileModels[currentProgress]
                    textDetails.text = String.format(dialogMessage, fileModel.path)
                    textOfTotal.text = String.format(
                        getString(R.string.message_of_total),
                        currentProgress + 1,
                        totalProgress
                    )
                    progressIndicator.progress = currentProgress + 1
                }
                if (currentProgress + 1 > totalProgress) {
                    actionAfter.invoke()
                    dismiss()
                }
            }

            setOnShowListener {
                viewModel.progressEvent.observe(viewLifecycleOwner, progressObserver)
                dialogAction.invoke()
            }

            setOnDismissListener {
                viewModel.progressEvent.removeObservers(viewLifecycleOwner)
                timer.dispose()
            }
        }
    }

    // endregion DIALOGS

    private fun formatElapsedTime(textView: TextView, timeInMillis: Long) {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val elapsedTime = String.format(
            getString(R.string.message_elasped_time),
            formatter.format(timeInMillis)
        )
        textView.text = elapsedTime
    }
}