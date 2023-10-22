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

package com.blacksquircle.ui.feature.settings

import android.content.Context
import com.blacksquircle.ui.core.storage.keyvalue.SettingsManager
import com.blacksquircle.ui.feature.settings.data.repository.SettingsRepositoryImpl
import com.blacksquircle.ui.feature.settings.domain.model.KeyModel
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SettingsRepositoryTests {

    @Test
    fun `When loading keyboard preset Then return keys list containing a tab`() {
        // Given
        val repository = SettingsRepositoryImpl(
            settingsManager = mockk<SettingsManager>().apply {
                every { keyboardPreset } returns "ABC"
            },
            context = mockk<Context>().apply {
                every { getString(any()) } returns "Tab"
            },
        )

        // When
        val actual = repository.keyboardPreset()

        // Then
        val expected = listOf(
            KeyModel(display = "Tab", value = '\t'),
            KeyModel(display = "A", value = 'A'),
            KeyModel(display = "B", value = 'B'),
            KeyModel(display = "C", value = 'C'),
        )
        assertEquals(expected, actual)
    }
}