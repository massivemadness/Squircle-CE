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

package com.lightteam.unknown.language

import com.lightteam.language.language.Language
import com.lightteam.language.parser.LanguageParser
import com.lightteam.language.styler.LanguageStyler
import com.lightteam.language.suggestion.SuggestionProvider
import com.lightteam.unknown.parser.UnknownParser
import com.lightteam.unknown.styler.UnknownStyler
import com.lightteam.unknown.suggestions.UnknownSuggestions

class UnknownLanguage : Language {

    private var unknownParser: UnknownParser? = null
    private var unknownStyler: UnknownStyler? = null

    override fun getName(): String {
        return "unknown"
    }

    override fun getParser(): LanguageParser {
        return unknownParser ?: UnknownParser()
            .also { unknownParser = it }
    }

    override fun getSuggestions(): SuggestionProvider {
        return UnknownSuggestions()
    }

    override fun createStyler(): LanguageStyler {
        return UnknownStyler()
            .also { unknownStyler = it }
    }

    override fun cancelStyler() {
        unknownStyler?.cancelStyler()
    }
}