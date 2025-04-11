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

package com.blacksquircle.ui.feature.servers.ui

import android.net.Uri
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.servers.createServerConfig
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.feature.servers.ui.server.ServerViewEvent
import com.blacksquircle.ui.feature.servers.ui.server.ServerViewModel
import com.blacksquircle.ui.feature.servers.ui.server.ServerViewState
import com.blacksquircle.ui.feature.servers.ui.server.compose.PassphraseAction
import com.blacksquircle.ui.feature.servers.ui.server.compose.PasswordAction
import com.blacksquircle.ui.filesystem.base.model.AuthMethod
import com.blacksquircle.ui.filesystem.base.model.ServerType
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class ServerViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val serverRepository = mockk<ServerRepository>(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(UUID::class)
        every { UUID.randomUUID().toString() } returns "12345"
    }

    @Test
    fun `When screen opens Then display empty state`() = runTest {
        // Given
        val viewState = ServerViewState(isEditMode = false)

        // When
        val viewModel = createViewModel(serverId = null)

        // Then
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 0) { serverRepository.loadServer(any()) }
    }

    @Test
    fun `When serverId passed to the screen Then load server data`() = runTest {
        // Given
        val serverId = "1"
        val serverConfig = createServerConfig(uuid = serverId)
        coEvery { serverRepository.loadServer(serverId) } returns serverConfig

        // When
        val viewModel = createViewModel(serverId)
        val viewState = ServerViewState.create(serverConfig)

        // Then
        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 1) { serverRepository.loadServer(serverId) }
    }

    @Test
    fun `When scheme changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onSchemeChanged(ServerType.SFTP.value)

        // Then
        val viewState = ServerViewState(scheme = ServerType.SFTP)
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When scheme changed from sftp to ftp Then set auth method to password`() = runTest {
        // Given
        val viewModel = createViewModel()
        viewModel.onSchemeChanged(ServerType.SFTP.value)
        viewModel.onAuthMethodChanged(AuthMethod.KEY.value)

        // When
        viewModel.onSchemeChanged(ServerType.FTP.value)

        // Then
        val viewState = ServerViewState(
            scheme = ServerType.FTP,
            authMethod = AuthMethod.PASSWORD,
        )
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When name changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onNameChanged("server name")

        // Then
        val viewState = ServerViewState(name = "server name")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When address changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onAddressChanged("server address")

        // Then
        val viewState = ServerViewState(address = "server address")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When port changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPortChanged("22")

        // Then
        val viewState = ServerViewState(port = "22")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When username changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onUsernameChanged("username")

        // Then
        val viewState = ServerViewState(username = "username")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When auth method changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onAuthMethodChanged(AuthMethod.KEY.value)

        // Then
        val viewState = ServerViewState(authMethod = AuthMethod.KEY)
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When choose file clicked Then send choose event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onChooseFileClicked()

        // Then
        val expected = ServerViewEvent.ChooseFile
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When key file selected Then save key`() = runTest {
        // Given
        val viewModel = createViewModel()
        val keyUri = mockk<Uri>()
        val keyId = "key_id"
        coEvery { serverRepository.saveKeyFile(keyUri) } returns keyId

        // When
        viewModel.onKeyFileSelected(keyUri)

        // Then
        val viewState = ServerViewState(keyId = keyId)
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { serverRepository.saveKeyFile(keyUri) }
    }

    @Test
    fun `When password action changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPasswordActionChanged(PasswordAction.SAVE_PASSWORD.value)

        // Then
        val viewState = ServerViewState(passwordAction = PasswordAction.SAVE_PASSWORD)
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When passphrase action changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPassphraseActionChanged(PassphraseAction.SAVE_PASSPHRASE.value)

        // Then
        val viewState = ServerViewState(passphraseAction = PassphraseAction.SAVE_PASSPHRASE)
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When password changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPasswordChanged("password")

        // Then
        val viewState = ServerViewState(password = "password")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When passphrase changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onPassphraseChanged("passphrase")

        // Then
        val viewState = ServerViewState(passphrase = "passphrase")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When initial dir changed Then update view state`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onInitialDirChanged("/pub")

        // Then
        val viewState = ServerViewState(initialDir = "/pub")
        assertEquals(viewState, viewModel.viewState.value)
    }

    @Test
    fun `When save valid config clicked Then save config`() = runTest {
        // Given
        val viewModel = createViewModel()
        val validName = "Server Name"
        val validAddress = "192.168.1.1"

        // When
        viewModel.onNameChanged(validName)
        viewModel.onAddressChanged(validAddress)
        viewModel.onSaveClicked()

        // Then
        val viewEvent = ServerViewEvent.SendSaveResult
        val viewState = ServerViewState(
            name = validName,
            address = validAddress,
        )
        val serverConfig = viewState.toConfig(null)

        assertEquals(viewState, viewModel.viewState.value)
        assertEquals(viewEvent, viewModel.viewEvent.first())
        coVerify(exactly = 1) { serverRepository.upsertServer(serverConfig) }
    }

    @Test
    fun `When save invalid config clicked Then show error fields`() = runTest {
        // Given
        val viewModel = createViewModel()
        val invalidName = "" // empty
        val invalidAddress = " " // blank

        // When
        viewModel.onNameChanged(invalidName)
        viewModel.onAddressChanged(invalidAddress)
        viewModel.onSaveClicked()

        // Then
        val viewState = ServerViewState(
            name = invalidName,
            address = invalidAddress,
            invalidName = true,
            invalidAddress = true,
        )

        assertEquals(viewState, viewModel.viewState.value)
        coVerify(exactly = 0) { serverRepository.upsertServer(any()) }
    }

    @Test
    fun `When delete clicked Then delete server`() = runTest {
        // Given
        val serverId = "1"
        val serverConfig = createServerConfig(uuid = serverId)
        coEvery { serverRepository.loadServer(serverId) } returns serverConfig
        val viewModel = createViewModel(serverId)

        // When
        viewModel.onDeleteClicked()

        // Then
        coVerify(exactly = 1) { serverRepository.deleteServer(serverConfig) }
    }

    @Test
    fun `When cancel clicked Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onCancelClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    private fun createViewModel(serverId: String? = null): ServerViewModel {
        return ServerViewModel(
            serverRepository = serverRepository,
            serverId = serverId
        )
    }
}