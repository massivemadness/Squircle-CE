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

package com.KillerBLS.modpeide.syntax;

import java.util.regex.Pattern;

public class SyntaxPatterns {
    //Patterns with keywords
    public static final Pattern KEYWORDS = Pattern.compile(
            "(?<=\\b)((break)|(continue)|(else)|(for)|(function)|(if)|(in)|(new)" +
                    "|(this)|(var)|(while)|(return)|(case)|(catch)|(of)|(typeof)" +
                    "|(const)|(default)|(do)|(switch)|(try)|(null)|(true)" +
                    "|(false)|(eval)|(let))(?=\\b)", Pattern.CASE_INSENSITIVE);
    public static final Pattern KEYWORDS2 = Pattern.compile(
            "(?<=\\b)((parseInt)|(parseFloat))(?=\\b)", Pattern.CASE_INSENSITIVE);
    public static final Pattern COMMENTS = Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*");
    public static final Pattern STRINGS = Pattern.compile("\"(.*?)\"|'(.*?)'");
    public static final Pattern SYMBOLS = Pattern.compile(
            "(!|\\(|\\)|\\+|-|\\*|<|>|=|\\?|\\{|\\}|\\[|\\]|\\||:|%|&)");
    public static final Pattern NUMBERS = Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)");
    public static final Pattern CLASSES = Pattern.compile(
            "(?<=\\b)((Block)|(Entity)|(Item)|(Level)|(ModPE)" +
                    "|(Player)|(Server)|(ChatColor)|(BlockFace)" +
                    "|(ItemCategory)|(ParticleType)|(EntityType)" +
                    "|(EntityRenderType)|(ArmorType)|(MobEffect)" +
                    "|(DimensionId)|(UseAnimation)|(Enchantment)" +
                    "|(EnchantType)|(BlockRenderLayer)|(Math))(?=\\b)", Pattern.CASE_INSENSITIVE);
}
