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
internal fun PassphraseAction(
    passphraseAction: PassphraseAction,
    onPassphraseActionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Dropdown(
        entries = stringArrayResource(R.array.passphraseAction),
        entryValues = PassphraseAction.entries
            .fastMap(PassphraseAction::value)
            .toTypedArray(),
        currentValue = passphraseAction.value,
        onValueSelected = onPassphraseActionChanged,
        modifier = modifier,
    )
}

internal enum class PassphraseAction(val value: String) {
    ASK_FOR_PASSPHRASE("ask_for_passphrase"),
    SAVE_PASSPHRASE("save_passphrase");

    companion object {

        fun of(value: String): PassphraseAction {
            return checkNotNull(entries.find { it.value == value })
        }
    }
}