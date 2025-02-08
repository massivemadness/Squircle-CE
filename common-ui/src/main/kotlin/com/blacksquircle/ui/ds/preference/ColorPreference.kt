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

package com.blacksquircle.ui.ds.preference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.dialog.ColorPickerDialog

@Composable
fun ColorPreference(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    initialColor: Color = Color.Black,
    onColorSelected: (Color) -> Unit = {},
    enabled: Boolean = true,
    dialogTitle: String = stringResource(R.string.dialog_title_color_picker),
    dialogShown: Boolean = false,
) {
    var showDialog by rememberSaveable {
        mutableStateOf(dialogShown)
    }
    Preference(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = { showDialog = true },
        trailingContent = {
            Canvas(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(4.dp))
                    .size(24.dp)
            ) {
                drawRect(color = initialColor)
            }
        },
        modifier = modifier,
    )
    if (showDialog) {
        ColorPickerDialog(
            title = dialogTitle,
            confirmButton = stringResource(R.string.common_select),
            dismissButton = stringResource(android.R.string.cancel),
            alphaSlider = false,
            initialColor = initialColor,
            onColorSelected = { color ->
                showDialog = false
                onColorSelected(color)
            },
            onDismissClicked = { showDialog = false },
            onDismiss = { showDialog = false },
        )
    }
}

@PreviewLightDark
@Composable
private fun ColorPreferencePreview() {
    PreviewBackground {
        ColorPreference(
            title = "Color",
            subtitle = "Select color",
            initialColor = Color.Red,
            onColorSelected = {},
            dialogTitle = "Color Picker",
            dialogShown = false,
        )
    }
}