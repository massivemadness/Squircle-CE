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

package com.blacksquircle.ui.feature.keybindings.domain.model

/**
 * 10078 - 1 ctrl, 0 shift, 0 alt, 42 key code
 */
enum class Keybinding(val key: String, val defaultValue: String) {
    NEW("KEYBINDING_NEW", "10042"), // Ctrl + N
    OPEN("KEYBINDING_OPEN", "10043"), // Ctrl + O
    SAVE("KEYBINDING_SAVE", "10047"), // Ctrl + S
    SAVE_AS("KEYBINDING_SAVE_AS", "11047"), // Ctrl + Shift + S
    CLOSE("KEYBINDING_CLOSE", "10051"), // Ctrl + W
    CUT("KEYBINDING_CUT", "10052"), // Ctrl + X
    COPY("KEYBINDING_COPY", "10031"), // Ctrl + C
    PASTE("KEYBINDING_PASTE", "10050"), // Ctrl + V
    SELECT_ALL("KEYBINDING_SELECT_ALL", "10029"), // Ctrl + A
    SELECT_LINE("KEYBINDING_SELECT_LINE", "00129"), // Alt + A
    DELETE_LINE("KEYBINDING_DELETE_LINE", "10067"), // Ctrl + Backspace
    DUPLICATE_LINE("KEYBINDING_DUPLICATE_LINE", "10032"), // Ctrl + D
    PREV_WORD("KEYBINDING_PREV_WORD", "00121"), // Alt + ←
    NEXT_WORD("KEYBINDING_NEXT_WORD", "00122"), // Alt + →
    LINE_START("KEYBINDING_LINE_START", "10021"), // Ctrl + ←
    LINE_END("KEYBINDING_LINE_END", "10022"), // Ctrl + →
    UNDO("KEYBINDING_UNDO", "10054"), // Ctrl + Z
    REDO("KEYBINDING_REDO", "11054"), // Ctrl + Shift + Z
    FIND("KEYBINDING_FIND", "10034"), // Ctrl + F
    REPLACE("KEYBINDING_REPLACE", "10046"), // Ctrl + R
    GOTO_LINE("KEYBINDING_GOTO_LINE", "10035"), // Ctrl + G
    FORCE_SYNTAX("KEYBINDING_FORCE_SYNTAX", ""), // None set
    COLOR_PICKER("KEYBINDING_COLOR_PICKER", "") // None set
}