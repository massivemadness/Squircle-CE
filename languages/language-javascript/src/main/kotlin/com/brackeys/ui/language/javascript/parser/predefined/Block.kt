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
internal class Block {
    fun defineBlock(id: Int, name: String, scriptable: Any, type: Any, transparency: Any, render: Any) {}
    fun defineLiquidBlock(id: Int, name: String, texture: Any, type: Any) {}
    fun getAllBlockIds() {}
    fun getDestroyTime(id: Int, data: Int) {}
    fun getFriction(id: Int, data: Int) {}
    fun getRenderType(id: Int) {}
    fun getTextureCoords(id: Int, count: Int, data: Int) {}
    fun setColor(id: Int, htmlColor: Scriptable) {}
    fun setDestroyTime(id: Int, time: Double) {}
    fun setExplosionResistance(id: Int, resistance: Double) {}
    fun setFriction(id: Int, doubleValue: Double) {}
    fun setLightLevel(id: Int, level: Int) {}
    fun setLightOpacity(id: Int, opacity: Int) {}
    fun setRedstoneConsumer(id: Int, someBoolean: Boolean) {}
    fun setRenderLayer(id: Int, layer: Int) {}
    fun setRenderType(id: Int, render: Int) {}
    fun setShape(id: Int, v1: Double, v2: Double, v3: Double, v4: Double, v5: Double, v6: Double, data: Int) {}
}