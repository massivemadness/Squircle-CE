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

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.blacksquircle.ui.ds.SquircleTheme

@Composable
fun SwitchPreference(
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    checked: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    Preference(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        onClick = { onCheckedChange(!checked) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
    )
}

@Preview
@Composable
private fun EnabledSwitchPreferenceCheckedPreview() {
    SquircleTheme {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
private fun EnabledSwitchPreferenceUncheckedPreview() {
    SquircleTheme {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
private fun DisabledSwitchPreferenceCheckedPreview() {
    SquircleTheme {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            enabled = false,
            checked = true,
            onCheckedChange = {}
        )
    }
}

@Preview
@Composable
private fun DisabledSwitchPreferenceUncheckedPreview() {
    SquircleTheme {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            enabled = false,
            checked = false,
            onCheckedChange = {}
        )
    }
}