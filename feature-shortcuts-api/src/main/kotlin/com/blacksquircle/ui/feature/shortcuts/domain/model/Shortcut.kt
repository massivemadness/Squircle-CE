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
 * 10042 - 1 ctrl, 0 shift, 0 alt, 42 key code
 */
enum class Shortcut(val key: String, val defaultValue: String) {
    NEW("shortcut_new", "10042"), // Ctrl + N
    OPEN("shortcut_open", "10043"), // Ctrl + O
    SAVE("shortcut_save", "10047"), // Ctrl + S
    SAVE_AS("shortcut_save_as", "11047"), // Ctrl + Shift + S
    CLOSE("shortcut_close", "10051"), // Ctrl + W
    CUT("shortcut_cut", "10052"), // Ctrl + X
    COPY("shortcut_copy", "10031"), // Ctrl + C
    PASTE("shortcut_paste", "10050"), // Ctrl + V
    SELECT_ALL("shortcut_select_all", "10029"), // Ctrl + A
    SELECT_LINE("shortcut_select_line", "00129"), // Alt + A
    DELETE_LINE("shortcut_delete_line", "10067"), // Ctrl + DEL
    DUPLICATE_LINE("shortcut_duplicate_line", "10032"), // Ctrl + D
    PREV_WORD("shortcut_prev_word", "00121"), // Alt + ←
    NEXT_WORD("shortcut_next_word", "00122"), // Alt + →
    LINE_START("shortcut_line_start", "10021"), // Ctrl + ←
    LINE_END("shortcut_line_end", "10022"), // Ctrl + →
    UNDO("shortcut_undo", "10054"), // Ctrl + Z
    REDO("shortcut_redo", "11054"), // Ctrl + Shift + Z
    FIND("shortcut_find", "10034"), // Ctrl + F
    REPLACE("shortcut_replace", "10046"), // Ctrl + R
    GOTO_LINE("shortcut_goto_line", "10035"), // Ctrl + G
    FORCE_SYNTAX("shortcut_force_syntax", ""), // None set
    INSERT_COLOR("shortcut_insert_color", "") // None set
}