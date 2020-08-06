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

package com.lightteam.javascript.parser.predefined

/**
 * This class is a part of ModPE API. Simply ignore it.
 */
@Suppress("unused", "unused_parameter")
internal class Level {
    fun addParticle(type: Int, x: Double, y: Double, z: Double, velX: Double, velY: Double, velZ: Double, size: Int) {}
    fun biomeIdToName(biomeId: Int) {}
    fun canSeeSky(x: Int, y: Int, z: Int) {}
    fun destroyBlock(x: Int, y: Int, z: Int, someBoolean: Boolean) {}
    fun dropItem(x: Double, y: Double, z: Double, range: Float, id: Int, count: Int, data: Int) {}
    fun executeCommand(cmd: String, output: Boolean) {}
    fun explode(x: Double, y: Double, z: Double, radius: Double, bool: Boolean, bool2: Boolean, someDouble: Double) {}
    fun explode(x: Double, y: Double, z: Double, radius: Double) {}
    fun getAddress() {}
    fun getBiome(x: Int, z: Int) {}
    fun getBiomeName(x: Int, z: Int) {}
    fun getBrightness(x: Int, y: Int, z: Int) {}
    fun getChestSlot(x: Int, y: Int, z: Int, slotNumber: Int) {}
    fun getChestSlotCount(x: Int, y: Int, z: Int, slotNumber: Int) {}
    fun getChestSlotData(x: Int, y: Int, z: Int, slotNumber: Int) {}
    fun getData(x: Int, y: Int, z: Int) {}
    fun getDifficulty() {}
    fun getFurnaceSlot(x: Int, y: Int, z: Int, slotNumber: Int) {}
    fun getFurnaceSlotCount(x: Int, y: Int, z: Int, slotNumber: Int) {}
    fun getFurnaceSlotData(x: Int, y: Int, z: Int, slotNumber: Int) {}
    fun getGameMode() {}
    fun getGrassColor(id: Int, data: Int) {}
    fun getLightningLevel() {}
    fun getRainLevel() {}
    fun getSignText(x: Int, y: Int, z: Int, line: Int) {}
    fun getSpawnerEntityType(x: Int, y: Int, z: Int) {}
    fun getTile(x: Int, y: Int, z: Int) {}
    fun getTime() {}
    fun getWorldDir() {}
    fun getWorldName() {}
    fun isRemote() {}
    fun playSound(x: Double, y: Double, z: Double, sound: String, volume: Double, pitch: Double) {}
    fun playSoundEnt(ent: Any, sound: String, volume: Double, pitch: Double) {}
    fun setBlockExtraData(x: Int, y: Int, z: Int, data: Int) {}
    fun setChestSlot(x: Int, y: Int, z: Int, slot: Int, id: Int, data: Int, count: Int) {}
    fun setChestSlotCustomName(x: Int, y: Int, z: Int, slot: Int, name: String) {}
    fun setDifficulty(difficulty: Int) {}
    fun setFurnaceSlot(x: Int, y: Int, z: Int, slot: Int, id: Int, data: Int, count: Int) {}
    fun setGameMode(mode: Int) {}
    fun setGrassColor(x: Int, z: Int, htmlColor: Int) {}
    fun setLightningLevel(lightingLevel: Double) {}
    fun setNightMode(someBoolean: Boolean) {}
    fun setRainLevel(rainLevel: Double) {}
    fun setSignText(x: Int, y: Int, z: Int, line: Int, text: String) {}
    fun setSpawn(x: Int, y: Int, z: Int) {}
    fun setSpawnerEntityType(x: Int, y: Int, z: Int, mobId: Int) {}
    fun setTile(x: Int, y: Int, z: Int, id: Int, data: Int) {}
    fun setTile(x: Int, y: Int, z: Int, id: Int) {}
    fun setTime(time: Int) {}
    fun spawnChicken(x: Double, y: Double, z: Double, texture: String) {}
    fun spawnCow(x: Double, y: Double, z: Double, texture: String) {}
    fun spawnMob(x: Double, y: Double, z: Double, mobId: Int, texture: String) {}
}