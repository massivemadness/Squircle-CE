/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.ui.insertcolor

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.blacksquircle.ui.core.effect.ResultEventBus
import com.blacksquircle.ui.ds.dialog.ColorPickerDialog
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.feature.editor.R
import com.blacksquircle.ui.feature.editor.ui.editor.KEY_INSERT_COLOR

@Composable
internal fun InsertColorScreen(navController: NavController) {
    ColorPickerDialog(
        title = stringResource(R.string.editor_color_picker_dialog_title),
        confirmButton = stringResource(R.string.editor_color_picker_dialog_button_insert),
        dismissButton = stringResource(android.R.string.cancel),
        onColorSelected = { color ->
            ResultEventBus.sendResult(KEY_INSERT_COLOR, color.toHexString().toColorInt())
            navController.popBackStack()
        },
        onDismissClicked = { navController.popBackStack() },
        onDismiss = { navController.popBackStack() },
    )
}