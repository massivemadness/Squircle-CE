/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.core.settings

import android.content.Context
import android.content.SharedPreferences

typealias OnChangedListener = () -> Unit

class SettingsManager(private val context: Context) {

    companion object {

        // Look And Feel
        const val KEY_EDITOR_THEME = "editor_theme_internal"
        const val KEY_FULLSCREEN_MODE = "fullscreen_mode"

        // Other
        const val KEY_CONFIRM_EXIT = "confirm_exit"

        // Font
        const val KEY_FONT_SIZE = "font_size"
        const val KEY_FONT_TYPE = "font_type_internal"

        // Editor
        const val KEY_WORD_WRAP = "word_wrap"
        const val KEY_CODE_COMPLETION = "code_completion"
        const val KEY_PINCH_ZOOM = "pinch_zoom"
        const val KEY_LINE_NUMBERS = "line_numbers"
        const val KEY_HIGHLIGHT_CURRENT_LINE = "highlight_current_line"
        const val KEY_HIGHLIGHT_MATCHING_DELIMITERS = "highlight_matching_delimiters"
        const val KEY_HIGHLIGHT_CODE_BLOCKS = "highlight_code_blocks"
        const val KEY_SHOW_INVISIBLE_CHARS = "show_invisible_chars"
        const val KEY_READ_ONLY = "read_only"

        // Tabs
        const val KEY_SELECTED_DOCUMENT_ID = "selected_document_id"
        const val KEY_AUTO_SAVE_FILES = "auto_save_files"

        // Keyboard
        const val KEY_USE_EXTENDED_KEYBOARD = "use_extended_keyboard"
        const val KEY_KEYBOARD_PRESET = "keyboard_preset"
        const val KEY_SOFT_KEYBOARD = "soft_keyboard"

        // Code Style
        const val KEY_AUTO_INDENTATION = "auto_indentation"
        const val KEY_AUTO_CLOSE_PAIRS = "auto_close_pairs"

        // Tab Options
        const val KEY_USE_SPACES_NOT_TABS = "use_spaces_not_tabs"
        const val KEY_TAB_WIDTH = "tab_width"

        // Encoding
        const val KEY_ENCODING_AUTO_DETECT = "encoding_auto_detect"
        const val KEY_ENCODING_FOR_OPENING = "encoding_for_opening"
        const val KEY_ENCODING_FOR_SAVING = "encoding_for_saving"

        // Linebreaks
        const val KEY_LINEBREAK_FOR_SAVING = "linebreak_for_saving"

        // File Explorer
        const val KEY_WORKSPACE = "workspace"
        const val KEY_SHOW_HIDDEN_FILES = "show_hidden_files"
        const val KEY_COMPACT_PACKAGES = "compact_packages"
        const val KEY_FOLDERS_ON_TOP = "folders_on_top"
        const val KEY_SORT_MODE = "sort_mode"

        // Git
        const val KEY_GIT_CREDENTIALS_USERNAME = "git_credentials_username"
        const val KEY_GIT_CREDENTIALS_PASSWORD = "git_credentials_password"
        const val KEY_GIT_USER_EMAIL = "git_user_email"
        const val KEY_GIT_USER_NAME = "git_user_name"
    }

    private val fileName: String
        get() = context.packageName + "_preferences"

    private var _sharedPreferences: SharedPreferences? = null
    private val sharedPreferences: SharedPreferences
        get() = _sharedPreferences ?: context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
            .also { _sharedPreferences = it }

    var editorTheme: String
        get() = sharedPreferences.getString(KEY_EDITOR_THEME, "darcula") ?: "darcula"
        set(value) = sharedPreferences.edit().putString(KEY_EDITOR_THEME, value).apply()
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
        get() = sharedPreferences.getString(KEY_FONT_TYPE, "jetbrains_mono") ?: "jetbrains_mono"
        set(value) = sharedPreferences.edit().putString(KEY_FONT_TYPE, value).apply()

    var wordWrap: Boolean
        get() = sharedPreferences.getBoolean(KEY_WORD_WRAP, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_WORD_WRAP, value).apply()
    var codeCompletion: Boolean
        get() = sharedPreferences.getBoolean(KEY_CODE_COMPLETION, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_CODE_COMPLETION, value).apply()

