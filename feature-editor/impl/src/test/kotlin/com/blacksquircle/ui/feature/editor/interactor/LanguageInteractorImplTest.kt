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
import com.blacksquircle.ui.feature.editor.data.interactor.LanguageInteractorImpl
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.test.provider.TestDispatcherProvider
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class LanguageInteractorImplTest {

    private val grammarRegistry = mockk<GrammarRegistry>(relaxed = true)
    private val dispatcherProvider = TestDispatcherProvider()
    private val context = mockk<Context>(relaxed = true)

    private lateinit var languageInteractor: LanguageInteractor

    @Before
    fun setup() {
        mockkStatic(GrammarRegistry::class)
        every { GrammarRegistry.getInstance() } returns grammarRegistry
        every { grammarRegistry.loadGrammars(any<String>()) } returns emptyList()

        every { context.assets } returns mockk()

        languageInteractor = LanguageInteractorImpl(
            dispatcherProvider = dispatcherProvider,
            context = context
        )
    }

    @Test
    fun `When loadGrammars called Then load grammar files into registry`() = runTest {
        // When
        languageInteractor.loadGrammars()

        // Then
        verify(exactly = 1) { grammarRegistry.loadGrammars(any<String>()) }
    }
}