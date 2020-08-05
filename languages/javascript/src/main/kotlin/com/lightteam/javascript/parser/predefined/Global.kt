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

import com.lightteam.javascript.parser.JavaScriptParser

/**
 * This class is a part of ModPE API. Simply ignore it.
 */
@Suppress("unused", "unused_parameter")
internal class Global : JavaScriptParser.FunctionRegistrar {

    // Entity
    fun bl_setMobSkin(ent: Any, image: String) {}
    fun bl_spawnMob(x: Double, y: Double, z: Double, typeId: Int, image: String) {}
    fun getPitch(ent: Any) {}
    fun getYaw(ent: Any) {}
    fun rideAnimal(rider: Any, target: Any) {}
    fun setPosition(ent: Any, x: Double, y: Double, z: Double) {}
    fun setPositionRelative(ent: Any, x: Double, y: Double, z: Double) {}
    fun setRot(ent: Any, yaw: Double, pitch: Double) {}
    fun setVelX(ent: Any, velocity: Double) {}
    fun setVelY(ent: Any, velocity: Double) {}
    fun setVelZ(ent: Any, velocity: Double) {}

    // Level
    fun explode(x: Double, y: Double, z: Double, radius: Double, hasFire: Boolean) {}
    fun getLevel() {}
    fun getTile(x: Double, y: Double, z: Double) {}
    fun preventDefault() {}
    fun setNightMode(isNight: Boolean) {}
    fun setTile(x: Double, y: Double, z: Double, blockId: Int, damage: Int) {}
    fun spawnChicken(x: Double, y: Double, z: Double, image: String) {}
    fun spawnCow(x: Double, y: Double, z: Double, image: String) {}
    fun spawnPigZombie(x: Double, y: Double, z: Double, heldItemId: Int, image: String) {}

    // Player
    fun addItemInventory(id: Int, data: Int, count: Int) {}
    fun addItemInventory(id: Int, count: Int) {}
    fun getCarriedItem() {}
    fun getPlayerEnt() {}
    fun getPlayerX() {}
    fun getPlayerY() {}
    fun getPlayerZ() {}

    // Text
    fun clientMessage(message: String) {}
    fun print(text: String) {}
}