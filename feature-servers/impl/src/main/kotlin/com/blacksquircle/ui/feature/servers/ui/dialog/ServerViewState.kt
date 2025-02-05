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
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.dialog.internal.PasswordAction
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.FileServer

@Immutable
internal data class ServerViewState(
    val isEditMode: Boolean = false,
    val scheme: FileServer = FileServer.FTP,
    val name: String = "",
    val address: String = "",
    val port: String = "",
    val initialDir: String = "",
    val passwordAction: PasswordAction = PasswordAction.ASK_FOR_PASSWORD,
    val passphraseAction: PassphraseAction = PassphraseAction.ASK_FOR_PASSPHRASE,
    val authMethod: AuthMethod = AuthMethod.PASSWORD,
    val username: String = "",
    val password: String = "",
    val privateKey: String = "",
    val passphrase: String = "",
    val invalidName: Boolean = false,
    val invalidAddress: Boolean = false,
) : ViewState() {

    companion object {
        const val DEFAULT_FTP_PORT = 21
        const val DEFAULT_SFTP_PORT = 22
    }
}