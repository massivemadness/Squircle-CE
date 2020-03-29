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

package com.lightteam.modpeide.data.feature.suggestion.predefined

import com.lightteam.modpeide.domain.feature.suggestion.SuggestionProvider
import com.lightteam.modpeide.domain.model.editor.Suggestion

class ModPECompletion : SuggestionProvider {

    private val block = arrayOf(
        "defineBlock",
        "defineLiquidBlock",
        "getAllBlockIds",
        "getDestroyTime",
        "getFriction",
        "getRenderLayer",
        "getRenderType",
        "getTextureCoords",
        "setColor",
        "setDestroyTime",
        "setExplosionResistance",
        "setFriction",
        "setLightLevel",
        "setLightOpacity",
        "setRedstoneConsumer",
        "setRenderLayer",
        "setRenderType",
        "setShape"
    )

    private val entity = arrayOf(
        "addEffect",
        "getAll",
        "getAnimalAge",
        "getArmor",
        "getArmorCustomName",
        "getArmorDamage",
        "getCarriedItem",
        "getCarriedItemCount",
        "getCarriedItemData",
        "getEntityTypeId",
        "getExtraData",
        "getHealth",
        "getItemEntityCount",
        "getItemEntityData",
        "getItemEntityId",
        "getMaxHealth",
        "getMobSkin",
        "getNameTag",
        "getOffhandSlot",
        "getOffhandSlotCount",
        "getOffhandSlotData",
        "getPitch",
        //"getRenderType",
        "getRider",
        "getRiding",
        "getTarget",
        "getUniqueId",
        "getVelX",
        "getVelY",
        "getVelZ",
        "getX",
        "getY",
        "getYaw",
        "getZ",
        "isSneaking",
        "remove",
        "removeAllEffects",
        "removeEffect",
        "rideAnimal",
        "setAnimalAge",
        "setArmor",
        "setArmorCustomName",
        "setCape",
        "setCarriedItem",
        "setCollisionSize",
        "setExtraData",
        "setFireTicks",
        "setHealth",
        "setImmobile",
        "setMaxHealth",
        "setMobSkin",
        "setNameTag",
        "setOffhandSlot",
        "setPosition",
        "setPositionRelative",
        //"setRenderType",
        "setRot",
        "setSneaking",
        "setTarget",
        "setVelX",
        "setVelY",
        "setVelZ",
        "spawnMob"
    )

    private val item = arrayOf(
        "addCraftRecipe",
        "addFurnaceRecipe",
        "addShapedRecipe",
        "defineArmor",
        "defineThrowable",
        "getCustomThrowableRenderType",
        "getMaxDamage",
        "getMaxStackSize",
        "getName",
        //"getTextureCoords",
        "getUseAnimation",
        "internalNameToId",
        "isValidItem",
        "setAllowOffhand",
        "setCategory",
        "setEnchantType",
        "setHandEquipped",
        "setMaxDamage",
        "setProperties",
        "setStackedByData",
        "setUseAnimation",
        "translatedNameToId"
    )

    private val level = arrayOf(
        "addParticle",
        "biomeIdToName",
        "canSeeSky",
        "destroyBlock",
        "dropItem",
        "executeCommand",
        "explode",
        "getAddress",
        "getBiome",
        "getBiomeName",
        "getBrightness",
        "getChestSlot",
        "getChestSlotCount",
        "getChestSlotCustomName",
        "getChestSlotData",
        "getData",
        "getDifficulty",
        "getFurnaceSlot",
        "getFurnaceSlotCount",
        "getFurnaceSlotData",
        "getGameMode",
        "getGrassColor",
        "getLightningLevel",
        "getRainLevel",
        "getSignText",
        "getSpawnerEntityType",
        "getTile",
        "getTime",
        "getWorldDir",
        "getWorldName",
        "isRemote",
        "playSound",
        "playSoundEnt",
        "setBlockExtraData",
        "setChestSlot",
        "setChestSlotCustomName",
        "setDifficulty",
        "setFurnaceSlot",
        "setGameMode",
        "setGrassColor",
        "setLightningLevel",
        "setNightMode",
        "setRainLevel",
        "setSignText",
        "setSpawn",
        "setSpawnerEntityType",
        "setTile",
        "setTime",
        "spawnChicken",
        "spawnCow",
        "spawnMob",
        "spawnPigZombie"
    )

