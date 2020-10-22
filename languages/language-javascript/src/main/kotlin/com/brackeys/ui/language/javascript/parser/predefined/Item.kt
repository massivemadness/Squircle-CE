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

package com.brackeys.ui.language.javascript.parser.predefined

import org.mozilla.javascript.Scriptable

/**
 * This class is a part of ModPE API. Simply ignore it.
 */
@Suppress("unused", "unused_parameter")
internal class Item {
    fun addCraftRecipe(id: Int, count: Int, data: Int, recipe: Scriptable) {}
    fun addFurnaceRecipe(inputId: Int, outputId: Int, outputDamage: Int) {}
    fun addShapedRecipe(id: Int, count: Int, data: Int, items: Scriptable, recipe: Scriptable) {}
    fun defineArmor(id: Int, iconName: String, offset: Int, name: String, texture: String, damageReduceAmount: Int, maxDamage: Int, armorType: Int) {}
    fun defineThrowable(id: Int, textureName: String, textureGroup: Int, itemName: String, stackLimit: Int) {}
    fun getCustomThrowableRenderType(id: Int) {}
    fun getMaxDamage(id: Int) {}
    fun getMaxStackSize(id: Int) {}
    fun getName(id: Int, data: Int, raw: Boolean) {}
    fun getTextureCoords(id: Int, data: Int) {}
    fun getUseAnimation(id: Int) {}
    fun internalNameToId(name: String) {}
    fun isValidItem(id: Int) {}
    fun setAllowOffhand(itemId: Int, allowOffhand: Boolean) {}
    fun setCategory(id: Int, category: Int) {}
    fun setEnchantType(id: Int, data: Int, enchantType: Int) {}
    fun setHandEquipped(id: Int, isHandEquipped: Boolean) {}
    fun setMaxDamage(id: Int, damage: Int) {}
    fun setProperties(id: Int, properties: Any) {}
    fun setStackedByData(id: Int, someBoolean: Boolean) {}
    fun setUseAnimation(id: Int, animation: Int) {}
    fun translatedNameToId(name: String) {}
}