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

package com.lightteam.modpeide.data.storage.keyvalue

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

class PreferenceHandler(private val rxSharedPreferences: RxSharedPreferences) {

    companion object Key {
        //Look And Feel
        const val KEY_THEME = "THEME"
        const val KEY_FULLSCREEN_MODE = "FULLSCREEN_MODE"

        //Other
        const val KEY_CONFIRM_EXIT = "CONFIRM_EXIT"

        //Font
        const val KEY_FONT_SIZE = "FONT_SIZE_2"
        const val KEY_FONT_TYPE = "FONT_TYPE_1"

        //Tabs
        const val KEY_RESUME_SESSION = "RESUME_SESSION"
        const val KEY_TAB_LIMIT = "TAB_LIMIT"

        //Editor
        const val KEY_WORD_WRAP = "WORD_WRAP"
        const val KEY_CODE_COMPLETION = "CODE_COMPLETION"
        const val KEY_PINCH_ZOOM = "PINCH_ZOOM"
        const val KEY_HIGHLIGHT_CURRENT_LINE = "HIGHLIGHT_CURRENT_LINE"
        const val KEY_HIGHLIGHT_MATCHING_DELIMITERS = "HIGHLIGHT_MATCHING_DELIMITERS"

        //Keyboard
        const val KEY_USE_EXTENDED_KEYBOARD = "USE_EXTENDED_KEYBOARD"
        const val KEY_USE_SOFT_KEYBOARD = "USE_SOFT_KEYBOARD"
        const val KEY_USE_IME_KEYBOARD = "USE_IME_KEYBOARD"

        //Code Style
        const val KEY_AUTO_INDENTATION = "AUTO_INDENTATION"
        const val KEY_AUTOCLOSE_BRACKETS = "AUTOCLOSE_BRACKETS"
        const val KEY_AUTOCLOSE_QUOTES = "AUTOCLOSE_QUOTES"

        //File Explorer
        const val KEY_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES"
        const val KEY_FOLDERS_ON_TOP = "FOLDERS_ON_TOP"
        const val KEY_SORT_MODE = "SORT_MODE"
    }

    fun getTheme(): Preference<String> = rxSharedPreferences.getString(KEY_THEME, "THEME_DARCULA")
    fun getFullscreenMode(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_FULLSCREEN_MODE, false)

    fun getConfirmExit(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_CONFIRM_EXIT, true)

    fun getFontSize(): Preference<Int> = rxSharedPreferences.getInteger(KEY_FONT_SIZE, 14)
    fun getFontType(): Preference<String> = rxSharedPreferences.getString(KEY_FONT_TYPE, "droid_sans_mono")

    fun getResumeSession(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_RESUME_SESSION, true)
    fun getTabLimit(): Preference<Int> = rxSharedPreferences.getInteger(KEY_TAB_LIMIT, 5)

    fun getWordWrap(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_WORD_WRAP, true)
    fun getCodeCompletion(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_CODE_COMPLETION, true)
    fun getPinchZoom(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_PINCH_ZOOM, true)
    fun getHighlightCurrentLine(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_HIGHLIGHT_CURRENT_LINE, true)
    fun getHighlightMatchingDelimiters(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, true)

    fun getExtendedKeyboard(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_USE_EXTENDED_KEYBOARD, true)
    fun getSoftKeyboard(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_USE_SOFT_KEYBOARD, false)
    fun getImeKeyboard(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_USE_IME_KEYBOARD, false)

    fun getAutoIndentation(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_AUTO_INDENTATION, true)
    fun getAutoCloseBrackets(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_AUTOCLOSE_BRACKETS, true)
    fun getAutoCloseQuotes(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_AUTOCLOSE_QUOTES, false)

    fun getFilterHidden(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_SHOW_HIDDEN_FILES, true)
    fun getFoldersOnTop(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_FOLDERS_ON_TOP, true)
    fun getSortMode(): Preference<String> = rxSharedPreferences.getString(KEY_SORT_MODE, "0")

    //Setter
    fun setFilterHidden(showHiddenFiles: Boolean) = getFilterHidden().set(showHiddenFiles)
    fun setSortMode(sortMode: String) = getSortMode().set(sortMode)
}