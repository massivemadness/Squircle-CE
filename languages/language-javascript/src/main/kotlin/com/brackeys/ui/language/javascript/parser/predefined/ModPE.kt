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

/**
 * This class is a part of ModPE API. Simply ignore it.
 */
@Suppress("unused", "unused_parameter")
internal class ModPE {
    fun dumpVtable(className: String, vtableSize: Int) {}
    fun getBytesFromTexturePack(texture: String) {}
    fun getI18n(name: String) {}
    fun getLanguage() {}
    fun getMinecraftVersion() {}
    fun getOS() {}
    fun joinServer(address: String, port: String) {}
    fun langEdit(key: String, value: String) {}
    fun leaveGame() {}
    fun log(text: String) {}
    fun openInputStreamFromTexturePack(texture: String) {}
    fun overrideTexture(file: String, url: String) {}
    fun readData(prefName: String) {}
    fun removeData(prefName: String) {}
    fun resetFov() {}
    fun resetImages() {}
    fun saveData(prefName: String, prefData: String) {}
    fun selectLevel(worldName: String) {}
    fun setCamera(ent: Any) {}
    fun setFoodItem(id: Int, iconName: String, offset: Int, halfhearts: Int, name: String, maxStack: Int) {}
    fun setFoodItem(id: Int, iconName: String, offset: Int, halfhearts: Int, name: String) {}
    fun setFov(fov: Double) {}
    fun setGameSpeed(ticks: Double) {}
    fun setGuiBlocks(url: String) {}
    fun setItem(id: Int, iconName: String, offset: Int, name: String, maxStack: Int) {}
    fun setItem(id: Int, iconName: String, offset: Int, name: String) {}
    fun setItems(url: String) {}
    fun setTerrain(url: String) {}
    fun setUiRenderDebug(someBoolean: Boolean) {}
    fun showTipMessage(text: String) {}
    fun takeScreenshot(filename: String) {}
}