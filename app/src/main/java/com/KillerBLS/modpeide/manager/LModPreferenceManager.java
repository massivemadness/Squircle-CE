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

package com.KillerBLS.modpeide.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LModPreferenceManager {

    private SharedPreferences preferences;

    public LModPreferenceManager(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getFullScreenParameter() {
        return preferences.getBoolean("fullscreen", false);
    }

    public boolean getExitConfirmParameter() {
        return preferences.getBoolean("confirm_exit", true);
    }

    public String getLanguageParameter() {
        return preferences.getString("language", "en");
    }

    public void updateLanguageParameter() {
        preferences.edit().putString("language", getLanguageParameter()).apply();
    }

    public boolean getLineNumbersParameter() {
        return preferences.getBoolean("line_numbers", true);
    }

    public boolean getHighlightCurrentLineParameter() {
        return preferences.getBoolean("highlight_current_line", true);
    }

    public String getFontFaceParameter() {
        return preferences.getString("font_face", "monospace");
    }

    public float getFixedTextSizeParameter() {
        return Float.parseFloat(preferences.getString("fixed_text_size", "14"));
    }

    public void updateFixedTextSizeParameter(float size) {
        preferences.edit().putString("fixed_text_size", ""+size).apply();
    }

    public boolean getReadOnlyParameter() {
        return preferences.getBoolean("read_only", false);
    }

    public boolean getSyntaxHighlightParameter() {
        return preferences.getBoolean("syntax_highlighting", true);
    }

    public boolean getBracketMatchingParameter() {
        return preferences.getBoolean("brackets_matching", true);
    }

    public boolean getBracketsAutoCloseParameter() {
        return preferences.getBoolean("brackets_auto_closing", true);
    }

    public boolean getPinchZoomParameter() {
        return preferences.getBoolean("pinch_zoom", true);
    }

    public boolean getAutoCompleteParameter() {
        return preferences.getBoolean("autocomplete_enabled", true);
    }

    public boolean getAutoIndentParameter() {
        return preferences.getBoolean("auto_indentation", true);
    }

    public boolean getSymbolsHighlight() {
        return preferences.getBoolean("highlight_symbols", true);
    }
}

