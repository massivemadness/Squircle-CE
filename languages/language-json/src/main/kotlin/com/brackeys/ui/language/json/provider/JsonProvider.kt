/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.language.json.provider

import com.brackeys.ui.language.base.model.SuggestionModel
import com.brackeys.ui.language.base.model.SuggestionType
import com.brackeys.ui.language.base.provider.SuggestionProvider
import com.brackeys.ui.language.base.provider.utils.WordsManager

class JsonProvider private constructor() : SuggestionProvider {

    companion object {

        private var jsonProvider: JsonProvider? = null

        fun getInstance(): JsonProvider {
            return jsonProvider ?: JsonProvider().also {
                jsonProvider = it
            }
        }
    }

    private val wordsManager = WordsManager()

    override fun getAll(): Set<SuggestionModel> {
        return wordsManager.getWords()
            .map {
                SuggestionModel(
                    type = SuggestionType.WORD,
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