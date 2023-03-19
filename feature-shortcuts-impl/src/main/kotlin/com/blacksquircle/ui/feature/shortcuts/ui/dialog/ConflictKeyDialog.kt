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

package com.blacksquircle.ui.feature.shortcuts.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.ui.mvi.ShortcutIntent
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.uikit.R as UiR

@AndroidEntryPoint
class ConflictKeyDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ShortcutsViewModel>(R.id.shortcuts_graph)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(requireContext()).show {
            title(android.R.string.dialog_alert_title)
            message(R.string.shortcut_conflict)
            negativeButton(android.R.string.cancel) {
                viewModel.obtainEvent(ShortcutIntent.ResolveConflict(reassign = false))
            }
            positiveButton(UiR.string.common_continue) {
                viewModel.obtainEvent(ShortcutIntent.ResolveConflict(reassign = true))
            }
        }
    }
}