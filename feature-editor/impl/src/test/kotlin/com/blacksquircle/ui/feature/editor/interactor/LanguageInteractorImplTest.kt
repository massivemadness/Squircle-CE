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

package com.blacksquircle.ui.feature.editor.interactor

import android.content.Context
import android.content.res.AssetManager
import com.blacksquircle.ui.feature.editor.data.interactor.LanguageInteractorImpl
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.eclipse.tm4e.core.registry.IGrammarSource
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.InputStream

class LanguageInteractorImplTest {

    private val grammarRegistry = mockk<GrammarRegistry>(relaxed = true)
    private val grammarSource = mockk<IGrammarSource>(relaxed = true)
    private val dispatcherProvider = TestDispatcherProvider()
    private val jsonParser = mockk<Json>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private lateinit var languageInteractor: LanguageInteractor

    @Before
    fun setup() {
        mockkStatic(GrammarRegistry::class)
        mockkStatic(IGrammarSource::class)

        every { GrammarRegistry.getInstance() } returns grammarRegistry
        every { IGrammarSource.fromInputStream(any(), any(), any()) } returns grammarSource

        every { context.assets } returns mockk<AssetManager>().apply {
            every { open(any()) } returns InputStream.nullInputStream()
        }

        languageInteractor = LanguageInteractorImpl(
            dispatcherProvider = dispatcherProvider,
            jsonParser = jsonParser,
            context = context
        )
    }

    @Test
    @Ignore("TODO: Mock json")
    fun `When registerGrammar called Then load grammar file into registry`() = runTest {
        // Given
        val language = "source.js"

        // When
        languageInteractor.loadGrammars()
        languageInteractor.registerGrammar(language)

        // Then
        verify(exactly = 1) { grammarRegistry.loadGrammar(any()) }
    }
}