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

package com.blacksquircle.ui.language.javascript.parser

import com.blacksquircle.ui.language.base.exception.ParseException
import com.blacksquircle.ui.language.base.model.ParseResult
import com.blacksquircle.ui.language.base.parser.LanguageParser
import org.mozilla.javascript.*

class JavaScriptParser private constructor() : LanguageParser {

    companion object {

        private var javaScriptParser: JavaScriptParser? = null

        fun getInstance(): JavaScriptParser {
            return javaScriptParser ?: JavaScriptParser().also {
                javaScriptParser = it
            }
        }
    }

    override fun execute(name: String, source: String): ParseResult {
        val context = Context.enter()
        context.optimizationLevel = -1
        context.maximumInterpreterStackDepth = 1 // to avoid recursive calls
        return try {
            val scope = context.initStandardObjects()
            context.evaluateString(scope, source, name, 1, null)
            ParseResult(null)
        } catch (e: RhinoException) {
            val parseException = ParseException(e.message, e.lineNumber(), e.columnNumber())
            ParseResult(parseException)
        } finally {
            Context.exit()
        }
    }
}