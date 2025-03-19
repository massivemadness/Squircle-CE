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

package com.blacksquircle.ui.feature.editor.data.factory

import android.content.Context
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.feature.editor.api.factory.LanguageFactory
import io.github.rosemoe.sora.lang.EmptyLanguage
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.dsl.languages
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver

internal class LanguageFactoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val context: Context,
) : LanguageFactory {

    init {
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(context.assets)
        )
    }

    override suspend fun create(languageName: String): Language {
        return with(dispatcherProvider.io()) {
            val languageScope = "source.js"

            GrammarRegistry.getInstance().loadGrammars(
                languages {
                    language("js") {
                        grammar = "languages/javascript/syntaxes/javascript.tmLanguage.json"
                        defaultScopeName()
                        languageConfiguration = "languages/javascript/language-configuration.json"
                    }
                }
            )

            try {
                TextMateLanguage.create(languageScope, true)
            } catch (e: Exception) {
                EmptyLanguage()
            }
        }
    }
}