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

import android.content.Context
import android.os.Environment
import com.blacksquircle.ui.feature.explorer.data.workspace.DefaultWorkspaceSource
import com.blacksquircle.ui.feature.explorer.data.workspace.LOCAL_WORKSPACE_ID
import com.blacksquircle.ui.feature.explorer.data.workspace.ROOT_WORKSPACE_ID
import com.scottyab.rootbeer.RootBeer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File

class DefaultWorkspaceSourceTest {

    private val rootBeer = mockk<RootBeer>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private lateinit var defaultWorkspaceSource: DefaultWorkspaceSource

    @Before
    fun setup() {
        val mockkFile = mockk<File>().apply {
            every { absolutePath } returns ""
        }
        mockkStatic(Environment::class)
        every { Environment.getExternalStorageDirectory() } returns mockkFile
    }

    @Test
    fun `When loading workspaces without root Then return local workspace`() = runTest {
        // Given
        coEvery { rootBeer.isRooted } returns false
        defaultWorkspaceSource = DefaultWorkspaceSource(
            rootBeer = rootBeer,
            context = context,
        )

        // When
        val workspaces = defaultWorkspaceSource.workspaceFlow.first()

        // Then
        assertEquals(1, workspaces.size)
        assertEquals(workspaces[0].uuid, LOCAL_WORKSPACE_ID)
    }

    @Test
    fun `When loading workspaces with root Then return local and root workspaces`() = runTest {
        // Given
        coEvery { rootBeer.isRooted } returns true
        defaultWorkspaceSource = DefaultWorkspaceSource(
            rootBeer = rootBeer,
            context = context,
        )

        // When
        val workspaces = defaultWorkspaceSource.workspaceFlow.first()

        // Then
        assertEquals(2, workspaces.size)
        assertEquals(workspaces[0].uuid, LOCAL_WORKSPACE_ID)
        assertEquals(workspaces[1].uuid, ROOT_WORKSPACE_ID)
    }
}