    private val modpe = arrayOf(
        "dumpVtable",
        "getBytesFromTexturePack",
        "getI18n",
        "getLanguage",
        "getMinecraftVersion",
        "getOS",
        "joinServer",
        "langEdit",
        "leaveGame",
        "log",
        "openInputStreamFromTexturePack",
        "overrideTexture",
        "readData",
        "removeData",
        "resetFov",
        "resetImages",
        "saveData",
        "selectLevel",
        "setCamera",
        "setFoodItem",
        "setFov",
        "setGameSpeed",
        "setGuiBlocks",
        "setItem",
        "setItems",
        "setTerrain",
        "setUiRenderDebug",
        "showTipMessage",
        "takeScreenshot"
    )

    private val player = arrayOf(
        "addExp",
        "addItemCreativeInv",
        "addItemInventory",
        "canFly",
        "clearInventorySlot",
        "enchant",
        "getArmorSlot",
        "getArmorSlotDamage",
        //"getCarriedItem",
        //"getCarriedItemCount",
        //"getCarriedItemData",
        "getDimension",
        "getEnchantments",
        "getEntity",
        "getExhaustion",
        "getExp",
        "getHunger",
        "getInventorySlot",
        "getInventorySlotCount",
        "getInventorySlotData",
        "getItemCustomName",
        "getLevel",
        "getName",
        "getPointedBlockData",
        "getPointedBlockId",
        "getPointedBlockSide",
        "getPointedBlockX",
        "getPointedBlockY",
        "getPointedBlockZ",
        "getPointedEntity",
        "getPointedVecX",
        "getPointedVecY",
        "getPointedVecZ",
        "getSaturation",
        "getScore",
        "getSelectedSlotId",
        //"getX",
        //"getY",
        //"getZ",
        "isFlying",
        "isPlayer",
        "setArmorSlot",
        "setCanFly",
        "setExhaustion",
        "setExp",
        "setFlying",
        //"setHealth"
        "setHunger",
        "setInventorySlot",
        "setItemCustomName",
        "setLevel",
        "setSaturation",
        "setSelectedSlotId"
    )

    private val server = arrayOf(
        //"getAddress",
        "getAllPlayerNames",
        "getAllPlayers",
        "getPort",
        //"joinServer",
        "sendChat"
    )

    private val hooks = arrayOf(
        "continueDestroyBlock",
        "customThrowableHitBlockHook",
        //"destroyBlock",
        "projectileHitBlockHook",
        "startDestroyBlock",
        "chatHook",
        "chatReceiveHook",
        "procCmd",
        "serverMessageReceiveHook",
        "newLevel",
        "selectLevelHook",
        "attackHook",
        "customThrowableHitEntityHook",
        "deathHook",
        "entityAddedHook",
        "entityHurtHook",
        "entityRemovedHook",
        "projectileHitEntityHook",
        "blockEventHook",
        "explodeHook",
        //"leaveGame",
        "levelEventHook",
        "modTick",
        "newLevel",
        "redstoneUpdateHook",
        "screenChangeHook",
        "selectLevelHook",
        "eatHook",
        "playerAddExpHook",
        "playerExpLevelChangeHook",
        "useItem"
    )

    private val global = arrayOf(
        "bl_setMobSkin",
        "bl_spawnMob",
        "preventDefault",
        "getPlayerX",
        "getPlayerY",
        "getPlayerZ",
        "getPlayerEnt",
        "clientMessage",
        "print"
    )

    private val javascript = arrayOf(
        "function"
    )

    override fun getAll(): List<Suggestion> {
        val stringArray = arrayOf(
            *block,
            *entity,
            *item,
            *level,
            *modpe,
            *player,
            *server,
            *hooks,
            *global,
            *javascript
        )
        val suggestions: MutableList<Suggestion> = mutableListOf()
        for (string in stringArray) {
            val suggestion = Suggestion(string)
            suggestions.add(suggestion)
        }
        return suggestions
    }
}