    var pinchZoom: Boolean
        get() = sharedPreferences.getBoolean(KEY_PINCH_ZOOM, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_PINCH_ZOOM, value).apply()
    var lineNumbers: Boolean
        get() = sharedPreferences.getBoolean(KEY_LINE_NUMBERS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_LINE_NUMBERS, value).apply()
    var highlightCurrentLine: Boolean
        get() = sharedPreferences.getBoolean(KEY_HIGHLIGHT_CURRENT_LINE, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_HIGHLIGHT_CURRENT_LINE, value).apply()
    var highlightMatchingDelimiters: Boolean
        get() = sharedPreferences.getBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_HIGHLIGHT_MATCHING_DELIMITERS, value).apply()
    var highlightCodeBlocks: Boolean
        get() = sharedPreferences.getBoolean(KEY_HIGHLIGHT_CODE_BLOCKS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_HIGHLIGHT_CODE_BLOCKS, value).apply()
    var showInvisibleChars: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_INVISIBLE_CHARS, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SHOW_INVISIBLE_CHARS, value).apply()
    var readOnly: Boolean
        get() = sharedPreferences.getBoolean(KEY_READ_ONLY, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_READ_ONLY, value).apply()

    var selectedUuid: String
        get() = sharedPreferences.getString(KEY_SELECTED_DOCUMENT_ID, "").orEmpty()
        set(value) = sharedPreferences.edit().putString(KEY_SELECTED_DOCUMENT_ID, value).apply()
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
        get() = sharedPreferences.getBoolean(KEY_SOFT_KEYBOARD, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SOFT_KEYBOARD, value).apply()

    var autoIndentation: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_INDENTATION, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_INDENTATION, value).apply()
    var autoClosePairs: Boolean
        get() = sharedPreferences.getBoolean(KEY_AUTO_CLOSE_PAIRS, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_AUTO_CLOSE_PAIRS, value).apply()

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
        get() = sharedPreferences.getString(KEY_LINEBREAK_FOR_SAVING, "lf") ?: "lf"
        set(value) = sharedPreferences.edit().putString(KEY_LINEBREAK_FOR_SAVING, value).apply()

    var showHidden: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHOW_HIDDEN_FILES, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SHOW_HIDDEN_FILES, value).apply()
    var compactPackages: Boolean
        get() = sharedPreferences.getBoolean(KEY_COMPACT_PACKAGES, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_COMPACT_PACKAGES, value).apply()
    var foldersOnTop: Boolean
        get() = sharedPreferences.getBoolean(KEY_FOLDERS_ON_TOP, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FOLDERS_ON_TOP, value).apply()
    var sortMode: String
        get() = sharedPreferences.getString(KEY_SORT_MODE, "sort_by_name") ?: "sort_by_name"
        set(value) = sharedPreferences.edit().putString(KEY_SORT_MODE, value).apply()
    var workspace: String
        get() = sharedPreferences.getString(KEY_WORKSPACE, "local") ?: "local"
        set(value) = sharedPreferences.edit().putString(KEY_WORKSPACE, value).apply()
    var gitCredentialsUsername: String
        get() = sharedPreferences.getString(KEY_GIT_CREDENTIALS_USERNAME, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_GIT_CREDENTIALS_USERNAME, value).apply()
    var gitCredentialsPassword: String
        get() = sharedPreferences.getString(KEY_GIT_CREDENTIALS_PASSWORD, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_GIT_CREDENTIALS_PASSWORD, value).apply()
    var gitUserEmail: String
        get() = sharedPreferences.getString(KEY_GIT_USER_EMAIL, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_GIT_USER_EMAIL, value).apply()
    var gitUserName: String
        get() = sharedPreferences.getString(KEY_GIT_USER_NAME, "") ?: ""
        set(value) = sharedPreferences.edit().putString(KEY_GIT_USER_NAME, value).apply()

    private val listeners = HashMap<String, OnChangedListener>()
    private val callback = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key in listeners.keys) {
            listeners[key]?.invoke()
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(callback)
    }

    fun load(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun update(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun registerListener(key: String, onValueChanged: OnChangedListener) {
        listeners[key] = onValueChanged
    }

    fun unregisterListener(key: String) {
        listeners.remove(key)
    }
}