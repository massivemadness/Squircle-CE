package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteDialog : DialogFragment() {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<DeleteDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val isMultiDelete = navArgs.fileCount > 1

        val dialogTitle = if (isMultiDelete) {
            getString(R.string.dialog_title_multi_delete)
        } else {
            navArgs.fileName
        }

        val dialogMessage = if (isMultiDelete) {
            R.string.dialog_message_multi_delete
        } else {
            R.string.dialog_message_delete
        }

        return MaterialDialog(requireContext()).show {
            title(text = dialogTitle)
            message(dialogMessage)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_delete) {
                navController.popBackStack()
                viewModel.obtainEvent(ExplorerEvent.DeleteFile)
            }
        }
    }
}