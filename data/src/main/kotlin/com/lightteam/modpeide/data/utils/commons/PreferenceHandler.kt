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

package com.lightteam.modpeide.data.utils.commons

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

class PreferenceHandler(private val rxSharedPreferences: RxSharedPreferences) {

    companion object {
        // Look And Feel
        // const val KEY_THEME = "THEME_1"
        const val KEY_COLOR_SCHEME = "COLOR_SCHEME"
        const val KEY_FULLSCREEN_MODE = "FULLSCREEN_MODE"

        // Other
        const val KEY_CONFIRM_EXIT = "CONFIRM_EXIT"

        // Font
        const val KEY_FONT_SIZE = "FONT_SIZE_2"
        const val KEY_FONT_TYPE = "FONT_TYPE_3"

        // Tabs
        const val KEY_RESUME_SESSION = "RESUME_SESSION"
        const val KEY_TAB_LIMIT = "TAB_LIMIT"
        const val KEY_SELECTED_DOCUMENT_ID = "SELECTED_DOCUMENT_ID"

        // Editor
        const val KEY_WORD_WRAP = "WORD_WRAP"
        const val KEY_CODE_COMPLETION = "CODE_COMPLETION"
        const val KEY_ERROR_HIGHLIGHTING = "ERROR_HIGHLIGHTING"
        const val KEY_PINCH_ZOOM = "PINCH_ZOOM"
        const val KEY_HIGHLIGHT_CURRENT_LINE = "HIGHLIGHT_CURRENT_LINE"
        const val KEY_HIGHLIGHT_MATCHING_DELIMITERS = "HIGHLIGHT_MATCHING_DELIMITERS"

        // Keyboard
        const val KEY_USE_EXTENDED_KEYBOARD = "USE_EXTENDED_KEYBOARD"
        const val KEY_KEYBOARD_PRESET = "KEYBOARD_PRESET"
        const val KEY_USE_SOFT_KEYBOARD = "USE_SOFT_KEYBOARD"

        // Encoding
        const val KEY_ENCODING_AUTO_DETECT = "ENCODING_AUTO_DETECT"
        const val KEY_ENCODING_FOR_OPENING = "ENCODING_FOR_OPENING"
        const val KEY_ENCODING_FOR_SAVING = "ENCODING_FOR_SAVING"

        // Linebreaks
        const val KEY_LINEBREAK_FOR_SAVING = "LINEBREAK_FOR_SAVING"

        // File Explorer
        const val KEY_OPEN_UNKNOWN_FILE_IN_EDITOR = "OPEN_UNKNOWN_FILE_IN_EDITOR"
        const val KEY_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES"
        const val KEY_FOLDERS_ON_TOP = "FOLDERS_ON_TOP"
        const val KEY_VIEW_MODE = "VIEW_MODE"
        const val KEY_SORT_MODE = "SORT_MODE"

        // Code Style
        const val KEY_AUTO_INDENTATION = "AUTO_INDENTATION"
        const val KEY_AUTO_CLOSE_BRACKETS = "AUTO_CLOSE_BRACKETS"
        const val KEY_AUTO_CLOSE_QUOTES = "AUTO_CLOSE_QUOTES"
    }

    fun getColorScheme(): Preference<String> = rxSharedPreferences.getString(KEY_COLOR_SCHEME, "964c249d-ad3c-4d85-8010-f3d55c1ae0a2") // Darcula
    fun getFullscreenMode(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_FULLSCREEN_MODE, false)

    fun getConfirmExit(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_CONFIRM_EXIT, true)

    fun getFontSize(): Preference<Int> = rxSharedPreferences.getInteger(KEY_FONT_SIZE, 14)
    fun getFontType(): Preference<String> = rxSharedPreferences.getString(KEY_FONT_TYPE, "file:///android_asset/fonts/jetbrains_mono.ttf") // JetBrains Mono

    fun getResumeSession(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_RESUME_SESSION, true)
    fun getTabLimit(): Preference<Int> = rxSharedPreferences.getInteger(KEY_TAB_LIMIT, 5)
    fun getSelectedDocumentId(): Preference<String> = rxSharedPreferences.getString(KEY_SELECTED_DOCUMENT_ID, "whatever")

    fun getWordWrap(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_WORD_WRAP, true)
    fun getCodeCompletion(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_CODE_COMPLETION, true)
    fun getErrorHighlighting(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_ERROR_HIGHLIGHTING, true)
    fun getPinchZoom(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_PINCH_ZOOM, true)
    fun getHighlightCurrentLine(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_HIGHLIGHT_CURRENT_LINE, true)
    fun getHighlightMatchingDelimiters(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, true)

    fun getExtendedKeyboard(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_USE_EXTENDED_KEYBOARD, true)
    fun getKeyboardPreset(): Preference<String> = rxSharedPreferences.getString(KEY_KEYBOARD_PRESET, "59d24164-3e1f-4a6d-bb8d-0ee23b4083e6") // Default
    fun getSoftKeyboard(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_USE_SOFT_KEYBOARD, false)

    fun getEncodingAutoDetect(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_ENCODING_AUTO_DETECT, false)
    fun getEncodingForOpening(): Preference<String> = rxSharedPreferences.getString(KEY_ENCODING_FOR_OPENING, "UTF-8")
    fun getEncodingForSaving(): Preference<String> = rxSharedPreferences.getString(KEY_ENCODING_FOR_SAVING, "UTF-8")

    fun getLinebreakForSaving(): Preference<String> = rxSharedPreferences.getString(KEY_LINEBREAK_FOR_SAVING, "2")

    fun getOpenUnknownFile(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_OPEN_UNKNOWN_FILE_IN_EDITOR, false)
    fun getFilterHidden(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_SHOW_HIDDEN_FILES, true)
    fun getFoldersOnTop(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_FOLDERS_ON_TOP, true)
    fun getViewMode(): Preference<String> = rxSharedPreferences.getString(KEY_VIEW_MODE, "0")
    fun getSortMode(): Preference<String> = rxSharedPreferences.getString(KEY_SORT_MODE, "0")

    fun getAutoIndentation(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_AUTO_INDENTATION, true)
    fun getAutoCloseBrackets(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_AUTO_CLOSE_BRACKETS, true)
    fun getAutoCloseQuotes(): Preference<Boolean> = rxSharedPreferences.getBoolean(KEY_AUTO_CLOSE_QUOTES, true)
}