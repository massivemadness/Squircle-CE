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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.ds.slider.Slider

@Composable
fun SliderPreference(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minValue: Float = 0f,
    maxValue: Float = 1f,
    currentValue: Float = 0f,
    stepCount: Int = 1,
    onValueChanged: (Float) -> Unit = {},
) {
    Preference(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        bottomContent = {
            Spacer(Modifier.height(8.dp))
            Slider(
                currentValue = currentValue,
                onValueChanged = onValueChanged,
                enabled = enabled,
                minValue = minValue,
                maxValue = maxValue,
                stepCount = stepCount,
            )
        },
        modifier = modifier.mergeSemantics(),
    )
}

@PreviewLightDark
@Composable
private fun SliderPreferenceEnabledPreview() {
    PreviewBackground {
        SliderPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            enabled = true,
            minValue = 1f,
            maxValue = 8f,
            currentValue = 4f,
            stepCount = 2,
        )
    }
}

@PreviewLightDark
@Composable
private fun SliderPreferenceDisabledPreview() {
    PreviewBackground {
        SliderPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            enabled = false,
            minValue = 1f,
            maxValue = 8f,
            currentValue = 4f,
            stepCount = 2,
        )
    }
}