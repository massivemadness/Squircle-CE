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

package com.blacksquircle.ui.feature.fonts.interactor

import android.content.Context
import android.graphics.Typeface
import com.blacksquircle.ui.feature.fonts.data.interactor.FontsInteractorImpl
import com.blacksquircle.ui.feature.fonts.data.model.AssetsFont
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class FontsInteractorImplTest {

    private val typeface = mockk<Typeface>()
    private val dispatcherProvider = TestDispatcherProvider()
    private val context = mockk<Context>(relaxed = true)

    private val fontsInteractor = FontsInteractorImpl(
        dispatcherProvider = dispatcherProvider,
        context = context
    )

    @Before
    fun setup() {
        mockkStatic(Typeface::class)
        every { Typeface.createFromAsset(any(), any()) } returns typeface

        every { context.assets } returns mockk()
    }

    @Test
    fun `When loading internal font Then load from assets`() = runTest {
        // Given
        val fontId = AssetsFont.DROID_SANS_MONO.fontId

        // When
        fontsInteractor.loadFont(fontId)

        // Then
        verify(exactly = 1) { Typeface.createFromAsset(any(), any()) }
    }

    @Test
    @Ignore("TODO: Mock font file")
    fun `When loading external font Then load from file`() = runTest {
        // Given
        val fontId = "external font"

        // When
        fontsInteractor.loadFont(fontId)

        // Then
        verify(exactly = 1) { Typeface.createFromFile(any<String>()) }
    }
}