/*
 * Copyright (C) 2018 Light Team Software
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
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.KillerBLS.modpeide.utils.text.language;

import com.KillerBLS.modpeide.utils.text.ArrayUtils;

import java.util.regex.Pattern;

public class ModPELanguage extends Language {

    @Override
    public final String getExtension() {
        return ".js";
    }

    /**
     * Паттерны с ключевыми словами для подсветки синтаксиса, используются в
     * {@link com.KillerBLS.modpeide.widget.TextProcessor}.
     */

    private static final Pattern SYNTAX_NUMBERS = Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)");

    public final Pattern getSyntaxNumbers() {
        return SYNTAX_NUMBERS;
    }

    private static final Pattern SYNTAX_SYMBOLS = Pattern.compile(
            "(!|\\+|-|\\*|<|>|=|\\?|\\||:|%|&)");

    public final Pattern getSyntaxSymbols() {
        return SYNTAX_SYMBOLS;
    }

    private static final Pattern SYNTAX_BRACKETS = Pattern.compile("(\\(|\\)|\\{|\\}|\\[|\\])");

    public final Pattern getSyntaxBrackets() {
        return SYNTAX_BRACKETS;
    }

    private static final Pattern SYNTAX_KEYWORDS = Pattern.compile(
            "(?<=\\b)((break)|(continue)|(else)|(for)|(function)|(if)|(in)|(new)" +
                    "|(this)|(var)|(while)|(return)|(case)|(catch)|(of)|(typeof)" +
                    "|(const)|(default)|(do)|(switch)|(try)|(null)|(true)" +
                    "|(false)|(eval)|(let))(?=\\b)"); //Слова без CASE_INSENSITIVE

    public final Pattern getSyntaxKeywords() {
        return SYNTAX_KEYWORDS;
    }

    private static final Pattern SYNTAX_METHODS = Pattern.compile(
            "(?<=(function) )(\\w+)", Pattern.CASE_INSENSITIVE);

    public final Pattern getSyntaxMethods() {
        return SYNTAX_METHODS;
    }

    private static final Pattern SYNTAX_STRINGS = Pattern.compile("\"(.*?)\"|'(.*?)'");

    public final Pattern getSyntaxStrings() {
        return SYNTAX_STRINGS;
    }

    private static final Pattern SYNTAX_COMMENTS = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*");

    public final Pattern getSyntaxComments() {
        return SYNTAX_COMMENTS;
    }

    private static final char[] LANGUAGE_BRACKETS = new char[]{'{', '[', '(', '}', ']', ')'}; //do not change

    public final char[] getLanguageBrackets() {
        return LANGUAGE_BRACKETS;
    }

    /**
     * Слова для автопродолжения кода.
     */

    private static final String[] BLOCK_KEYWORDS = new String[] {
            "defineBlock", "defineLiquidBlock", "getAllBlockIds", "getDestroyTime",
            "getFriction", "getRenderLayer", "getRenderType", "getTextureCoords", "setColor",
            "setDestroyTime", "setExplosionResistance", "setFriction", "setLightLevel",
            "setLightOpacity", "setRedstoneConsumer", "setRenderLayer", "setRenderType", "setShape"
    };
    private static final String[] ENTITY_KEYWORDS = new String[] {
            "addEffect", "getAll", "getAnimalAge", "getArmor", "getArmorCustomName",
            "getArmorDamage", "getCarriedItem", "getCarriedItemCount", "getCarriedItemData",
            "getEntityTypeId", "getExtraData", "getHealth", "getItemEntityCount",
            "getItemEntityData", "getItemEntityId", "getMaxHealth", "getMobSkin", "getNameTag",
            "getOffhandSlot", "getOffhandSlotCount", "getOffhandSlotData", "getPitch",
            /*"getRenderType",*/ "getRider()", "getRiding()", "getTarget", "getUniqueId",
            "getVelX", "getVelY", "getVelZ", "getX()", "getY()", "getYaw", "getZ()", "isSneaking",
            "remove", "removeAllEffects", "removeEffect", "rideAnimal", "setAnimalAge", "setArmor",
            "setArmorCustomName", "setCape", "setCarriedItem", "setCollisionSize", "setExtraData",
            "setFireTicks", "setHealth", "setImmobile", "setMaxHealth", "setMobSkin", "setNameTag",
            "setOffhandSlot", "setPosition", "setPositionRelative", /*"setRenderType",*/
            "setRot", "setSneaking", "setTarget", "setVelX", "setVelY", "setVelZ", "spawnMob"
    };
    private static final String[] ITEM_KEYWORDS = new String[] {
            "addCraftRecipe", "addFurnaceRecipe", "addShapedRecipe", "defineArmor",
            "defineThrowable", "getCustomThrowableRenderType", "getMaxDamage", "getMaxStackSize",
            "getName", /*"getTextureCoords",*/ "getUseAnimation", "internalNameToId", "isValidItem",
            "setAllowOffhand", "setCategory", "setEnchantType", "setHandEquipped", "setMaxDamage",
            "setProperties", "setStackedByData", "setUseAnimation", "translatedNameToId"
    };
    private static final String[] LEVEL_KEYWORDS = new String[] {
            "addParticle", "biomeIdToName", "canSeeSky", "destroyBlock", "dropItem",
            "executeCommand", "explode", "getAddress", "getBiome", "getBiomeName", "getBrightness",
            "getChestSlot", "getChestSlotCount", "getChestSlotCustomName", "getChestSlotData",
            "getData", "getDifficulty()", "getFurnaceSlot", "getFurnaceSlotCount",
            "getFurnaceSlotData", "getGameMode()", "getGrassColor", "getLightningLevel()",
            "getRainLevel()", "getSignText", "getSpawnerEntityType", "getTile", "getTime()",
            "getWorldDir()", "getWorldName()", "isRemote()", "playSound", "playSoundEnt",
            "setBlockExtraData", "setChestSlot", "setChestSlotCustomName", "setDifficulty",
            "setFurnaceSlot", "setGameMode", "setGrassColor", "setLightningLevel", "setNightMode",
            "setRainLevel", "setSignText", "setSpawn", "setSpawnerEntityType", "setTile", "setTime",
            "spawnChicken", "spawnCow", "spawnMob", "spawnPigZombie"
    };
    private static final String[] MODPE_KEYWORDS = new String[] {
            "dumpVtable", "getBytesFromTexturePack", "getI18n", "getLanguage()",
            "getMinecraftVersion()", "getOS()", "joinServer", "langEdit", "leaveGame", "log",
            "openInputStreamFromTexturePack", "overrideTexture", "readData", "removeData",
            "resetFov()", "resetImages()", "saveData", "selectLevel", "setCamera", "setFoodItem",
            "setFov", "setGameSpeed", "setGuiBlocks", "setItem", "setItems", "setTerrain",
            "setUiRenderDebug", "showTipMessage", "takeScreenshot"
    };
    private static final String[] PLAYER_KEYWORDS = new String[] {
            "addExp", "addItemCreativeInv", "addItemInventory", "canFly()", "clearInventorySlot",
            "enchant", "getArmorSlot", "getArmorSlotDamage", /*"getCarriedItem",
            "getCarriedItemCount", "getCarriedItemData",*/ "getDimension()", "getEnchantments",
            "getEntity()", "getExhaustion()", "getExp()", "getHunger()", "getInventorySlot",
            "getInventorySlotCount", "getInventorySlotData", "getItemCustomName", "getLevel",
            "getName", "getPointedBlockData()", "getPointedBlockId()", "getPointedBlockSide()",
            "getPointedBlockX()", "getPointedBlockY()", "getPointedBlockZ()", "getPointedEntity()",
            "getPointedVecX()", "getPointedVecY()", "getPointedVecZ()", "getSaturation", "getScore",
            "getSelectedSlotId()", /*"getX", "getY", "getZ",*/ "isFlying()", "isPlayer",
            "setArmorSlot", "setCanFly", "setExhaustion", "setExp", "setFlying", /*"setHealth"*/
            "setHunger", "setInventorySlot", "setItemCustomName", "setLevel", "setSaturation",
            "setSelectedSlotId"
    };
    private static final String[] SERVER_KEYWORDS = new String[] {
            /*"getAddress",*/ "getAllPlayerNames()", "getAllPlayers()", "getPort()", /*"joinServer",*/
            "sendChat"
    };
    private static final String[] HOOKS_KEYWORDS = new String[] {
            "continueDestroyBlock", "customThrowableHitBlockHook", /*"destroyBlock",*/
            "projectileHitBlockHook", "startDestroyBlock", "chatHook", "chatReceiveHook",
            "procCmd", "serverMessageReceiveHook", "newLevel", "selectLevelHook", "attackHook",
            "customThrowableHitEntityHook", "deathHook", "entityAddedHook", "entityHurtHook",
            "entityRemovedHook", "projectileHitEntityHook", "blockEventHook", "explodeHook",
            /*"leaveGame()",*/"levelEventHook", "modTick()", "newLevel()", "redstoneUpdateHook",
            "screenChangeHook", "selectLevelHook()", "eatHook", "playerAddExpHook",
            "playerExpLevelChangeHook", "useItem"
    };
    private static final String[] GLOBAL_KEYWORDS = new String[] {
            "bl_setMobSkin", "bl_spawnMob", "preventDefault()", "getPlayerX()", "getPlayerY()",
            "getPlayerZ()", "getPlayerEnt()", "clientMessage", "print"
    };
    private static final String[] JS_KEYWORDS = new String[] {
            "function"
    };

    /**
     * Соединение всех массивов в один. Этот массив и будет использоваться для
     * получения слов в редакторе.
     */
    private static final String[] ALL_KEYWORDS = ArrayUtils.join(String.class,
            BLOCK_KEYWORDS, ENTITY_KEYWORDS, ITEM_KEYWORDS, LEVEL_KEYWORDS, MODPE_KEYWORDS,
            PLAYER_KEYWORDS, SERVER_KEYWORDS, HOOKS_KEYWORDS, GLOBAL_KEYWORDS, JS_KEYWORDS);

    public final String[] getAllCompletions() {
        return ALL_KEYWORDS;
    }
}