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

package com.blacksquircle.ui.ds.dialog

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.extensions.toHexString
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.ds.textfield.TextFieldStyleDefaults
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorPickerDialog(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButton: String? = null,
    dismissButton: String? = null,
    alphaSlider: Boolean = true,
    brightnessSlider: Boolean = true,
    initialColor: Color = Color.White,
    onColorSelected: (Color) -> Unit = {},
    onDismissClicked: () -> Unit = {},
    properties: DialogProperties = DialogProperties(),
) {
    val controller = rememberColorPickerController()
    var textColor by remember {
        mutableStateOf(initialColor.toHexString())
    }

    AlertDialog(
        title = title,
        content = {
            Column {
                HsvColorPicker(
                    initialColor = initialColor,
                    onColorChanged = { colorEnvelope ->
                        if (colorEnvelope.fromUser) {
                            textColor = colorEnvelope.color.toHexString()
                        }
                    },
                    controller = controller,
                    modifier = Modifier
                        .aspectRatio(1f / 1f)
                        .padding(24.dp),
                )

                if (alphaSlider) {
                    AlphaSlider(
                        initialColor = initialColor,
                        controller = controller,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                }

                if (brightnessSlider) {
                    BrightnessSlider(
                        initialColor = initialColor,
                        controller = controller,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                }

                TextField(
                    inputText = textColor,
                    onInputChanged = { input ->
                        textColor = input
                        try {
                            val color = Color(input.toColorInt())
                            controller.selectByColor(color, fromUser = false)
                        } catch (e: Exception) {
                            // ignored
                        }
                    },
                    textFieldStyle = TextFieldStyleDefaults.Default.copy(
                        textStyle = TextStyle(
                            fontFamily = FontFamily(Typeface.MONOSPACE),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        ),
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(150.dp)
                )
            }
        },
        onDismiss = onDismiss,
        modifier = modifier,
        verticalScroll = true,
        horizontalPadding = true,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        onConfirmClicked = { onColorSelected(controller.selectedColor.value) },
        onDismissClicked = onDismissClicked,
        properties = properties,
    )
}

@PreviewLightDark
@Composable
private fun ColorPickerDialogPreview() {
    PreviewBackground {
        ColorPickerDialog(
            title = "Color Picker",
            confirmButton = "Select",
            dismissButton = "Cancel",
            onDismiss = {},
        )
    }
}