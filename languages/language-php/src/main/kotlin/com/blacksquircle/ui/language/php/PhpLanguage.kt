/*
 * Copyright 2021 Squircle IDE contributors.
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

package com.blacksquircle.ui.language.php

import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.parser.LanguageParser
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.base.utils.endsWith
import com.blacksquircle.ui.language.php.parser.PhpParser
import com.blacksquircle.ui.language.php.provider.PhpProvider
import com.blacksquircle.ui.language.php.styler.PhpStyler

class PhpLanguage : Language {

    companion object {

        private val FILE_EXTENSIONS = arrayOf(".php", ".php3", ".php4", ".php5", ".phps", ".phtml")

        fun supportFormat(fileName: String): Boolean {
            return fileName.endsWith(FILE_EXTENSIONS)
        }
    }

    override fun getName(): String {
        return "php"
    }

    override fun getParser(): LanguageParser {
        return PhpParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return PhpProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return PhpStyler.getInstance()
    }
}