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

package com.lightteam.language.plaintext

import com.lightteam.language.base.Language
import com.lightteam.language.base.parser.LanguageParser
import com.lightteam.language.base.provider.SuggestionProvider
import com.lightteam.language.base.styler.LanguageStyler
import com.lightteam.language.plaintext.parser.PlainTextParser
import com.lightteam.language.plaintext.provider.PlainTextProvider
import com.lightteam.language.plaintext.styler.PlainTextStyler

class PlainTextLanguage : Language {

    override fun getName(): String {
        return "plaintext"
    }

    override fun getParser(): LanguageParser {
        return PlainTextParser.getInstance()
    }

    override fun getProvider(): SuggestionProvider {
        return PlainTextProvider.getInstance()
    }

    override fun getStyler(): LanguageStyler {
        return PlainTextStyler.getInstance()
    }
}