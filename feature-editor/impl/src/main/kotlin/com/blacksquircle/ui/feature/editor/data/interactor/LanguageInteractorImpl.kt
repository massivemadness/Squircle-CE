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
import com.blacksquircle.ui.feature.editor.api.factory.LanguageInteractor
import com.blacksquircle.ui.feature.editor.data.model.AssetsGrammar
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.withContext
import org.eclipse.tm4e.core.registry.IGrammarSource
import timber.log.Timber
import java.io.File

internal class LanguageInteractorImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val context: Context,
) : LanguageInteractor {

    init {
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(context.assets)
        )
    }

    override suspend fun loadGrammar(language: String) {
        withContext(dispatcherProvider.io()) {
            try {
                val grammarRegistry = GrammarRegistry.getInstance()

                /** Check if [language] is in assets */
                val assetsGrammar = AssetsGrammar.find(language)
                if (assetsGrammar != null) {
                    val languageFile = assetsGrammar.languageDir + ".tmLanguage.json"
                    val relativePath = assetsGrammar.languageUri.substring(ASSET_PATH.length)

                    val syntaxRulesPath = "$relativePath/syntaxes/$languageFile"
                    val configurationPath = "$relativePath/language-configuration.json"

                    val grammarSource = IGrammarSource.fromInputStream(
                        /* stream = */ context.assets.open(syntaxRulesPath),
                        /* fileName = */ syntaxRulesPath.substringAfterLast(File.separator),
                        /* charset = */ Charsets.UTF_8
                    )
                    val grammar = DefaultGrammarDefinition
                        .withLanguageConfiguration(
                            /* grammarSource = */ grammarSource,
                            /* languageConfigurationPath = */ configurationPath,
                            /* languageName = */ assetsGrammar.languageName,
                            /* scopeName = */ assetsGrammar.languageId,
                        )
                        .withEmbeddedLanguages(emptyMap()) // TODO refactor

                    grammarRegistry.loadGrammar(grammar)
                    return@withContext
                }

                throw IllegalStateException("Language $language not found")
            } catch (e: Exception) {
                Timber.e(e, "Couldn't load grammar into registry: ${e.message}")
            }
        }
    }

    companion object {
        private const val ASSET_PATH = "file:///android_asset/"
    }
}