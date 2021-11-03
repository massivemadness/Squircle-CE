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

package com.blacksquircle.ui.language.julia.lexer

enum class JuliaToken {
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    KEYWORD_OTHER,
    KEYWORD_CONTROL,

    CONSTANTS,
    OPERATOR,

    /*BASE_MODULE_FUNCS,
    BASE_MACROS,
    BASE_MODULES,
    BASE_FUNCS,*/
    BASE_TYPES,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,
    LONG_DOUBLE_QUOTED_STRING,
    SINGLE_BACKTICK_STRING,

    LINE_COMMENT,
    BLOCK_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}