/*
 * Copyright (C) 2017 Light Team Software
 *
 * This file is part of ModPE IDE.
 *
 * ModPE IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ModPE IDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.util;

import android.support.annotation.NonNull;

import java.lang.reflect.Array;

public class CompletionKeywords {

    private static final String[] BLOCK_KEYWORDS = new String[] {
            "defineBlock", "defineLiquidBlock", "getAllBlockIds", "getDestroyTime",
            "getFriction", "setShape", "getRenderType", "getTextureCoords", "setColor",
            "setDestroyTime", "setExplosionResistance", "setFriction", "setRedstoneConsumer",
            "setLightLevel", "setLightOpacity", "setRenderLayer", "setRenderType"
    };
    private static final String[] ENTITY_KEYWORDS = new String[] {
            "getAll", "getAnimalAge", "getArmor", "getArmorCustomName", "getArmorDamage",
            "getEntityTypeId", "getExtraData", "getHealth", "getItemEntityCount",
            "getItemEntityData", "getItemEntityId", "getMaxHealth", "getMobSkin", "getNameTag",
            "getPitch()", "getRenderType", "getRider", "getRiding", "getTarget", "getUniqueId",
            "getVelX()", "getVelY()", "getVelZ()", "getYaw()", "isSneaking()", "remove",
            "removeAllEffects", "removeEffect", "rideAnimal", "setArmor", "setArmorCustomName",
            "setCape", "setCollisionSize", "setExtraData", "setFireTicks", "setHealth",
            "setImmobile", "setMaxHealth", "setMobSkin", "setNameTag", "setPosition",
            "setPositionRelative", "setCarriedItem", "setRenderType", "setRot", "setSneaking",
            "setTarget", "setVelX", "setVelY", "setVelZ", "spawnMob", "addEffect"
    };
    private static final String[] ITEM_KEYWORDS = new String[] {
            "getMaxDamage", "getMaxStackSize", "defineArmor", "defineThrowable",
            "getCustomThrowableRenderType", "addCraftRecipe", "setMaxDamage", "addFurnaceRecipe",
            "getName", "getTextureCoords", "getUseAnimation", "internalNameToId", "isValidItem",
            "setCategory", "setEnchantType", "addShapedRecipe", "setHandEquipped", "setProperties",
            "setStackedByData", "setUseAnimation", "translatedNameToId"
    };
    private static final String[] LEVEL_KEYWORDS = new String[] {
            "biomeIdToName", "canSeeSky", "setSpawnerTypeId", "destroyBlock", "explode",
            "getAddress", "getBiome", "getBiomeName", "getBrightness", "getGameMode",
            "getGrassColor", "getDifficulty", "setDifficulty", "getTile", "getData", "getTime",
            "getWorldDir()", "getWorldName", "setGameMode", "setGrassColor", "getLightningLevel()",
            "getRainLevel()", "setNightMode", "setSpawn", "setTile", "setTime", "spawnMob",
            "getSignText", "setSignText", "addParticle", "playSound", "playSoundEnt",
            "setBlockExtraData", "dropItem", "getChestSlot", "getChestSlotCount",
            "getChestSlotData", "setChestSlot", "setChestSlotCustomName", "setSpawnerEntityType",
            "setLightningLevel", "setRainLevel", "getFurnaceSlot", "getFurnaceSlotCount",
            "getFurnaceSlotData", "setFurnaceSlot"
    };
    private static final String[] MODPE_KEYWORDS = new String[] {
            "getOS()", "dumpVtable", "getI18n", "getBytesFromTexturePack", "getLanguage",
            "getMinecraftVersion", "langEdit", /*"leaveGame"*/"openInputStreamFromTexturePack",
            "overrideTexture", "readData", "removeData", "saveData", "resetFov", "resetImages",
            "setFoodItem", "setFov", "setGameSpeed", "setItem", "showTipMessage",
            "setUiRenderDebug", "takeScreenshot", "setGuiBlocks", "setItems", "setTerrain",
            "selectLevel"
    };
    private static final String[] PLAYER_KEYWORDS = new String[] {
            "addExp", "addItemInventory", "addItemCreativeInv", "canFly()", "clearInventorySlot",
            "enchant", "getEnchantments", "getArmorSlot", "getArmorSlotDamage", /*"getCarriedItem"*/
            "getCarriedItemCount", "getCarriedItemData", "getDimension", "getEntity",
            "getExhaustion", "getExp", "getHunger", "getInventorySlot", "getInventorySlotCount",
            "getInventorySlotData", "getItemCustomName", "setInventorySlot", "getLevel", "setLevel",
            "setSaturation", "setSelectedSlotId", "setItemCustomName", "getName",
            "getPointedBlockId()", "getPointedBlockData()", "getPointedBlockSide()",
            "getPointedBlockX()", "getPointedBlockY()", "getPointedBlockZ()", "getPointedEntity()",
            "getPointedVecX()", "getPointedVecY()", "getPointedVecZ()", "getSaturation", "getScore",
            "getSelectedSlotId()", /*"getX", "getY", "getZ",*/ "isFlying()", "setCanFly",
            "setFlying", /*"setHealth"*/"setArmorSlot", "setExhaustion", "setExp",
            /*"addItemCreativeInv",*/ "setHunger", "isPlayer()"
    };
    private static final String[] SERVER_KEYWORDS = new String[] {
            /*"getAddress",*/ "getAllPlayerNames()", "getAllPlayers()", "getPort()", "joinServer",
            "sendChat"
    };
    private static final String[] HOOKS_KEYWORDS = new String[] {
            "useItem", /*"destroyBlock",*/ "newLevel", "procCmd", "selectLevelHook",
            /*"leaveGame",*/ "attackHook", "modTick", "eatHook", "explodeHook", "deathHook",
            "entityAddedHook", "entityRemovedHook", "entityHurtHook", "projectileHitEntityHook",
            "playerAddExpHook", "playerExpLevelChangeHook", "redstoneUpdateHook",
            "startDestroyBlock", "continueDestroyBlock", "blockEventHook", "levelEventHook",
            "serverMessageReceiveHook", "screenChangeHook", "chatReceiveHook", "chatHook"
    };
    private static final String[] JS_KEYWORDS = new String[] {
            "function"
    };
    private static final String[] GLOBAL_KEYWORDS = new String[] {
            "clientMessage", "getPlayerX()", "getPlayerY()", "getPlayerZ()", "getPlayerEnt()"
    };

    public static final String[] ALL_KEYWORDS = join(String.class,
            BLOCK_KEYWORDS, ENTITY_KEYWORDS, ITEM_KEYWORDS, LEVEL_KEYWORDS, GLOBAL_KEYWORDS,
            MODPE_KEYWORDS, PLAYER_KEYWORDS, SERVER_KEYWORDS, HOOKS_KEYWORDS, JS_KEYWORDS);

    @SuppressWarnings("unchecked")
    private static <T> T[] join(Class<T> c, @NonNull T[]... objects) {
        int size = 0;
        for (T[] object : objects) {
            size += object.length;
        }
        T[] result = (T[]) Array.newInstance(c, size);
        int index = 0;
        for (T[] object : objects) {
            for (T t : object) {
                Array.set(result, index, t);
                index++;
            }
        }
        return result;
    }
}

