/*
 * Copyright Squircle CE contributors.
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

package com.blacksquircle.ui.feature.explorer.workspace

import com.blacksquircle.ui.core.database.dao.workspace.WorkspaceDao
import com.blacksquircle.ui.core.database.entity.workspace.WorkspaceEntity
import com.blacksquircle.ui.feature.explorer.data.workspace.UserWorkspaceSource
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.filesystem.local.LocalFilesystem
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UserWorkspaceSourceTest {

    private val workspaceDao = mockk<WorkspaceDao>(relaxed = true)

    private lateinit var userWorkspaceSource: UserWorkspaceSource

    @Test
    fun `When loading user workspaces Then return workspaces`() = runTest {
        // Given
        val workspaceId = "12345"
        val workspaceEntity = WorkspaceEntity(
            uuid = workspaceId,
            name = "Custom",
            type = WorkspaceType.CUSTOM.value,
            fileUri = "file://...",
            filesystemUuid = LocalFilesystem.LOCAL_UUID,
        )
        coEvery { workspaceDao.flowAll() } returns flowOf(listOf(workspaceEntity))

        userWorkspaceSource = UserWorkspaceSource(workspaceDao)

        // When
        val workspaces = userWorkspaceSource.workspaceFlow.first()

        // Then
        assertEquals(1, workspaces.size)
        assertEquals(workspaces[0].uuid, workspaceId)
    }
}