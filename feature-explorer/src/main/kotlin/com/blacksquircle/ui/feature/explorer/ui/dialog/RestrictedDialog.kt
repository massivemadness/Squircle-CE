package com.blacksquircle.ui.feature.explorer.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.afollestad.materialdialogs.MaterialDialog
import com.blacksquircle.ui.feature.explorer.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestrictedDialog : DialogFragment() {

    private val navArgs by navArgs<RestrictedDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(R.string.dialog_title_storage_access)
            message(R.string.dialog_message_storage_access)
            negativeButton(R.string.action_cancel)
            positiveButton(R.string.action_continue) {
                val intent = Intent(navArgs.action).apply {
                    data = navArgs.data.toUri()
                }
                startActivity(intent)
            }
        }
    }
}