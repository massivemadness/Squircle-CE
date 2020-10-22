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

package com.brackeys.ui.language.javascript.provider

import com.brackeys.ui.language.base.model.SuggestionModel
import com.brackeys.ui.language.base.model.SuggestionType
import com.brackeys.ui.language.base.provider.SuggestionProvider
import com.brackeys.ui.language.base.provider.utils.WordsManager
import com.brackeys.ui.language.javascript.parser.predefined.*

/**
 * This suggestions only used in ModPE Script
 */
class ModPEScriptProvider private constructor() : SuggestionProvider {

    companion object {

        private var modpeScriptProvider: ModPEScriptProvider? = null

        fun getInstance(): ModPEScriptProvider {
            return modpeScriptProvider ?: ModPEScriptProvider().also {
                modpeScriptProvider = it
            }
        }
    }

    private val wordsManager = WordsManager()
    private val modpeScriptApi = hashSetOf<SuggestionModel>()

    init {

        // ModPE Script predefined suggestions
        val modpeApi = arrayOf(
            ArmorType::class.java,
            Block::class.java,
            BlockFace::class.java,
            BlockRenderLayer::class.java,
            ChatColor::class.java,
            DimensionId::class.java,
            Enchantment::class.java,
            EnchantType::class.java,
            Entity::class.java,
            EntityRenderType::class.java,
            EnchantType::class.java,
            Global::class.java,
            Hooks::class.java,
            Item::class.java,
            ItemCategory::class.java,
            Level::class.java,
            MobEffect::class.java,
            ModPE::class.java,
            ParticleType::class.java,
            Player::class.java,
            Server::class.java,
            UseAnimation::class.java
        )
        for (clazz in modpeApi) {
            for (field in clazz.declaredFields) {
                val suggestionModel = SuggestionModel(
                    type = SuggestionType.FIELD,
                    text = clazz.simpleName + "." + field.name,
                    returnType = field.type.simpleName
                )
                modpeScriptApi.add(suggestionModel)
            }
            for (method in clazz.declaredMethods) {
                val suggestionModel = SuggestionModel(
                    type = SuggestionType.METHOD,
                    text = method.name,
                    returnType = method.returnType.simpleName
                )
                modpeScriptApi.add(suggestionModel)
            }
        }

        // JavaScript predefined suggestions
        val function = SuggestionModel(
            type = SuggestionType.WORD,
            text = "function",
            returnType = ""
        )
        modpeScriptApi.add(function)
        // TODO add
    }

    override fun getAll(): Set<SuggestionModel> {
        return modpeScriptApi + wordsManager.getWords()
            .map {
                SuggestionModel(
                    type = SuggestionType.WORD,
                    text = it.value,
                    returnType = ""
                )
            }
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