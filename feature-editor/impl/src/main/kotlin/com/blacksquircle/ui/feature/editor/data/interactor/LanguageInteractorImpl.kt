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

package com.blacksquircle.ui.feature.editor.data.interactor

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.editor.data.mapper.GrammarMapper
import com.blacksquircle.ui.feature.editor.data.model.GrammarData
import com.blacksquircle.ui.feature.editor.domain.interactor.LanguageInteractor
import com.blacksquircle.ui.feature.editor.domain.model.GrammarModel
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

internal class LanguageInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val jsonParser: Json,
    private val context: Context,
) : LanguageInteractor {

    private var grammars = emptyList<GrammarModel>()

    init {
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(context.assets)
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun loadGrammars(): List<GrammarModel> {
        return withContext(dispatcherProvider.io()) {
            if (grammars.isNotEmpty()) {
                return@withContext grammars
            }
            val languagesFile = context.assets.open(ASSET_FILE)
            grammars = jsonParser.decodeFromStream<List<GrammarData>>(languagesFile)
                .map(GrammarMapper::toModel)
            grammars
        }
    }

    override suspend fun registerGrammar(language: String) {
        withContext(dispatcherProvider.io()) {
            val grammar = grammars.find { it.scopeName == language }
            if (grammar == null) {
                return@withContext
            }
            if (GrammarRegistry.getInstance().findGrammar(language) != null) {
                return@withContext
            }

            val grammarDefinition = GrammarMapper.toDefinition(grammar)
            GrammarRegistry.getInstance().loadGrammar(grammarDefinition)

            grammar.embeddedLanguages.forEach { (_, name) ->
                val embeddedGrammar = grammars.find { it.name == name }
                if (embeddedGrammar != null) {
                    registerGrammar(embeddedGrammar.scopeName)
                }
            }
        }
    }

    companion object {
        private const val ASSET_FILE = "languages.json"
    }
}