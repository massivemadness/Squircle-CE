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

package com.blacksquircle.ui.feature.servers.ui.server.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.util.fastMap
import com.blacksquircle.ui.ds.dropdown.Dropdown
import com.blacksquircle.ui.feature.servers.R

@Composable
@NonRestartableComposable
internal fun PasswordAction(
    passwordAction: PasswordAction,
    onPasswordActionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Dropdown(
        entries = stringArrayResource(R.array.passwordAction),
        entryValues = PasswordAction.entries
            .fastMap(PasswordAction::value)
            .toTypedArray(),
        currentValue = passwordAction.value,
        onValueSelected = onPasswordActionChanged,
        modifier = modifier,
    )
}

internal enum class PasswordAction(val value: String) {
    ASK_FOR_PASSWORD("ask_for_password"),
    SAVE_PASSWORD("save_password");

    companion object {

        fun of(value: String): PasswordAction {
            return checkNotNull(entries.find { it.value == value })
        }
    }
}