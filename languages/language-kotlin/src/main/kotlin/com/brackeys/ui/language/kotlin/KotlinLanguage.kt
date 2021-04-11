/*
 * Copyright 2021 Brackeys IDE contributors.
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

package com.brackeys.ui.language.kotlin

import com.brackeys.ui.language.base.Language
import com.brackeys.ui.language.base.parser.LanguageParser
import com.brackeys.ui.language.base.provider.SuggestionProvider
import com.brackeys.ui.language.base.styler.LanguageStyler
import com.brackeys.ui.language.kotlin.parser.KotlinParser
import com.brackeys.ui.language.kotlin.provider.KotlinProvider
import com.brackeys.ui.language.kotlin.styler.KotlinStyler

class KotlinLanguage : Language {

    companion object {

        private const val FILE_EXTENSION = ".kt"

        fun supportFormat(fileName: String): Boolean {
            return fileName.endsWith(FILE_EXTENSION, ignoreCase = true)
        }
    }

    override fun getName(): String {
        return "kotlin"
    }

    override fun getParser(): LanguageParser {
        return KotlinParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return KotlinProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return KotlinStyler.getInstance()
    }
}