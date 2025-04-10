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

import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.servers.api.navigation.ServerDialog
import com.blacksquircle.ui.feature.servers.createServerConfig
import com.blacksquircle.ui.feature.servers.domain.model.ServerStatus
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import com.blacksquircle.ui.feature.servers.ui.cloud.CloudViewModel
import com.blacksquircle.ui.feature.servers.ui.cloud.CloudViewState
import com.blacksquircle.ui.feature.servers.ui.cloud.model.ServerModel
import com.blacksquircle.ui.test.rule.MainDispatcherRule
import com.blacksquircle.ui.test.rule.TimberConsoleRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class CloudViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val timberConsoleRule = TimberConsoleRule()

    private val serverRepository = mockk<ServerRepository>(relaxed = true)

    @Test
    fun `When back pressed Then send popBackStack event`() = runTest {
        // Given
        val viewModel = createViewModel()

        // When
        viewModel.onBackClicked()

        // Then
        assertEquals(ViewEvent.PopBackStack, viewModel.viewEvent.first())
    }

    @Test
    fun `When server clicked Then send navigation event`() = runTest {
        // Given
        val viewModel = createViewModel()
        val serverConfig = createServerConfig()
        val screen = ServerDialog(serverConfig.uuid)

        // When
        viewModel.onServerClicked(serverConfig)

        // Then
        val expected = ViewEvent.Navigation(screen)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When create server clicked Then send navigation event`() = runTest {
        // Given
        val viewModel = createViewModel()
        val screen = ServerDialog(null)

        // When
        viewModel.onCreateClicked()

        // Then
        val expected = ViewEvent.Navigation(screen)
        assertEquals(expected, viewModel.viewEvent.first())
    }

    @Test
    fun `When screen opens Then load servers and ping`() = runTest {
        // Given
        val servers = listOf(
            createServerConfig(uuid = "1"),
            createServerConfig(uuid = "2"),
            createServerConfig(uuid = "3"),
        )
        coEvery { serverRepository.loadServers() } returns servers
        coEvery { serverRepository.checkAvailability(any()) } coAnswers { delay(200); 200L }

        // When
        val viewModel = createViewModel() // init {}

        // Then
        val viewState = CloudViewState(
            servers = listOf(
                ServerModel(config = servers[0], status = ServerStatus.Checking),
                ServerModel(config = servers[1], status = ServerStatus.Checking),
                ServerModel(config = servers[2], status = ServerStatus.Checking),
            )
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { serverRepository.checkAvailability(servers[0]) }
        coVerify(exactly = 1) { serverRepository.checkAvailability(servers[1]) }
        coVerify(exactly = 1) { serverRepository.checkAvailability(servers[2]) }
    }

    @Test
    fun `When ping is successful Then display latency`() = runTest {
        // Given
        val latency = 200L
        val servers = listOf(createServerConfig())
        coEvery { serverRepository.loadServers() } returns servers
        coEvery { serverRepository.checkAvailability(any()) } returns latency

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = CloudViewState(
            servers = listOf(
                ServerModel(
                    config = servers[0],
                    status = ServerStatus.Available(latency),
                ),
            )
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { serverRepository.checkAvailability(servers[0]) }
    }

    @Test
    fun `When ping is failed Then display error message`() = runTest {
        // Given
        val exception = UnknownHostException()
        val servers = listOf(createServerConfig())
        coEvery { serverRepository.loadServers() } returns servers
        coEvery { serverRepository.checkAvailability(any()) } throws exception

        // When
        val viewModel = createViewModel() // init {}
        advanceUntilIdle()

        // Then
        val viewState = CloudViewState(
            servers = listOf(
                ServerModel(
                    config = servers[0],
                    status = ServerStatus.Unavailable(exception.message.orEmpty()),
                ),
            )
        )
        assertEquals(viewState, viewModel.viewState.value)

        coVerify(exactly = 1) { serverRepository.checkAvailability(servers[0]) }
    }

    private fun createViewModel(): CloudViewModel {
        return CloudViewModel(serverRepository)
    }
}