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

package com.lightteam.modpeide.data.parser

import com.lightteam.modpeide.data.parser.api.*
import com.lightteam.modpeide.domain.model.AnalysisModel
import io.reactivex.Single
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.Scriptable

object ScriptEngine {

    private const val CLASS_BLOCK = "Block"
    private const val CLASS_ENTITY = "Entity"
    private const val CLASS_ITEM = "Item"
    private const val CLASS_LEVEL = "Level"
    private const val CLASS_MODPE = "ModPE"
    private const val CLASS_PLAYER = "Player"
    private const val CLASS_SERVER = "Server"
    private const val CLASS_ITEMCATEGORY = "ItemCategory"

    fun analyze(sourceName: String, sourceCode: String): Single<AnalysisModel> {
        return Single
            .fromCallable {
                try {
                    val context: Context = Context.enter()
                    val scope: Scriptable = ImporterTopLevel(context)

                    context.optimizationLevel = -1

                    scope.put(CLASS_BLOCK, scope, Context.javaToJS(Block(), scope))
                    scope.put(CLASS_ENTITY, scope, Context.javaToJS(Entity(), scope))
                    scope.put(CLASS_ITEM, scope, Context.javaToJS(Item(), scope))
                    scope.put(CLASS_LEVEL, scope, Context.javaToJS(Level(), scope))
                    scope.put(CLASS_MODPE, scope, Context.javaToJS(ModPE(), scope))
                    scope.put(CLASS_PLAYER, scope, Context.javaToJS(Player(), scope))
                    scope.put(CLASS_SERVER, scope, Context.javaToJS(Server(), scope))
                    scope.put(CLASS_ITEMCATEGORY, scope, Context.javaToJS(ItemCategory(), scope))

                    context.evaluateString(scope, sourceCode, sourceName, 1, null)
                    return@fromCallable AnalysisModel(null)
                } catch (e: RuntimeException) {
                    return@fromCallable AnalysisModel(e)
                }
            }
    }
}