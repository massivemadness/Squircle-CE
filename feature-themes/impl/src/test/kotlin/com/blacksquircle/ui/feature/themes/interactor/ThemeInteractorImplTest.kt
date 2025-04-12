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

package com.blacksquircle.ui.feature.themes.interactor

import android.content.Context
import android.content.res.AssetManager
import com.blacksquircle.ui.core.files.Directories
import com.blacksquircle.ui.feature.themes.data.interactor.ThemeInteractorImpl
import com.blacksquircle.ui.feature.themes.data.model.AssetsTheme
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.eclipse.tm4e.core.registry.IThemeSource
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.io.InputStream

class ThemeInteractorImplTest {

    private val themeRegistry = mockk<ThemeRegistry>(relaxed = true)
    private val themeSource = mockk<IThemeSource>(relaxed = true)
    private val dispatcherProvider = TestDispatcherProvider()
    private val jsonParser = mockk<Json>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private val themesInteractor = ThemeInteractorImpl(
        dispatcherProvider = dispatcherProvider,
        jsonParser = jsonParser,
        context = context,
    )

    @Before
    fun setup() {
        mockkStatic(ThemeRegistry::class)
        mockkStatic(IThemeSource::class)

        every { ThemeRegistry.getInstance() } returns themeRegistry
        every { IThemeSource.fromInputStream(any(), any(), any()) } returns themeSource
        every { IThemeSource.fromFile(any()) } returns themeSource

        every { context.assets } returns mockk<AssetManager>().apply {
            every { open(any()) } returns InputStream.nullInputStream()
        }

        mockkObject(Directories)
        every { Directories.themesDir(context) } returns mockk<File>().apply {
            every { path } returns ""
        }
    }

    @Test
    @Ignore("TODO: Mock json")
    fun `When loading internal theme Then load from assets`() = runTest {
        // Given
        val themeId = AssetsTheme.THEME_DARCULA.themeId

        // When
        themesInteractor.loadTheme(themeId)

        // Then
        verify(exactly = 1) { IThemeSource.fromInputStream(any(), any(), any()) }
        verify(exactly = 1) { themeRegistry.loadTheme(themeSource, true) }
    }

    @Test
    @Ignore("TODO: Mock theme file")
    fun `When loading external theme Then load from file`() = runTest {
        // Given
        val themeId = "external theme"

        // When
        themesInteractor.loadTheme(themeId)

        // Then
        verify(exactly = 1) { IThemeSource.fromFile(any()) }
        verify(exactly = 1) { themeRegistry.loadTheme(themeSource, true) }
    }
}