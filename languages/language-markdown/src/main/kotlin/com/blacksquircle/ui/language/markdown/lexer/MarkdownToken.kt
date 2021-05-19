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

package com.blacksquircle.ui.language.markdown.lexer

enum class MarkdownToken {
    HEADER,

    UNORDERED_LIST_ITEM,
    ORDERED_LIST_ITEM,

    BOLDITALIC1,
    BOLDITALIC2,
    BOLD1,
    BOLD2,
    ITALIC1,
    ITALIC2,
    STRIKETHROUGH,

    CODE,
    CODE_BLOCK,

    LT,
    GT,
    EQ,
    NOT,
    DIV,
    MINUS,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,

    URL,
    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}