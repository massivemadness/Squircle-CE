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

package com.lightteam.language.javascript.parser.predefined

/**
 * This class is a part of ModPE API. Simply ignore it.
 */
@Suppress("unused", "unused_parameter")
internal class Hooks {
    fun attackHook(attacker: Any, victim: Any) {}
    fun blockEventHook(x: Int, y: Int, z: Int, eventType: Int, data: Int) {}
    fun continueDestroyBlock(x: Int, y: Int, z: Int, side: Int, progress: Int) {}
    fun customThrowableHitBlockHook(projectile: Any, itemId: Int, blockX: Int, blockY: Int, blockZ: Int, side: Int) {}
    fun customThrowableHitEntityHook(projectile: Any, itemId: Int, targetEntity: Any) {}
    fun chatHook(text: String) {}
    fun chatReceiveHook(str: String, sender: String) {}
    fun deathHook(attacker: Any, victim: Any) {}
    fun destroyBlock(x: Int, y: Int, z: Int, side: Int) {}
    fun eatHook(hearts: Int, saturationRatio: Float) {}
    fun entityAddedHook(entity: Any) {}
    fun entityHurtHook(attacker: Any, victim: Any, halfhearts: Int) {}
    fun entityRemovedHook(entity: Any) {}
    fun explodeHook(entity: Any, x: Double, y: Double, z: Double, power: Float, onFire: Boolean) {}
    fun leaveGame() {}
    fun levelEventHook(entity: Any, eventType: Int, x: Int, y: Int, z: Int, data: Int) {}
    fun modTick() {}
    fun newLevel() {}
    fun playerAddExpHook(player: Any, experienceAdded: Int) {}
    fun playerExpLevelChangeHook(player: Any, levelsAdded: Int) {}
    fun procCmd(cmd: String) {}
    fun projectileHitBlockHook(projectile: Any, blockX: Int, blockY: Int, blockZ: Int, side: Int) {}
    fun projectileHitEntityHook(projectile: Any, targetEntity: Any) {}
    fun redstoneUpdateHook(x: Int, y: Int, z: Int, newCurrent: Int, worldLoading: Boolean, blockId: Int, blockData: Int) {}
    fun screenChangeHook(screenName: String) {}
    fun serverMessageReceiveHook(message: String) {}
    fun selectLevelHook() {}
    fun startDestroyBlock(x: Int, y: Int, z: Int, side: Int) {}
    fun useItem(x: Int, y: Int, z: Int, itemId: Int, blockId: Int, side: Int, itemDamage: Int, blockDamage: Int) {}
}