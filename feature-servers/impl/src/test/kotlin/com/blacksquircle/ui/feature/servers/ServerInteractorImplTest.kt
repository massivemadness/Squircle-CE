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

package com.blacksquircle.ui.feature.servers

import com.blacksquircle.ui.feature.servers.data.interactor.ServerInteractorImpl
import com.blacksquircle.ui.feature.servers.domain.repository.ServerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ServerInteractorImplTest {

    private val serverRepository = mockk<ServerRepository>(relaxed = true)
    private val serverInteractor = ServerInteractorImpl(serverRepository)

    @Test
    fun `When authentication called Then authenticate in repository`() = runTest {
        // Given
        val uuid = "12345"
        val password = "secret"

        // When
        serverInteractor.authenticate(uuid, password)

        // Then
        coVerify(exactly = 1) { serverRepository.authenticate(uuid, password) }
    }

    @Test
    fun `When load servers Then load from repository`() = runTest {
        // When
        serverInteractor.loadServers()

        // Then
        coVerify(exactly = 1) { serverRepository.loadServers() }
    }

    @Test
    fun `When load server Then load from repository`() = runTest {
        // Given
        val uuid = "12345"

        // When
        serverInteractor.loadServer(uuid)

        // Then
        coVerify(exactly = 1) { serverRepository.loadServer(uuid) }
    }
}