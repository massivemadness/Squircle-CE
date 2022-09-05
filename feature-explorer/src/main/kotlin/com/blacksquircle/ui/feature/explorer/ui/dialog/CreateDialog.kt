package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.databinding.DialogCreateBinding
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerIntent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateDialog : DialogFragment() {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val navController by lazy { findNavController() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_create)
            customView(R.layout.dialog_create)

            val binding = DialogCreateBinding.bind(getCustomView())

            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_create) {
                val fileName = binding.input.text.toString()
                val isFolder = binding.boxIsFolder.isChecked
                navController.popBackStack()
                viewModel.obtainEvent(ExplorerIntent.CreateFile(fileName, isFolder))
            }
        }
    }
}