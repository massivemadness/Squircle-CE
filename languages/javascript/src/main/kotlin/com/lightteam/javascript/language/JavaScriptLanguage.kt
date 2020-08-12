/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.javascript.language

import com.lightteam.javascript.parser.JavaScriptParser
import com.lightteam.javascript.styler.JavaScriptStyler
import com.lightteam.javascript.suggestions.ModPESuggestions
import com.lightteam.language.language.Language
import com.lightteam.language.parser.LanguageParser
import com.lightteam.language.scheme.SyntaxScheme
import com.lightteam.language.styler.utils.Styleable
import com.lightteam.language.suggestion.SuggestionProvider

class JavaScriptLanguage : Language {

    companion object {
        const val FILE_EXTENSION = ".js"
    }

    private var javaScriptParser: JavaScriptParser? = null
    private var javaScriptStyler: JavaScriptStyler? = null
    private var suggestionProvider: SuggestionProvider? = null

    override fun getName(): String {
        return "javascript"
    }

    override fun getParser(): LanguageParser {
        return javaScriptParser ?: JavaScriptParser()
            .also { javaScriptParser = it }
    }

    override fun getSuggestions(): SuggestionProvider {
        return suggestionProvider ?: ModPESuggestions()
            .also { suggestionProvider = it }
    }

    override fun executeStyler(sourceCode: String, syntaxScheme: SyntaxScheme, styleable: Styleable) {
        javaScriptStyler = JavaScriptStyler().also {
            it.executeTask(sourceCode, syntaxScheme, styleable)
        }
    }

    override fun cancelStyler() {
        javaScriptStyler?.cancelTask()
    }
}