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

import java.util.regex.Pattern;

public abstract class Language {
    public abstract String getExtension();
    public abstract Pattern getSyntaxNumbers();
    public abstract Pattern getSyntaxSymbols();
    public abstract Pattern getSyntaxBrackets();
    public abstract Pattern getSyntaxKeywords();
    public abstract Pattern getSyntaxMethods();
    public abstract Pattern getSyntaxStrings();
    public abstract Pattern getSyntaxComments();
    public abstract char[] getLanguageBrackets();
    public abstract String[] getAllCompletions();
}