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
internal class Player {
    fun addExp(exp: Int) {}
    fun addItemCreativeInv(id: Int, count: Int, data: Int) {}
    fun addItemCreativeInv(id: Int, count: Int) {}
    fun addItemInventory(id: Int, count: Int, data: Int) {}
    fun addItemInventory(id: Int, count: Int) {}
    fun canFly() {}
    fun clearInventorySlot(slotNumber: Int) {}
    fun enchant(slot: Int, enchantment: Int, power: Int) {}
    fun getArmorSlot(slotNumber: Int) {}
    fun getArmorSlotDamage(slotNumber: Int) {}
    fun getCarriedItem() {}
    fun getCarriedItemCount() {}
    fun getCarriedItemData() {}
    fun getDimension() {}
    fun getEnchantments(slot: Int) {}
    fun getEntity() {}
    fun getExhaustion() {}
    fun getExp() {}
    fun getHunger() {}
    fun getInventorySlot(slotNumber: Int) {}
    fun getInventorySlotCount(slotNumber: Int) {}
    fun getInventorySlotData(slotNumber: Int) {}
    fun getItemCustomName(id: Int) {}
    fun getLevel() {}
    fun getName(ent: Any) {}
    fun getPointedBlockData() {}
    fun getPointedBlockId() {}
    fun getPointedBlockSide() {}
    fun getPointedBlockX() {}
    fun getPointedBlockY() {}
    fun getPointedBlockZ() {}
    fun getPointedEntity() {}
    fun getPointedVecX() {}
    fun getPointedVecY() {}
    fun getPointedVecZ() {}
    fun getSaturation() {}
    fun getScore() {}
    fun getSelectedSlotId() {}
    fun getX() {}
    fun getY() {}
    fun getZ() {}
    fun isFlying() {}
    fun isPlayer(ent: Any) {}
    fun setArmorSlot(slotNumber: Int, id: Int, data: Int) {}
    fun setCanFly(someBoolean: Boolean) {}
    fun setExhaustion(someDouble: Double) {}
    fun setExp(someDouble: Double) {}
    fun setFlying(someBoolean: Boolean) {}
    fun setHealth(halfhearts: Int) {}
    fun setHunger(doubleValue: Double) {}
    fun setInventorySlot(slot: Int, id: Int, count: Int, data: Int) {}
    fun setItemCustomName(slot: Int, name: String) {}
    fun setLevel(level: Int) {}
    fun setSaturation(value: Double) {}
    fun setSelectedSlotId(id: Int) {}
}