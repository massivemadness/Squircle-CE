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

package com.KillerBLS.modpeide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.KillerBLS.modpeide.manager.TypefaceManager;

/**
 * Управление настройками приложения.
 */
public class Wrapper {

    private SharedPreferences mManager;

    //Look And Feel
    private static final String KEY_FULLSCREEN_MODE = "FULLSCREEN_MODE";

    //Other
    private static final String KEY_CONFIRM_EXIT = "CONFIRM_EXIT";

    //Font
    private static final String KEY_FONT_SIZE = "FONT_SIZE_1";
    private static final String KEY_FONT_TYPE = "FONT_TYPE";

    //Tabs
    private static final String KEY_RESUME_SESSION = "RESUME_SESSION";
    private static final String KEY_MAX_TABS_COUNT = "MAX_TABS_COUNT_1";
    private static final String KEY_DISABLE_SWIPE_GESTURE = "DISABLE_SWIPE";

    //Editor
    private static final String KEY_WRAP_CONTENT = "WRAP_CONTENT";
    private static final String KEY_CODE_COMPLETION = "CODE_COMPLETION";
    private static final String KEY_PINCH_ZOOM = "PINCH_ZOOM";
    private static final String KEY_SHOW_LINE_NUMBERS = "SHOW_LINE_NUMBERS";
    private static final String KEY_HIGHLIGHT_CURRENT_LINE = "HIGHLIGHT_CURRENT_LINE";
    private static final String KEY_BRACKET_MATCHING = "BRACKET_MATCHING";
    private static final String KEY_SYNTAX_HIGHLIGHT = "SYNTAX_HIGHLIGHT";
    private static final String KEY_READ_ONLY = "READ_ONLY";

    //Keyboard
    private static final String KEY_USE_EXTENDED_KEYBOARD = "USE_EXTENDED_KEYS";
    private static final String KEY_USE_IME_KEYBOARD = "USE_IME_KEYBOARD";

    //Code Style
    private static final String KEY_INDENT_LINE = "INDENT_LINE";
    private static final String KEY_INSERT_BRACKET = "INSERT_BRACKET";

    //File Explorer
    private static final String KEY_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES";
    private static final String KEY_SORT_MODE = "SORT_MODE";

    public Wrapper(Context context) {
        mManager = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // region LOOK_AND_FEEL

    public boolean getFullscreenMode() {
        return mManager.getBoolean(KEY_FULLSCREEN_MODE, false);
    }

    // endregion LOOK_AND_FEEL

    // region OTHER

    public boolean getConfirmExit() {
        return mManager.getBoolean(KEY_CONFIRM_EXIT, true);
    }

    // endregion OTHER

    // region FONT

    public int getFontSize() {
        return Integer.parseInt(mManager.getString(KEY_FONT_SIZE, "14"));
    }

    public String getFontType() {
        return mManager.getString(KEY_FONT_TYPE, TypefaceManager.DROID_SANS_MONO);
    }

    // endregion FONT

    // region TABS

    public boolean getResumeSession() {
        return mManager.getBoolean(KEY_RESUME_SESSION, true);
    }

    public int getMaxTabsCount() {
        return Integer.parseInt(mManager.getString(KEY_MAX_TABS_COUNT, "5"));
    }

    public boolean getDisableSwipeGesture() {
        return mManager.getBoolean(KEY_DISABLE_SWIPE_GESTURE, false);
    }

    // endregion TABS

    // region EDITOR

    public boolean getWrapContent() {
        return mManager.getBoolean(KEY_WRAP_CONTENT, true);
    }

    public boolean getCodeCompletion() {
        return mManager.getBoolean(KEY_CODE_COMPLETION, true);
    }

    public boolean getPinchZoom() {
        return mManager.getBoolean(KEY_PINCH_ZOOM, true);
    }

    public boolean getShowLineNumbers() {
        return mManager.getBoolean(KEY_SHOW_LINE_NUMBERS, true);
    }

    public boolean getHighlightCurrentLine() {
        return mManager.getBoolean(KEY_HIGHLIGHT_CURRENT_LINE, true);
    }

    public boolean getBracketMatching() {
        return mManager.getBoolean(KEY_BRACKET_MATCHING, true);
    }

    public boolean getSyntaxHighlight() {
        return mManager.getBoolean(KEY_SYNTAX_HIGHLIGHT, true);
    }

    public boolean getReadOnly() {
        return mManager.getBoolean(KEY_READ_ONLY, false);
    }

    // endregion EDITOR

    // region KEYBOARD

    public boolean getExtendedKeyboard() {
        return mManager.getBoolean(KEY_USE_EXTENDED_KEYBOARD, true);
    }

    public boolean getImeKeyboard() {
        return mManager.getBoolean(KEY_USE_IME_KEYBOARD, false);
    }

    // endregion KEYBOARD

    // region CODE_STYLE

    public boolean getIndentLine() {
        return mManager.getBoolean(KEY_INDENT_LINE, true);
    }

    public boolean getInsertBracket() {
        return mManager.getBoolean(KEY_INSERT_BRACKET, true);
    }

    // endregion CODE_STYLE

    // region FILE_EXPLORER

    public boolean getFilterHidden() {
        return mManager.getBoolean(KEY_SHOW_HIDDEN_FILES, true);
    }

    public void setFilterHidden(boolean showHiddenFiles) {
        mManager.edit().putBoolean(KEY_SHOW_HIDDEN_FILES, showHiddenFiles).apply();
    }

    public int getSortMode() {
        return Integer.parseInt(mManager.getString(KEY_SORT_MODE, "0")); //Сортировка по имени
    }

    public void setSortMode(String sortMode) {
        mManager.edit().putString(KEY_SORT_MODE, sortMode).apply();
    }

    // endregion FILE_EXPLORER
}
