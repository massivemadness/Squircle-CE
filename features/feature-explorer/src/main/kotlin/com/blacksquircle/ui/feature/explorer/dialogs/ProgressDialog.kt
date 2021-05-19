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

package com.blacksquircle.ui.feature.explorer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.DialogProgressBinding
import com.blacksquircle.ui.feature.explorer.utils.Operation
import com.blacksquircle.ui.feature.explorer.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.filesystem.base.model.FileModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProgressDialog : DialogFragment() {

    private val viewModel: ExplorerViewModel by activityViewModels()
    private val navArgs: ProgressDialogArgs by navArgs()

    private lateinit var binding: DialogProgressBinding

    private var dialogTitle: Int = -1
    private var dialogMessage: Int = -1
    private var dialogAction: () -> Unit = {} // Действие, которое запустится при открытии диалога
    private var onCloseAction: () -> Unit = {} // Действие, которое выполнится при закрытии диалога
    private var indeterminate: Boolean = false // Загрузка без отображения реального прогресса
    private var tempFiles: List<FileModel> = emptyList() // Список файлов для отображения информации

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectData()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(dialogTitle)
            customView(R.layout.dialog_progress)
            cancelOnTouchOutside(false)
            positiveButton(R.string.action_run_in_background) {
                onCloseAction.invoke()
            }
            negativeButton(R.string.action_cancel) {
                viewModel.currentJob?.cancel()
                onCloseAction.invoke()
            }

            binding = DialogProgressBinding.bind(getCustomView())

            formatElapsedTime(binding.textElapsedTime, 0L) // 00:00

            val then = System.currentTimeMillis()
            lifecycleScope.launchWhenStarted {
                repeat(1000) {
                    val difference = System.currentTimeMillis() - then
                    formatElapsedTime(binding.textElapsedTime, difference)
                    delay(1000)
                }
            }

            val totalProgress = tempFiles.size
            binding.progressIndicator.max = totalProgress
            binding.progressIndicator.isIndeterminate = indeterminate

            val progressObserver = Observer<Int> { currentProgress ->
                if (currentProgress < tempFiles.size) {
                    val fileModel = tempFiles[currentProgress]
                    binding.textDetails.text = getString(dialogMessage, fileModel.path)
                    binding.textOfTotal.text = getString(
                        R.string.message_of_total,
                        currentProgress + 1,
                        totalProgress
                    )
                    binding.progressIndicator.progress = currentProgress + 1
                }
                if (currentProgress >= totalProgress) {
                    onCloseAction.invoke()
                    dismiss()
                }
            }

            setOnShowListener {
                viewModel.progressEvent.observe(this@ProgressDialog, progressObserver)
                dialogAction.invoke()
            }

            setOnDismissListener {
                viewModel.progressEvent.removeObservers(this@ProgressDialog)
            }
        }
    }

    private fun formatElapsedTime(textView: TextView, timeInMillis: Long) {
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val elapsedTime = getString(
            R.string.message_elapsed_time,
            formatter.format(timeInMillis)
        )
        textView.text = elapsedTime
    }

    private fun collectData() {
        tempFiles = viewModel.tempFiles.toList()
        viewModel.tempFiles.clear() // Clear immediately
        when (viewModel.operation) {
            Operation.DELETE -> {
                dialogTitle = R.string.dialog_title_deleting
                dialogMessage = R.string.message_deleting
                dialogAction = {
                    viewModel.deleteFiles(tempFiles)
                }
            }
            Operation.COPY -> {
                dialogTitle = R.string.dialog_title_copying
                dialogMessage = R.string.message_copying
                dialogAction = {
                    viewModel.copyFiles(tempFiles, navArgs.parentPath)
                }
                onCloseAction = {
                    viewModel.allowPasteFiles.value = false
                }
            }
            Operation.CUT -> {
                dialogTitle = R.string.dialog_title_copying
                dialogMessage = R.string.message_copying
                dialogAction = {
                    viewModel.cutFiles(tempFiles, navArgs.parentPath)
                }
                onCloseAction = {
                    viewModel.allowPasteFiles.value = false
                }
            }
            Operation.COMPRESS -> {
                dialogTitle = R.string.dialog_title_compressing
                dialogMessage = R.string.message_compressing
                dialogAction = {
                    viewModel.compressFiles(
                        source = tempFiles,
                        destPath = navArgs.parentPath,
                        archiveName = navArgs.archiveName ?: tempFiles.first().name + ".zip"
                    )
                }
            }
            Operation.EXTRACT -> {
                dialogTitle = R.string.dialog_title_extracting
                dialogMessage = R.string.message_extracting
                dialogAction = {
                    viewModel.extractAll(tempFiles.first(), navArgs.parentPath)
                }
                indeterminate = true
            }
        }
    }
}