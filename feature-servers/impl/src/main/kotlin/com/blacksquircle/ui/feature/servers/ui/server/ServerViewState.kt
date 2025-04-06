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

package com.blacksquircle.ui.feature.servers.ui.server

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.feature.servers.ui.server.compose.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.server.compose.PasswordAction
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerType
import java.util.UUID

@Immutable
internal data class ServerViewState(
    val isEditMode: Boolean = false,
    val scheme: ServerType = ServerType.FTP,
    val name: String = "",
    val address: String = "",
    val port: String = "",
    val initialDir: String = "",
    val passwordAction: PasswordAction = PasswordAction.ASK_FOR_PASSWORD,
    val passphraseAction: PassphraseAction = PassphraseAction.ASK_FOR_PASSPHRASE,
    val authMethod: AuthMethod = AuthMethod.PASSWORD,
    val username: String = "",
    val password: String = "",
    val keyId: String = "",
    val passphrase: String = "",
    val invalidName: Boolean = false,
    val invalidAddress: Boolean = false,
) : ViewState {

    fun toConfig(serverId: String?): ServerConfig {
        return ServerConfig(
            uuid = serverId ?: UUID.randomUUID().toString(),
            scheme = scheme,
            name = name.trim(),
            address = address.trim(),
            port = port.toIntOrNull() ?: when (scheme) {
                ServerType.FTP,
                ServerType.FTPS,
                ServerType.FTPES -> DEFAULT_FTP_PORT
                ServerType.SFTP -> DEFAULT_SFTP_PORT
            },
            initialDir = initialDir.trim(),
            authMethod = authMethod,
            username = username.trim(),
            password = if (
                authMethod == AuthMethod.PASSWORD &&
                passwordAction == PasswordAction.SAVE_PASSWORD
            ) {
                password.trim()
            } else {
                null
            },
            keyId = if (authMethod == AuthMethod.KEY) keyId else null,
            passphrase = if (
                authMethod == AuthMethod.KEY &&
                passphraseAction == PassphraseAction.SAVE_PASSPHRASE
            ) {
                passphrase.trim()
            } else {
                null
            },
        )
    }

    companion object {

        const val DEFAULT_FTP_PORT = 21
        const val DEFAULT_SFTP_PORT = 22

        fun create(serverConfig: ServerConfig): ServerViewState {
            return ServerViewState(
                isEditMode = true,
                scheme = serverConfig.scheme,
                name = serverConfig.name,
                address = serverConfig.address,
                port = serverConfig.port.toString(),
                initialDir = serverConfig.initialDir,
                passwordAction = if (serverConfig.password.isNullOrEmpty()) {
                    PasswordAction.ASK_FOR_PASSWORD
                } else {
                    PasswordAction.SAVE_PASSWORD
                },
                passphraseAction = if (serverConfig.passphrase.isNullOrEmpty()) {
                    PassphraseAction.ASK_FOR_PASSPHRASE
                } else {
                    PassphraseAction.SAVE_PASSPHRASE
                },
                authMethod = serverConfig.authMethod,
                username = serverConfig.username,
                password = serverConfig.password.orEmpty(),
                keyId = serverConfig.keyId.orEmpty(),
                passphrase = serverConfig.passphrase.orEmpty(),
            )
        }
    }
}