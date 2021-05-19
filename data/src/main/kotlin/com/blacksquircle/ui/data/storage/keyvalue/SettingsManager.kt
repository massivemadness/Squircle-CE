/*
 * Copyright 2021 Squircle IDE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.data.storage.keyvalue

import android.content.SharedPreferences

class SettingsManager(private val sharedPreferences: SharedPreferences) {

    companion object {

        // Look And Feel
        const val KEY_COLOR_SCHEME = "COLOR_SCHEME"
        const val KEY_FULLSCREEN_MODE = "FULLSCREEN_MODE"

        // Other
        const val KEY_CONFIRM_EXIT = "CONFIRM_EXIT"

        // Font
        const val KEY_FONT_SIZE = "FONT_SIZE_2"
        const val KEY_FONT_TYPE = "FONT_TYPE_3"

        // Editor
        const val KEY_WORD_WRAP = "WORD_WRAP"
        const val KEY_CODE_COMPLETION = "CODE_COMPLETION"
        const val KEY_ERROR_HIGHLIGHTING = "ERROR_HIGHLIGHTING"
        const val KEY_PINCH_ZOOM = "PINCH_ZOOM"
        const val KEY_HIGHLIGHT_CURRENT_LINE = "HIGHLIGHT_CURRENT_LINE"
        const val KEY_HIGHLIGHT_MATCHING_DELIMITERS = "HIGHLIGHT_MATCHING_DELIMITERS"

        // Tabs
        const val KEY_SELECTED_DOCUMENT_ID = "SELECTED_DOCUMENT_ID"
        const val KEY_AUTO_SAVE_FILES = "AUTO_SAVE_FILES"

        // Keyboard
        const val KEY_USE_EXTENDED_KEYBOARD = "USE_EXTENDED_KEYBOARD"
        const val KEY_KEYBOARD_PRESET = "KEYBOARD_PRESET_1"
        const val KEY_USE_SOFT_KEYBOARD = "USE_SOFT_KEYBOARD"

        // Code Style
        const val KEY_AUTO_INDENTATION = "AUTO_INDENTATION"
        const val KEY_AUTO_CLOSE_BRACKETS = "AUTO_CLOSE_BRACKETS"
        const val KEY_AUTO_CLOSE_QUOTES = "AUTO_CLOSE_QUOTES"

        // Tab Options
        const val KEY_USE_SPACES_NOT_TABS = "USE_SPACES_NOT_TABS"
        const val KEY_TAB_WIDTH = "TAB_WIDTH"

        // Encoding
        const val KEY_ENCODING_AUTO_DETECT = "ENCODING_AUTO_DETECT"
        const val KEY_ENCODING_FOR_OPENING = "ENCODING_FOR_OPENING"
        const val KEY_ENCODING_FOR_SAVING = "ENCODING_FOR_SAVING"

        // Linebreaks
        const val KEY_LINEBREAK_FOR_SAVING = "LINEBREAK_FOR_SAVING"

        // File Explorer
        const val KEY_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES"
        const val KEY_FOLDERS_ON_TOP = "FOLDERS_ON_TOP"
        const val KEY_VIEW_MODE = "VIEW_MODE"
        const val KEY_SORT_MODE = "SORT_MODE"
    }

    var colorScheme: String
        get() = sharedPreferences.getString(KEY_COLOR_SCHEME, "DARCULA") ?: "DARCULA"
        set(value) = sharedPreferences.edit().putString(KEY_COLOR_SCHEME, value).apply()
    var fullScreenMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_FULLSCREEN_MODE, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FULLSCREEN_MODE, value).apply()

    var confirmExit: Boolean
        get() = sharedPreferences.getBoolean(KEY_CONFIRM_EXIT, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_CONFIRM_EXIT, value).apply()

    var fontSize: Int
        get() = sharedPreferences.getInt(KEY_FONT_SIZE, 14)
        set(value) = sharedPreferences.edit().putInt(KEY_FONT_SIZE, value).apply()
    var fontType: String
        get() = sharedPreferences.getString(KEY_FONT_TYPE, "file:///android_asset/fonts/jetbrains_mono.ttf") ?: "file:///android_asset/fonts/jetbrains_mono.ttf"
        set(value) = sharedPreferences.edit().putString(KEY_FONT_TYPE, value).apply()

    var selectedDocumentId: String
        get() = sharedPreferences.getString(KEY_SELECTED_DOCUMENT_ID, "whatever") ?: "whatever"
        set(value) = sharedPreferences.edit().putString(KEY_SELECTED_DOCUMENT_ID, value).apply()

    var wordWrap: Boolean
        get() = sharedPreferences.getBoolean(KEY_WORD_WRAP, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_WORD_WRAP, value).apply()
    var codeCompletion: Boolean
        get() = sharedPreferences.getBoolean(KEY_CODE_COMPLETION, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_CODE_COMPLETION, value).apply()
    var errorHighlighting: Boolean
        get() = sharedPreferences.getBoolean(KEY_ERROR_HIGHLIGHTING, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_ERROR_HIGHLIGHTING, value).apply()
    var pinchZoom: Boolean
        get() = sharedPreferences.getBoolean(KEY_PINCH_ZOOM, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_PINCH_ZOOM, value).apply()
    var highlightCurrentLine: Boolean
        get() = sharedPreferences.getBoolean(KEY_HIGHLIGHT_CURRENT_LINE, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_HIGHLIGHT_CURRENT_LINE, value).apply()
    var highlightMatchingDelimiters: Boolean
        get() = sharedPreferences.getBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, value).apply()

    var autoSaveFiles: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_SAVE_FILES, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_SAVE_FILES, value).apply()

    var extendedKeyboard: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_EXTENDED_KEYBOARD, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_USE_EXTENDED_KEYBOARD, value).apply()
    var keyboardPreset: String
        get() = sharedPreferences.getString(KEY_KEYBOARD_PRESET, "{}();,.=|&![]<>+-/*?:_") ?: "{}();,.=|&![]<>+-/*?:_"
        set(value) = sharedPreferences.edit().putString(KEY_KEYBOARD_PRESET, value).apply()
    var softKeyboard: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_SOFT_KEYBOARD, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_USE_SOFT_KEYBOARD, value).apply()

    var autoIndentation: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_INDENTATION, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_INDENTATION, value).apply()
    var autoCloseBrackets: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_CLOSE_BRACKETS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_CLOSE_BRACKETS, value).apply()
    var autoCloseQuotes: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_CLOSE_QUOTES, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_CLOSE_QUOTES, value).apply()

    var useSpacesInsteadOfTabs: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_SPACES_NOT_TABS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_USE_SPACES_NOT_TABS, value).apply()
    var tabWidth: Int
        get() = sharedPreferences.getInt(KEY_TAB_WIDTH, 4)
        set(value) = sharedPreferences.edit().putInt(KEY_TAB_WIDTH, value).apply()

    var encodingAutoDetect: Boolean
        get() = sharedPreferences.getBoolean(KEY_ENCODING_AUTO_DETECT, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_ENCODING_AUTO_DETECT, value).apply()
    var encodingForOpening: String
        get() = sharedPreferences.getString(KEY_ENCODING_FOR_OPENING, "UTF-8") ?: "UTF-8"
        set(value) = sharedPreferences.edit().putString(KEY_ENCODING_FOR_OPENING, value).apply()
    var encodingForSaving: String
        get() = sharedPreferences.getString(KEY_ENCODING_FOR_SAVING, "UTF-8") ?: "UTF-8"
        set(value) = sharedPreferences.edit().putString(KEY_ENCODING_FOR_SAVING, value).apply()

    var lineBreakForSaving: String
        get() = sharedPreferences.getString(KEY_LINEBREAK_FOR_SAVING, "2") ?: "2"
        set(value) = sharedPreferences.edit().putString(KEY_LINEBREAK_FOR_SAVING, value).apply()

    // TODO: 2020/8/7  For the following lines, the file explorer needs to refresh when its value changes
    var filterHidden: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HIDDEN_FILES, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SHOW_HIDDEN_FILES, value).apply()
    var foldersOnTop: Boolean
        get() = sharedPreferences.getBoolean(KEY_FOLDERS_ON_TOP, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FOLDERS_ON_TOP, value).apply()
    var viewMode: String
        get() = sharedPreferences.getString(KEY_VIEW_MODE, "0") ?: "0"
        set(value) = sharedPreferences.edit().putString(KEY_VIEW_MODE, value).apply()
    var sortMode: String
        get() = sharedPreferences.getString(KEY_SORT_MODE, "0") ?: "0"
        set(value) = sharedPreferences.edit().putString(KEY_SORT_MODE, value).apply()

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}