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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.blacksquircle.ui.core.contract.NotificationPermission
import com.blacksquircle.ui.core.contract.PermissionResult
import com.blacksquircle.ui.core.extensions.navigate
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.Operation
import com.blacksquircle.ui.feature.explorer.databinding.DialogProgressBinding
import com.blacksquircle.ui.feature.explorer.ui.mvi.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.navigation.ExplorerScreen
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.worker.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProgressDialog : DialogFragment() {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val navArgs by navArgs<ProgressDialogArgs>()
    private val navController by lazy { findNavController() }
    private val notificationPermission = NotificationPermission(this) { result ->
        when (result) {
            PermissionResult.DENIED,
            PermissionResult.DENIED_FOREVER -> {
                navController.navigate(ExplorerScreen.NotificationDeniedForever)
            }
            PermissionResult.GRANTED -> dismiss()
        }
    }

    private lateinit var binding: DialogProgressBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProgressBinding.inflate(layoutInflater)

        lifecycleScope.launch {
            val then = System.currentTimeMillis()
            formatElapsedTime(0L) // 00:00
            repeat(Int.MAX_VALUE) {
                val difference = System.currentTimeMillis() - then
                formatElapsedTime(difference)
                delay(1000)
            }
        }

        val operation = Operation.of(navArgs.operation)
        observeProgress(operation)

        return AlertDialog.Builder(requireContext())
            .setTitle(
                when (operation) {
                    Operation.CREATE -> R.string.dialog_title_creating
                    Operation.RENAME -> R.string.dialog_title_renaming
                    Operation.DELETE -> R.string.dialog_title_deleting
                    Operation.CUT -> R.string.dialog_title_copying
                    Operation.COPY -> R.string.dialog_title_copying
                    Operation.COMPRESS -> R.string.dialog_title_compressing
                    Operation.EXTRACT -> R.string.dialog_title_extracting
                }
            )
            .setView(binding.root)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                when (operation) {
                    Operation.CREATE -> CreateFileWorker.cancelJob(requireContext())
                    Operation.RENAME -> RenameFileWorker.cancelJob(requireContext())
                    Operation.DELETE -> DeleteFileWorker.cancelJob(requireContext())
                    Operation.CUT -> CutFileWorker.cancelJob(requireContext())
                    Operation.COPY -> CopyFileWorker.cancelJob(requireContext())
                    Operation.COMPRESS -> CompressFileWorker.cancelJob(requireContext())
                    Operation.EXTRACT -> ExtractFileWorker.cancelJob(requireContext())
                }
            }
            .setPositiveButton(R.string.action_run_in_background) { _, _ ->
                notificationPermission.launch()
            }
            .create()
    }

    private fun observeProgress(operation: Operation) {
        binding.progressBar.isIndeterminate = navArgs.totalCount == -1
        binding.progressBar.max = navArgs.totalCount
        binding.total.isVisible = navArgs.totalCount > 0

        when (operation) {
            Operation.CREATE -> {
                CreateFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_creating, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
            Operation.RENAME -> {
                RenameFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_renaming, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
            Operation.DELETE -> {
                DeleteFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_deleting, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
            Operation.CUT -> {
                CutFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_copying, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
            Operation.COPY -> {
                CopyFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_copying, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
            Operation.COMPRESS -> {
                CompressFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_compressing, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
            Operation.EXTRACT -> {
                ExtractFileWorker.observeJob(requireContext())
                    .flowWithLifecycle(lifecycle)
                    .onEach { fileModel ->
                        binding.progressBar.progress += 1
                        binding.details.text = getString(R.string.message_extracting, fileModel.path)
                        binding.total.text = getString(
                            R.string.message_of_total,
                            binding.progressBar.progress,
                            navArgs.totalCount,
                        )
                    }
                    .catch {
                        viewModel.obtainEvent(ExplorerIntent.Refresh)
                        dismiss()
                    }
                    .launchIn(lifecycleScope)
            }
        }
    }

    private fun formatElapsedTime(timeInMillis: Long) {
        val formatter = SimpleDateFormat(getString(R.string.progress_time_format), Locale.getDefault())
        val elapsedTime = getString(
            R.string.message_elapsed_time,
            formatter.format(timeInMillis),
        )
        binding.elapsedTime.text = elapsedTime
    }
}