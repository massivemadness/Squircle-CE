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

package com.blacksquircle.ui.feature.editor.data.mapper

import com.blacksquircle.ui.feature.editor.data.model.GrammarData
import com.blacksquircle.ui.feature.editor.domain.model.GrammarModel
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarDefinition
import org.eclipse.tm4e.core.registry.IGrammarSource
import java.nio.charset.Charset

internal object GrammarMapper {

    fun toModel(grammarData: GrammarData): GrammarModel {
        return GrammarModel(
            name = grammarData.name.orEmpty(),
            displayName = grammarData.displayName.orEmpty(),
            scopeName = grammarData.scopeName.orEmpty(),
            grammar = grammarData.grammar.orEmpty(),
            languageConfiguration = grammarData.languageConfiguration.orEmpty(),
            embeddedLanguages = grammarData.embeddedLanguages.orEmpty(),
        )
    }

    fun toDefinition(grammarModel: GrammarModel): GrammarDefinition {
        return DefaultGrammarDefinition.withLanguageConfiguration(
            // grammarSource =
            IGrammarSource.fromInputStream(
                FileProviderRegistry.getInstance()
                    .tryGetInputStream(grammarModel.grammar),
                grammarModel.grammar,
                Charset.defaultCharset()
            ),
            /* languageConfigurationPath = */ grammarModel.languageConfiguration,
            /* languageName = */ grammarModel.name,
            /* scopeName = */ grammarModel.scopeName,
        ).withEmbeddedLanguages(grammarModel.embeddedLanguages)
    }
}