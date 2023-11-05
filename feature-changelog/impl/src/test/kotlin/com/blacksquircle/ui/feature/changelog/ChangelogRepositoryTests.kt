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

package com.blacksquircle.ui.feature.changelog

import android.content.Context
import android.content.res.Resources
import com.blacksquircle.ui.core.tests.TestDispatcherProvider
import com.blacksquircle.ui.feature.changelog.data.repository.ChangelogRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test
import java.io.InputStream

class ChangelogRepositoryTests {

    @Test
    @Ignore("Unstable on CI")
    fun `When loading changelog Then read data from resource file`() = runTest {
        // Given
        val resources = mockk<Resources>()
        val context = mockk<Context>()
        every { context.resources } returns resources
        every { resources.openRawResource(any()) } returns InputStream.nullInputStream()

        val repository = ChangelogRepositoryImpl(
            dispatcherProvider = TestDispatcherProvider(),
            context = context
        )

        // When
        repository.loadChangelog()

        // Then
        verify(exactly = 1) { resources.openRawResource(R.raw.changelog) }
    }
}