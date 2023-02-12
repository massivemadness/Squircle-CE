/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.language.actionscript

import com.blacksquircle.ui.language.actionscript.parser.ActionScriptParser
import com.blacksquircle.ui.language.actionscript.provider.ActionScriptProvider
import com.blacksquircle.ui.language.actionscript.styler.ActionScriptStyler
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.base.parser.LanguageParser
import com.blacksquircle.ui.language.base.provider.SuggestionProvider
import com.blacksquircle.ui.language.base.styler.LanguageStyler

class ActionScriptLanguage : Language {

    companion object {

        const val LANGUAGE_NAME = "actionscript"

        fun supportFormat(fileName: String): Boolean {
            return fileName.endsWith(".as")
        }
    }

    override val languageName = LANGUAGE_NAME

    override fun getParser(): LanguageParser {
        return ActionScriptParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return ActionScriptProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return ActionScriptStyler.getInstance()
    }
}