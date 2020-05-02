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

package com.lightteam.javascript.parser

import com.lightteam.javascript.parser.predefined.*
import com.lightteam.language.exception.ParseException
import com.lightteam.language.model.ParseModel
import com.lightteam.language.parser.LanguageParser
import io.reactivex.Single
import org.mozilla.javascript.*

class JavaScriptParser : LanguageParser {

    override fun execute(name: String, source: String): Single<ParseModel> {
        return Single.fromCallable {
            val context = Context.enter()
            context.optimizationLevel = -1
            context.maximumInterpreterStackDepth = 1 // to avoid recursive calls
            try {
                val scope = createRootScope(context)

                // Functions
                scope.put(Block::class.simpleName, scope, Context.javaToJS(Block(), scope))
                scope.put(Entity::class.simpleName, scope, Context.javaToJS(Entity(), scope))
                scope.put(Item::class.simpleName, scope, Context.javaToJS(Item(), scope))
                scope.put(Level::class.simpleName, scope, Context.javaToJS(Level(), scope))
                scope.put(ModPE::class.simpleName, scope, Context.javaToJS(ModPE(), scope))
                scope.put(Player::class.simpleName, scope, Context.javaToJS(Player(), scope))
                scope.put(Server::class.simpleName, scope, Context.javaToJS(Server(), scope))

                // Constants
                scope.put(ArmorType::class.simpleName, scope, Context.javaToJS(ArmorType(), scope))
                scope.put(BlockFace::class.simpleName, scope, Context.javaToJS(BlockFace(), scope))
                scope.put(BlockRenderLayer::class.simpleName, scope, Context.javaToJS(BlockRenderLayer(), scope))
                scope.put(ChatColor::class.simpleName, scope, Context.javaToJS(ChatColor(), scope))
                scope.put(DimensionId::class.simpleName, scope, Context.javaToJS(DimensionId(), scope))
                scope.put(Enchantment::class.simpleName, scope, Context.javaToJS(Enchantment(), scope))
                scope.put(EnchantType::class.simpleName, scope, Context.javaToJS(EnchantType(), scope))
                scope.put(EntityRenderType::class.simpleName, scope, Context.javaToJS(EntityRenderType(), scope))
                scope.put(EntityType::class.simpleName, scope, Context.javaToJS(EntityType(), scope))
                scope.put(ItemCategory::class.simpleName, scope, Context.javaToJS(ItemCategory(), scope))
                scope.put(MobEffect::class.simpleName, scope, Context.javaToJS(MobEffect(), scope))
                scope.put(ParticleType::class.simpleName, scope, Context.javaToJS(ParticleType(), scope))
                scope.put(UseAnimation::class.simpleName, scope, Context.javaToJS(UseAnimation(), scope))

                context.evaluateString(scope, source, name, 1, null)
                return@fromCallable ParseModel(null)
            } catch (e: RhinoException) {
                val parseException = ParseException(e.message, e.lineNumber(), e.columnNumber())
                return@fromCallable ParseModel(parseException)
            } finally {
                Context.exit()
            }
        }
    }

    private fun createRootScope(context: Context): Scriptable {
        val sharedScope = context.initStandardObjects()
        val rootScope = Global().register<Global>(context, sharedScope)
        rootScope.prototype = sharedScope
        rootScope.parentScope = null
        return rootScope
    }

    private inline fun <reified T> FunctionRegistrar.register(context: Context, scope: Scriptable): Scriptable {
        return context.wrapFactory.wrapAsJavaObject(context, scope, this, T::class.java)
    }

    interface FunctionRegistrar
}