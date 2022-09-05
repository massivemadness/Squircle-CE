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
import com.blacksquircle.ui.feature.explorer.databinding.DialogCompressBinding
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerEvent
import com.blacksquircle.ui.feature.explorer.ui.viewmodel.ExplorerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompressDialog : DialogFragment() {

    private val viewModel by activityViewModels<ExplorerViewModel>()
    private val navController by lazy { findNavController() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_archive_name)
            customView(R.layout.dialog_compress)

            val binding = DialogCompressBinding.bind(getCustomView())

            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_create_zip) {
                val fileName = binding.input.text.toString()
                navController.popBackStack()
                viewModel.obtainEvent(ExplorerEvent.CompressFile(fileName))
            }
        }
    }
}