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

package com.blacksquircle.ui.feature.servers.ui.dialog

import androidx.compose.runtime.Immutable
import com.blacksquircle.ui.core.mvi.ViewState
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerConfig
import com.blacksquircle.ui.filesystem.base.model.ServerScheme
import com.blacksquircle.ui.filesystem.ftp.FTPFilesystem
import com.blacksquircle.ui.filesystem.ftpes.FTPESFilesystem
import com.blacksquircle.ui.filesystem.ftps.FTPSFilesystem
import com.blacksquircle.ui.filesystem.sftp.SFTPFilesystem

@Immutable
data class ServerState(
    val isEditMode: Boolean = false,
    val uuid: String = "",
    val scheme: ServerScheme = ServerScheme.FTP,
    val name: String = "",
    val address: String = "",
    val port: String = "",
    val initialDir: String = "",
    val authMethod: AuthMethod = AuthMethod.PASSWORD,
    val username: String = "",
    val password: String? = null,
    val privateKey: String? = null,
    val passphrase: String? = null,
) : ViewState() {

    fun toServerConfig(): ServerConfig {
        return ServerConfig(
            uuid = uuid,
            scheme = scheme,
            name = name,
            address = address,
            port = port.toIntOrNull() ?: when (scheme) {
                ServerScheme.FTP,
                ServerScheme.FTPS,
                ServerScheme.FTPES -> DEFAULT_FTP_PORT
                ServerScheme.SFTP -> DEFAULT_SFTP_PORT
            },
            initialDir = initialDir,
            authMethod = authMethod,
            username = username,
            password = password,
            privateKey = privateKey,
            passphrase = passphrase,
        )
    }

    companion object {
        internal const val DEFAULT_FTP_PORT = 21
        internal const val DEFAULT_SFTP_PORT = 22
    }
}