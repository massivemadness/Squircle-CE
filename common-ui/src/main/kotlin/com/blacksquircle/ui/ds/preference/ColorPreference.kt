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

package com.blacksquircle.ui.ds.preference

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.dialog.AlertDialog

private const val RedKey = "red"
private const val GreenKey = "green"
private const val BlueKey = "blue"

private val ColorSaver = mapSaver(
    save = {
        mapOf(
            RedKey to it.red,
            GreenKey to it.green,
            BlueKey to it.blue,
        )
    },
    restore = {
        Color(
            red = it[RedKey] as Float,
            green = it[GreenKey] as Float,
            blue = it[BlueKey] as Float
        )
    }
)

@Composable
fun ColorPreference(
    title: String,
    subtitle: String,
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    enabled: Boolean = true,
    dialogTitle: String? = null,
    dialogShown: Boolean = false,
    confirmButton: String? = null,
    dismissButton: String? = null,
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
        }
    )
    if (showDialog) {
        val color = rememberSaveable(stateSaver = ColorSaver) {
            mutableStateOf(initialColor)
        }
        AlertDialog(
            title = dialogTitle ?: title,
            content = {
                // TODO ColorLayout
            },
            confirmButton = confirmButton,
            onConfirmClicked = {
                showDialog = false
                onColorSelected(color.value)
            },
            dismissButton = dismissButton,
            onDismissClicked = {
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Preview
@Composable
private fun ColorPreferencePreview() {
    SquircleTheme {
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