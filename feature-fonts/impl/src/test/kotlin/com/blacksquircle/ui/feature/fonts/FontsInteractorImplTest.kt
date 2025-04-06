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

package com.blacksquircle.ui.feature.fonts

import android.content.Context
import android.graphics.Typeface
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.core.tests.TestDispatcherProvider
import com.blacksquircle.ui.feature.fonts.data.interactor.FontsInteractorImpl
import com.blacksquircle.ui.feature.fonts.data.model.AssetsFont
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Ignore
import org.junit.Test

class FontsInteractorImplTest {

    private val dispatcherProvider = TestDispatcherProvider()
    private val context = mockk<Context>()

    private val fontsInteractor = FontsInteractorImpl(dispatcherProvider, context)

    @Test
    fun `When loading internal font Then load from assets`() = runTest {
        // Given
        val fontId = AssetsFont.DROID_SANS_MONO.fontId
        mockkStatic(Typeface::class)
        every { Typeface.createFromAsset(any(), any()) } returns mockk()
        every { context.assets } returns mockk()

        // When
        fontsInteractor.loadFont(fontId)

        // Then
        verify(exactly = 1) { Typeface.createFromAsset(any(), any()) }
    }

    @Test
    @Ignore("Need more time to figure it out")
    fun `When loading external font Then load from file`() = runTest {
        // Given
        val fontId = "external_font_id"
        val fontPath = "fonts"

        mockkObject(Directories)
        every { Directories.fontsDir(context) } returns mockk()
        mockkStatic(Typeface::class)
        every { Typeface.createFromFile(any<String>()) } returns mockk()

        // When
        fontsInteractor.loadFont(fontId)

        // Then
        verify(exactly = 1) { Typeface.createFromFile(fontPath) }
    }
}