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

package com.lightteam.modpeide.data.feature.parser

import com.lightteam.modpeide.data.feature.parser.predefined.*
import com.lightteam.modpeide.domain.exception.ParseException
import com.lightteam.modpeide.domain.feature.parser.SourceParser
import com.lightteam.modpeide.domain.model.editor.ParseModel
import io.reactivex.Single
import org.mozilla.javascript.*

class JavaScriptParser : SourceParser {

    companion object {
        private const val CLASS_BLOCK = "Block"
        private const val CLASS_ENTITY = "Entity"
        private const val CLASS_ITEM = "Item"
        private const val CLASS_LEVEL = "Level"
        private const val CLASS_MODPE = "ModPE"
        private const val CLASS_PLAYER = "Player"
        private const val CLASS_SERVER = "Server"

        private const val CLASS_ARMOR_TYPE = "ArmorType"
        private const val CLASS_BLOCK_FACE = "BlockFace"
        private const val CLASS_BLOCK_RENDER_LAYER = "BlockRenderLayer"
        private const val CLASS_CHAT_COLOR = "ChatColor"
        private const val CLASS_DIMENSION_ID = "DimensionId"
        private const val CLASS_ENCHANTMENT = "Enchantment"
        private const val CLASS_ENCHANT_TYPE = "EnchantType"
        private const val CLASS_ENTITY_RENDER_TYPE = "EntityRenderType"
        private const val CLASS_ENTITY_TYPE = "EntityType"
        private const val CLASS_ITEM_CATEGORY = "ItemCategory"
        private const val CLASS_MOB_EFFECT = "MobEffect"
        private const val CLASS_PARTICLE_TYPE = "ParticleType"
        private const val CLASS_USE_ANIMATION = "UseAnimation"
    }

    override fun execute(sourceName: String, sourceCode: String): Single<ParseModel> {
        return Single.fromCallable {
            val context = Context.enter()
            context.optimizationLevel = -1
            try {
                val scope = createRootScope(context)

                // Functions
                scope.put(CLASS_BLOCK, scope, Context.javaToJS(Block(), scope))
                scope.put(CLASS_ENTITY, scope, Context.javaToJS(Entity(), scope))
                scope.put(CLASS_ITEM, scope, Context.javaToJS(Item(), scope))
                scope.put(CLASS_LEVEL, scope, Context.javaToJS(Level(), scope))
                scope.put(CLASS_MODPE, scope, Context.javaToJS(ModPE(), scope))
                scope.put(CLASS_PLAYER, scope, Context.javaToJS(Player(), scope))
                scope.put(CLASS_SERVER, scope, Context.javaToJS(Server(), scope))

                // Variables
                scope.put(CLASS_ARMOR_TYPE, scope, Context.javaToJS(ArmorType(), scope))
                scope.put(CLASS_BLOCK_FACE, scope, Context.javaToJS(BlockFace(), scope))
                scope.put(CLASS_BLOCK_RENDER_LAYER, scope, Context.javaToJS(BlockRenderLayer(), scope))
                scope.put(CLASS_CHAT_COLOR, scope, Context.javaToJS(ChatColor(), scope))
                scope.put(CLASS_DIMENSION_ID, scope, Context.javaToJS(DimensionId(), scope))
                scope.put(CLASS_ENCHANTMENT, scope, Context.javaToJS(Enchantment(), scope))
                scope.put(CLASS_ENCHANT_TYPE, scope, Context.javaToJS(EnchantType(), scope))
                scope.put(CLASS_ENTITY_RENDER_TYPE, scope, Context.javaToJS(EntityRenderType(), scope))
                scope.put(CLASS_ENTITY_TYPE, scope, Context.javaToJS(EntityType(), scope))
                scope.put(CLASS_ITEM_CATEGORY, scope, Context.javaToJS(ItemCategory(), scope))
                scope.put(CLASS_MOB_EFFECT, scope, Context.javaToJS(MobEffect(), scope))
                scope.put(CLASS_PARTICLE_TYPE, scope, Context.javaToJS(ParticleType(), scope))
                scope.put(CLASS_USE_ANIMATION, scope, Context.javaToJS(UseAnimation(), scope))

                context.evaluateString(scope, sourceCode, sourceName, 1, null)
                return@fromCallable ParseModel(null)
            } catch (e: RhinoException) {
                return@fromCallable ParseModel(
                    ParseException(e.message, e.lineNumber())
                )
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