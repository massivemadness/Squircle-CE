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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.Operation
import com.blacksquircle.ui.feature.explorer.databinding.DialogProgressBinding
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.worker.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProgressDialog : DialogFragment() {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val navArgs by navArgs<ProgressDialogArgs>()

    private lateinit var binding: DialogProgressBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            customView(R.layout.dialog_progress)
            cancelOnTouchOutside(false)
            positiveButton(R.string.action_run_in_background)

            binding = DialogProgressBinding.bind(getCustomView())
            binding.progressBar.isIndeterminate = navArgs.totalCount == -1
            binding.progressBar.max = navArgs.totalCount
            binding.total.isVisible = navArgs.totalCount > 0

            when (Operation.find(navArgs.operation)) {
                Operation.CREATE -> {
                    title(R.string.dialog_title_creating)
                    negativeButton(R.string.action_cancel) {
                        CreateFileWorker.cancelJob(requireContext())
                    }
                    CreateFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_creating, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
                Operation.RENAME -> {
                    title(R.string.dialog_title_renaming)
                    negativeButton(R.string.action_cancel) {
                        RenameFileWorker.cancelJob(requireContext())
                    }
                    RenameFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_renaming, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
                Operation.DELETE -> {
                    title(R.string.dialog_title_deleting)
                    negativeButton(R.string.action_cancel) {
                        DeleteFileWorker.cancelJob(requireContext())
                    }
                    DeleteFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_deleting, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
                Operation.COPY -> {
                    title(R.string.dialog_title_copying)
                    negativeButton(R.string.action_cancel) {
                        CopyFileWorker.cancelJob(requireContext())
                    }
                    CopyFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_copying, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
                Operation.CUT -> {
                    title(R.string.dialog_title_copying)
                    negativeButton(R.string.action_cancel) {
                        CutFileWorker.cancelJob(requireContext())
                    }
                    CutFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_copying, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
                Operation.COMPRESS -> {
                    title(R.string.dialog_title_compressing)
                    negativeButton(R.string.action_cancel) {
                        CompressFileWorker.cancelJob(requireContext())
                    }
                    CompressFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_compressing, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
                Operation.EXTRACT -> {
                    title(R.string.dialog_title_extracting)
                    negativeButton(R.string.action_cancel) {
                        ExtractFileWorker.cancelJob(requireContext())
                    }
                    ExtractFileWorker.observeJob(requireContext())
                        .flowWithLifecycle(lifecycle)
                        .onEach { fileModel ->
                            binding.progressBar.progress += 1
                            binding.details.text = getString(R.string.message_extracting, fileModel.path)
                            binding.total.text = getString(
                                R.string.message_of_total,
                                binding.progressBar.progress,
                                navArgs.totalCount
                            )
                        }
                        .catch {
                            viewModel.obtainEvent(ExplorerIntent.Refresh)
                            dismiss()
                        }
                        .launchIn(lifecycleScope)
                }
            }

            lifecycleScope.launchWhenStarted {
                val then = System.currentTimeMillis()
                formatElapsedTime(0L) // 00:00
                repeat(Int.MAX_VALUE) {
                    val difference = System.currentTimeMillis() - then
                    formatElapsedTime(difference)
                    delay(1000)
                }
            }
        }
    }

    private fun formatElapsedTime(timeInMillis: Long) {
        val formatter = SimpleDateFormat(getString(R.string.progress_time_format), Locale.getDefault())
        val elapsedTime = getString(
            R.string.message_elapsed_time,
            formatter.format(timeInMillis)
        )
        binding.elapsedTime.text = elapsedTime
    }
}