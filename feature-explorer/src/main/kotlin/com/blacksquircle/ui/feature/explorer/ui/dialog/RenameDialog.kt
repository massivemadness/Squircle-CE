package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.DialogRenameBinding
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RenameDialog : DialogFragment() {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val navController by lazy { findNavController() }
    private val navArgs by navArgs<RenameDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_rename)
            customView(R.layout.dialog_rename)

            val binding = DialogRenameBinding.bind(getCustomView())
            binding.input.setText(navArgs.fileName)

            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_rename) {
                val fileName = binding.input.text.toString()
                navController.popBackStack()
                viewModel.obtainEvent(ExplorerEvent.RenameFile(fileName))
            }
        }
    }
}