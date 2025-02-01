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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog
import com.blacksquircle.ui.feature.shortcuts.R
import com.blacksquircle.ui.feature.shortcuts.ui.mvi.ShortcutIntent
import com.blacksquircle.ui.feature.shortcuts.ui.viewmodel.ShortcutsViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.blacksquircle.ui.ds.R as UiR

@AndroidEntryPoint
class ConflictKeyDialog : DialogFragment() {

    private val viewModel by hiltNavGraphViewModels<ShortcutsViewModel>(R.id.shortcuts_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SquircleTheme {
                    AlertDialog(
                        title = stringResource(android.R.string.dialog_alert_title),
                        content = {
                            Text(
                                text = stringResource(R.string.shortcut_conflict),
                                color = SquircleTheme.colors.colorTextAndIconSecondary,
                                style = SquircleTheme.typography.text16Regular,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        },
                        dismissButton = stringResource(android.R.string.cancel),
                        onDismissClicked = {
                            viewModel.obtainEvent(ShortcutIntent.ResolveConflict(reassign = false))
                            dismiss()
                        },
                        confirmButton = stringResource(UiR.string.common_continue),
                        onConfirmClicked = {
                            viewModel.obtainEvent(ShortcutIntent.ResolveConflict(reassign = true))
                            dismiss()
                        },
                        onDismiss = {
                            dismiss()
                        },
                    )
                }
            }
        }
    }
}