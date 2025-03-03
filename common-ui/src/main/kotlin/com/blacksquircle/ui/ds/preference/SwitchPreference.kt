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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.blacksquircle.ui.ds.PreviewBackground
import com.blacksquircle.ui.ds.extensions.mergeSemantics
import com.blacksquircle.ui.ds.switcher.Switcher

@Composable
fun SwitchPreference(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
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
            Switcher(
                onClick = { onCheckedChange(!checked) },
                checked = checked,
                enabled = enabled,
            )
        },
        modifier = modifier.mergeSemantics(),
    )
}

@PreviewLightDark
@Composable
private fun EnabledSwitchPreferenceCheckedPreview() {
    PreviewBackground {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            checked = true,
        )
    }
}

@PreviewLightDark
@Composable
private fun EnabledSwitchPreferenceUncheckedPreview() {
    PreviewBackground {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            checked = false,
        )
    }
}

@PreviewLightDark
@Composable
private fun DisabledSwitchPreferenceCheckedPreview() {
    PreviewBackground {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            enabled = false,
            checked = true,
        )
    }
}

@PreviewLightDark
@Composable
private fun DisabledSwitchPreferenceUncheckedPreview() {
    PreviewBackground {
        SwitchPreference(
            title = "Preference Title",
            subtitle = "Preference Subtitle",
            enabled = false,
            checked = false,
        )
    }
}