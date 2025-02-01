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

package com.blacksquircle.ui.feature.servers.ui.dialog.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import com.blacksquircle.ui.ds.dropdown.Dropdown
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.filesystem.base.model.FileServer

@Composable
@NonRestartableComposable
internal fun ServerScheme(
    scheme: String,
    onSchemeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Dropdown(
        entries = stringArrayResource(R.array.server_type),
        entryValues = FileServer.entries
            .map(FileServer::value)
            .toTypedArray(),
        currentValue = scheme,
        onValueSelected = onSchemeChanged,
        modifier = modifier,
    )
}