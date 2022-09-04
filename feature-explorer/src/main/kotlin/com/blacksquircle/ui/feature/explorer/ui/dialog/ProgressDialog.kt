package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.data.utils.BufferType
import com.blacksquircle.ui.feature.explorer.databinding.DialogProgressBinding
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import com.blacksquircle.ui.feature.explorer.ui.viewstate.ExplorerViewState
import com.blacksquircle.ui.feature.explorer.ui.worker.CreateFileWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
            observeViewModel()

            customView(R.layout.dialog_progress)
            cancelOnTouchOutside(false)
            positiveButton(R.string.action_run_in_background)
            negativeButton(R.string.action_cancel)

            binding = DialogProgressBinding.bind(getCustomView())
            binding.progressIndicator.max = navArgs.totalCount

            lifecycleScope.launchWhenStarted {
                val then = System.currentTimeMillis()
                formatElapsedTime(binding.textElapsedTime, 0L) // 00:00
                repeat(Int.MAX_VALUE) {
                    val difference = System.currentTimeMillis() - then
                    formatElapsedTime(binding.textElapsedTime, difference)
                    delay(1000)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.explorerViewState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                when (state) {
                    is ExplorerViewState.Data -> when (state.bufferType) {
                        BufferType.CREATE -> {
                            dialog?.setTitle(R.string.dialog_title_creating)
                            CreateFileWorker.observeJob(requireContext())
                                .flowWithLifecycle(lifecycle)
                                .onEach { fileModel ->
                                    val currentProgress = binding.progressIndicator.progress
                                    binding.textDetails.text = getString(R.string.message_creating, fileModel.path)
                                    binding.textOfTotal.text = getString(
                                        R.string.message_of_total,
                                        currentProgress + 1,
                                        navArgs.totalCount
                                    )
                                    binding.progressIndicator.progress = currentProgress + 1
                                }
                                .launchIn(lifecycleScope)
                        }
                        BufferType.RENAME -> {
                            dialog?.setTitle(R.string.dialog_title_renaming)
                            // message(R.string.message_renaming)
                        }
                        BufferType.DELETE -> {
                            dialog?.setTitle(R.string.dialog_title_deleting)
                            // message(R.string.message_deleting)
                        }
                        BufferType.COPY -> {
                            dialog?.setTitle(R.string.dialog_title_copying)
                            // message(R.string.message_copying)
                        }
                        BufferType.CUT -> {
                            dialog?.setTitle(R.string.dialog_title_copying)
                            // message(R.string.message_copying)
                        }
                        BufferType.COMPRESS -> {
                            dialog?.setTitle(R.string.dialog_title_compressing)
                            // message(R.string.message_compressing)
                        }
                        BufferType.EXTRACT -> {
                            dialog?.setTitle(R.string.dialog_title_extracting)
                            // message(R.string.message_extracting)
                        }
                        BufferType.NONE -> Unit
                    }
                    is ExplorerViewState.Stub -> Unit
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun formatElapsedTime(textView: TextView, timeInMillis: Long) {
        val formatter = SimpleDateFormat(getString(R.string.progress_time_format), Locale.getDefault())
        val elapsedTime = getString(
            R.string.message_elapsed_time,
            formatter.format(timeInMillis)
        )
        textView.text = elapsedTime
    }
}