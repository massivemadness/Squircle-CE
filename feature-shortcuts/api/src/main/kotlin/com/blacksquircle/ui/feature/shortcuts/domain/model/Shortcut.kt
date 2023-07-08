/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.shortcuts.domain.model

/**
 * 100N - 1 ctrl, 0 shift, 0 alt, N char
 */
enum class Shortcut(val key: String, val defaultValue: String) {
    NEW("shortcut_new", "100N"), // Ctrl + N
    OPEN("shortcut_open", "100O"), // Ctrl + O
    SAVE("shortcut_save", "100S"), // Ctrl + S
    SAVE_AS("shortcut_save_as", "110S"), // Ctrl + Shift + S
    CLOSE("shortcut_close", "100W"), // Ctrl + W
    CUT("shortcut_cut", "100X"), // Ctrl + X
    COPY("shortcut_copy", "100C"), // Ctrl + C
    PASTE("shortcut_paste", "100V"), // Ctrl + V
    SELECT_ALL("shortcut_select_all", "100A"), // Ctrl + A
    SELECT_LINE("shortcut_select_line", "001A"), // Alt + A
    DELETE_LINE("shortcut_delete_line", "100\u232B"), // Ctrl + ⌫
    DUPLICATE_LINE("shortcut_duplicate_line", "100D"), // Ctrl + D
    TOGGLE_CASE("shortcut_toggle_case", "110U"), // Ctrl + Shift + U
    PREV_WORD("shortcut_prev_word", "001\u2190"), // Alt + ←
    NEXT_WORD("shortcut_next_word", "001\u2192"), // Alt + →
    START_OF_LINE("shortcut_start_of_line", "100\u2190"), // Ctrl + ←
    END_OF_LINE("shortcut_end_of_line", "100\u2192"), // Ctrl + →
    UNDO("shortcut_undo", "100Z"), // Ctrl + Z
    REDO("shortcut_redo", "110Z"), // Ctrl + Shift + Z
    FIND("shortcut_find", "100F"), // Ctrl + F
    REPLACE("shortcut_replace", "100R"), // Ctrl + R
    GOTO_LINE("shortcut_goto_line", "100G"), // Ctrl + G
    FORCE_SYNTAX("shortcut_force_syntax", "000\u0000"), // None set
    INSERT_COLOR("shortcut_insert_color", "000\u0000") // None set
}