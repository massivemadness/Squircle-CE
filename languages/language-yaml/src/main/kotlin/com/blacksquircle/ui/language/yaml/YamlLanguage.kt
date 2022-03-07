/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.language.yaml

import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.parser.LanguageParser
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.endsWith
import com.blacksquircle.ui.language.yaml.parser.YamlParser
import com.blacksquircle.ui.language.yaml.provider.YamlProvider
import com.blacksquircle.ui.language.yaml.styler.YamlStyler

class YamlLanguage : Language {

    companion object {

        private val FILE_EXTENSIONS = arrayOf(".yaml", ".yml")

        fun supportFormat(fileName: String): Boolean {
            return fileName.endsWith(FILE_EXTENSIONS)
        }
    }

    override val languageName = "yaml"

    override fun getParser(): LanguageParser {
        return YamlParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return YamlProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return YamlStyler.getInstance()
    }
}