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

package com.lightteam.modpeide.data.parser.api;

internal class Entity {
    fun addEffect(ent: Any, effect: Int, duration: Int, amplification: Int, ambient: Boolean, showParticles: Boolean) {}
    fun getAll() {}
    fun getAnimalAge(animal: Any) {}
    fun getArmor(ent: Any, slot: Int) {}
    fun getArmorCustomName(ent: Any, slot: Int) {}
    fun getArmorDamage(ent: Any, slot: Int) {}
    fun getCarriedItem(ent: Any) {}
    fun getCarriedItemCount(ent: Any) {}
    fun getCarriedItemData(ent: Any) {}
    fun getEntityTypeId(mobId: Any) {}
    fun getExtraData(ent: Any, key: String) {}
    fun getHealth(ent: Any) {}
    fun getItemEntityCount(ent: Any) {}
    fun getItemEntityData(ent: Any) {}
    fun getItemEntityId(ent: Any) {}
    fun getMaxHealth(ent: Any) {}
    fun getMobSkin(ent: Any) {}
    fun getNameTag(ent: Any) {}
    fun getOffhandSlot(ent: Any) {}
    fun getOffhandSlotCount(ent: Any) {}
    fun getOffhandSlotData(ent: Any) {}
    fun getPitch(ent: Any) {}
    fun getRenderType(ent: Any) {}
    fun getRider(ent: Any) {}
    fun getRiding(ent: Any) {}
    fun getTarget(ent: Any) {}
    fun getUniqueId(ent: Any) {}
    fun getVelX(ent: Any) {}
    fun getVelY(ent: Any) {}
    fun getVelZ(ent: Any) {}
    fun getX(ent: Any) {}
    fun getY(ent: Any) {}
    fun getYaw(ent: Any) {}
    fun getZ(ent: Any) {}
    fun isSneaking(ent: Any) {}
    fun remove(ent: Any) {}
    fun removeAllEffects(ent: Any) {}
    fun removeEffect(ent: Any, effect: Int) {}
    fun rideAnimal(rider: Any, mount: Any) {}
    fun setAnimalAge(animal: Any, age: Int) {}
    fun setArmor(ent: Any, slot: Int, id: Int, data: Int) {}
    fun setArmorCustomName(ent: Any, slot: Int, name: String) {}
    fun setCape(ent: Any, texture: String) {}
    fun setCarriedItem(ent: Any, id: Int, count: Int, damage: Int) {}
    fun setCollisionSize(ent: Any, sizeX: Double, sizeY: Double) {}
    fun setExtraData(ent: Any, key: String, value: String) {}
    fun setFireTicks(ent: Any, seconds: Int) {}
    fun setHealth(ent: Any, halfhearts: Int) {}
    fun setImmobile(ent: Any, someBoolean: Double) {}
    fun setMaxHealth(ent: Any, maxhealth: Int) {}
    fun setMobSkin(ent: Any, texture: String) {}
    fun setNameTag(ent: Any, name: String) {}
    fun setOffhandSlot(ent: Any, slot: Int, id: Int, damage: Int) {}
    fun setPosition(ent: Any, x: Double, y: Double, z: Double) {}
    fun setPositionRelative(ent: Any, x: Double, y: Double, z: Double) {}
    fun setRenderType(ent: Any, render: Any) {}
    fun setRot(ent: Any, yaw: Double, pitch: Double) {}
    fun setSneaking(ent: Any, someBoolean: Boolean) {}
    fun setTarget(ent: Any, entity: Any) {}
    fun setVelX(ent: Any, velocity: Double) {}
    fun setVelY(ent: Any, velocity: Double) {}
    fun setVelZ(ent: Any, velocity: Double) {}
    fun spawnMob(x: Double, y: Double, z: Double, mobId: Int, texture: String) {}
}