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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.blacksquircle.ui.ds.textfield.TextField
import com.blacksquircle.ui.feature.servers.R
import com.blacksquircle.ui.feature.servers.ui.server.ServerViewState
import com.blacksquircle.ui.filesystem.base.model.ServerType

private const val HINT_ADDRESS = "192.168.21.101"

@Composable
internal fun ServerAddress(
    address: String,
    onAddressChanged: (String) -> Unit,
    port: String,
    onPortChanged: (String) -> Unit,
    scheme: ServerType,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        TextField(
            inputText = address,
            labelText = stringResource(R.string.hint_server_address),
            placeholderText = HINT_ADDRESS,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            error = isError,
            onInputChanged = onAddressChanged,
            modifier = Modifier.fillMaxWidth(0.70f)
        )

        Spacer(Modifier.width(8.dp))

        TextField(
            inputText = port,
            labelText = stringResource(R.string.hint_port),
            placeholderText = when (scheme) {
                ServerType.FTP,
                ServerType.FTPS,
                ServerType.FTPES -> ServerViewState.DEFAULT_FTP_PORT.toString()
                ServerType.SFTP -> ServerViewState.DEFAULT_SFTP_PORT.toString()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            onInputChanged = onPortChanged,
        )
    }
}