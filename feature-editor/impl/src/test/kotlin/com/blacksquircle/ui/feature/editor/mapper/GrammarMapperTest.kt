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

package com.blacksquircle.ui.feature.editor.mapper

import com.blacksquircle.ui.feature.editor.data.mapper.GrammarMapper
import com.blacksquircle.ui.feature.editor.data.model.GrammarData
import com.blacksquircle.ui.feature.editor.domain.model.GrammarModel
import org.junit.Assert.assertEquals
import org.junit.Test

class GrammarMapperTest {

    @Test
    fun `When mapping GrammarData Then return GrammarModel`() {
        // Given
        val grammarData = GrammarData(
            name = "html",
            displayName = "HTML",
            scopeName = "text.html.basic",
            grammar = "/storage/emulated/0/grammar.json",
            languageConfiguration = "/storage/emulated/0/config.json",
            embeddedLanguages = mapOf("source.js" to "JavaScript"),
        )
        val expected = GrammarModel(
            name = "html",
            displayName = "HTML",
            scopeName = "text.html.basic",
            grammar = "/storage/emulated/0/grammar.json",
            languageConfiguration = "/storage/emulated/0/config.json",
            embeddedLanguages = mapOf("source.js" to "JavaScript"),
        )

        // When
        val actual = GrammarMapper.toModel(grammarData)

        // Then
        assertEquals(expected, actual)
    }
}