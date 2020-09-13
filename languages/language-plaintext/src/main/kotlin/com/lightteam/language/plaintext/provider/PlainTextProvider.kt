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

package com.lightteam.language.plaintext.provider

import com.lightteam.language.base.model.SuggestionModel
import com.lightteam.language.base.model.SuggestionType
import com.lightteam.language.base.provider.SuggestionProvider
import com.lightteam.language.base.provider.utils.WordsManager

class PlainTextProvider private constructor() : SuggestionProvider {

    companion object {

        private var plainTextProvider: PlainTextProvider? = null

        fun getInstance(): PlainTextProvider {
            return plainTextProvider ?: PlainTextProvider().also {
                plainTextProvider = it
            }
        }
    }

    private val wordsManager = WordsManager()

    override fun getAll(): Set<SuggestionModel> {
        return wordsManager.getWords()
            .map {
                SuggestionModel(
                    type = SuggestionType.NONE,
                    text = it.value,
                    returnType = ""
                )
            }
            .toHashSet()
    }

    override fun processLine(lineNumber: Int, text: String) {
        wordsManager.processLine(lineNumber, text)
    }

    override fun deleteLine(lineNumber: Int) {
        wordsManager.deleteLine(lineNumber)
    }

    override fun clearLines() {
        wordsManager.clearLines()
    }
}