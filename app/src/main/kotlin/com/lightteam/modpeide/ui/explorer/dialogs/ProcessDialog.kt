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

package com.lightteam.modpeide.ui.explorer.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.android.material.progressindicator.ProgressIndicator
import com.lightteam.filesystem.model.FileModel
import com.lightteam.modpeide.R
import com.lightteam.modpeide.ui.base.dialogs.BaseDialogFragment
import com.lightteam.modpeide.ui.explorer.utils.Operation
import com.lightteam.modpeide.ui.explorer.viewmodel.ExplorerViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ProcessDialog : BaseDialogFragment() {

    private val viewModel: ExplorerViewModel by activityViewModels()
    private val navArgs: ProcessDialogArgs by navArgs()

    private var dialogTitle: Int = -1
    private var dialogMessage: Int = -1
    private var dialogAction: () -> Unit = {} // Действие, которое запустится при открытии диалога
    private var onCloseAction: () -> Unit = {} // Действие, которое выполняемое при закрытии диалога
    private var indeterminate: Boolean = false // Загрузка без отображения реального прогресса
    private var tempFiles: List<FileModel> = emptyList() // Список файлов для отображения информации

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        collectData()
        return MaterialDialog(requireContext()).show {
            title(dialogTitle)
            customView(R.layout.dialog_process)
            cancelOnTouchOutside(false)
            positiveButton(R.string.action_run_in_background) {
                onCloseAction.invoke()
            }
            negativeButton(R.string.action_cancel) {
                viewModel.cancelableDisposable.dispose()
                onCloseAction.invoke()
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

            val totalProgress = tempFiles.size
            progressIndicator.max = totalProgress
            progressIndicator.isIndeterminate = indeterminate

            val progressObserver = Observer<Int> { currentProgress ->
                if (currentProgress < tempFiles.size) {
                    val fileModel = tempFiles[currentProgress]
                    textDetails.text = getString(dialogMessage).format(fileModel.path)
                    textOfTotal.text = getString(R.string.message_of_total)
                        .format(currentProgress + 1, totalProgress)
                    progressIndicator.progress = currentProgress + 1
                }
                if (currentProgress >= totalProgress) {
                    onCloseAction.invoke()
                    dismiss()
                }
            }

            setOnShowListener {
                viewModel.progressEvent.observe(this@ProcessDialog, progressObserver)
                dialogAction.invoke()
            }

            setOnDismissListener {
                viewModel.progressEvent.removeObservers(this@ProcessDialog)
                timer.dispose()
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
        when (navArgs.operation) {
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
                    viewModel.copyFiles(tempFiles, navArgs.parent)
                }
                onCloseAction = {
                    viewModel.allowPasteFiles.set(false)
                }
            }
            Operation.CUT -> {
                dialogTitle = R.string.dialog_title_copying
                dialogMessage = R.string.message_copying
                dialogAction = {
                    viewModel.cutFiles(tempFiles, navArgs.parent)
                }
                onCloseAction = {
                    viewModel.allowPasteFiles.set(false)
                }
            }
            Operation.COMPRESS -> {
                dialogTitle = R.string.dialog_title_compressing
                dialogMessage = R.string.message_compressing
                dialogAction = {
                    viewModel.compressFiles(
                        source = tempFiles,
                        dest = navArgs.parent,
                        archiveName = navArgs.archiveName ?: tempFiles.first().name + ".zip"
                    )
                }
            }
            Operation.EXTRACT -> {
                dialogTitle = R.string.dialog_title_extracting
                dialogMessage = R.string.message_extracting
                dialogAction = {
                    viewModel.decompressFile(tempFiles.first(), navArgs.parent)
                }
                indeterminate = true
            }
        }
    }
}