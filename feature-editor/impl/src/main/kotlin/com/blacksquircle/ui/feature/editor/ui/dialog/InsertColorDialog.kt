/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import com.blacksquircle.ui.core.effect.sendNavigationResult
import com.blacksquircle.ui.core.extensions.sendFragmentResult
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.ColorPickerDialog
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.fragment.EditorFragment
import com.blacksquircle.ui.ds.R as UiR

internal class InsertColorDialog : DialogFragment() {

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = content {
        SquircleTheme {
            ColorPickerDialog(
                title = stringResource(UiR.string.dialog_title_color_picker),
                confirmButton = stringResource(R.string.action_insert),
                dismissButton = stringResource(android.R.string.cancel),
                onColorSelected = { color ->
                    sendNavigationResult(
                        key = EditorFragment.KEY_INSERT_COLOR,
                        result = bundleOf(
                            EditorFragment.ARG_COLOR to color.toHexString().toColorInt()
                        )
                    )
                    navController.popBackStack()
                },
                onDismissClicked = { navController.popBackStack() },
                onDismiss = { navController.popBackStack() },
            )
        }
    }
}