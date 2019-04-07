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

package com.lightteam.modpeide.data.storage

import android.content.SharedPreferences

class PreferenceHandler(private val sharedPreferences: SharedPreferences) {

    companion object Key {
        //Look And Feel
        private const val KEY_THEME = "THEME_RESOURCE"
        private const val KEY_FULLSCREEN_MODE = "FULLSCREEN_MODE"

        //Other
        private const val KEY_CONFIRM_EXIT = "CONFIRM_EXIT"

        //Font
        private const val KEY_FONT_SIZE = "FONT_SIZE_1"
        private const val KEY_FONT_TYPE = "FONT_TYPE"

        //Tabs
        private const val KEY_RESUME_SESSION = "RESUME_SESSION"
        private const val KEY_MAX_TABS_COUNT = "MAX_TABS_COUNT_1"

        //Editor
        private const val KEY_WRAP_CONTENT = "WRAP_CONTENT"
        private const val KEY_CODE_COMPLETION = "CODE_COMPLETION"
        private const val KEY_PINCH_ZOOM = "PINCH_ZOOM"
        private const val KEY_SHOW_LINE_NUMBERS = "SHOW_LINE_NUMBERS"
        private const val KEY_HIGHLIGHT_CURRENT_LINE = "HIGHLIGHT_CURRENT_LINE"
        private const val KEY_HIGHLIGHT_MATCHING_DELIMITERS = "HIGHLIGHT_MATCHING_DELIMITERS"

        //Keyboard
        private const val KEY_USE_EXTENDED_KEYBOARD = "USE_EXTENDED_KEYS"
        private const val KEY_USE_IME_KEYBOARD = "USE_IME_KEYBOARD"

        //Code Style
        private const val KEY_INDENT_LINE = "INDENT_LINE"
        private const val KEY_INSERT_BRACKET = "INSERT_BRACKET"
        private const val KEY_INSERT_QUOTE = "INSERT_QUOTE"

        //File Explorer
        private const val KEY_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES"
        private const val KEY_FOLDERS_ON_TOP = "FOLDERS_ON_TOP"
        private const val KEY_SORT_MODE = "SORT_MODE"
    }

    fun getTheme(): String = sharedPreferences.getString(KEY_THEME, "IDENTIFICATOR_DARCULA")
    fun getFullscreenMode(): Boolean = sharedPreferences.getBoolean(KEY_FULLSCREEN_MODE, false)

    fun getConfirmExit(): Boolean = sharedPreferences.getBoolean(KEY_CONFIRM_EXIT, true)

    fun getFontSize(): Int = Integer.parseInt(sharedPreferences.getString(KEY_FONT_SIZE, "14"))
    fun getFontType(): String = sharedPreferences.getString(KEY_FONT_TYPE, "Droid Sans Mono")

    fun getResumeSession(): Boolean = sharedPreferences.getBoolean(KEY_RESUME_SESSION, true)
    fun getMaxTabsCount(): Int = Integer.parseInt(sharedPreferences.getString(KEY_MAX_TABS_COUNT, "5"))

    fun getWrapContent(): Boolean = sharedPreferences.getBoolean(KEY_WRAP_CONTENT, true)
    fun getCodeCompletion(): Boolean = sharedPreferences.getBoolean(KEY_CODE_COMPLETION, true)
    fun getPinchZoom(): Boolean = sharedPreferences.getBoolean(KEY_PINCH_ZOOM, true)
    fun getShowLineNumbers(): Boolean = sharedPreferences.getBoolean(KEY_SHOW_LINE_NUMBERS, true)
    fun getHighlightCurrentLine(): Boolean = sharedPreferences.getBoolean(KEY_HIGHLIGHT_CURRENT_LINE, true)
    fun getHighlightMatchingDelimiters(): Boolean = sharedPreferences.getBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, true)

    fun getExtendedKeyboard(): Boolean = sharedPreferences.getBoolean(KEY_USE_EXTENDED_KEYBOARD, true)
    fun getImeKeyboard(): Boolean = sharedPreferences.getBoolean(KEY_USE_IME_KEYBOARD, false)

    fun getIndentLine(): Boolean = sharedPreferences.getBoolean(KEY_INDENT_LINE, true)
    fun getInsertBracket(): Boolean = sharedPreferences.getBoolean(KEY_INSERT_BRACKET, true)
    fun getInsertQuote(): Boolean = sharedPreferences.getBoolean(KEY_INSERT_QUOTE, false)

    fun getFilterHidden(): Boolean = sharedPreferences.getBoolean(KEY_SHOW_HIDDEN_FILES, true)
    fun setFilterHidden(showHiddenFiles: Boolean) = sharedPreferences.edit().putBoolean(KEY_SHOW_HIDDEN_FILES, showHiddenFiles).apply()
    fun getFoldersOnTop(): Boolean = sharedPreferences.getBoolean(KEY_FOLDERS_ON_TOP, true)
    fun getSortMode(): Int = Integer.parseInt(sharedPreferences.getString(KEY_SORT_MODE, "0")) //Сортировка по имени
    fun setSortMode(sortMode: String) = sharedPreferences.edit().putString(KEY_SORT_MODE, sortMode).apply()
}