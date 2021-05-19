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

package com.blacksquircle.ui.language.visualbasic.lexer

enum class VisualBasicToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    KEYWORD,

    BOOLEAN,
    BYTE,
    CHAR,
    DATE,
    DECIMAL,
    DOUBLE,
    INTEGER,
    LONG,
    OBJECT,
    SBYTE,
    SHORT,
    SINGLE,
    STRING,
    UINTEGER,
    ULONG,
    USHORT,

    TRUE,
    FALSE,

    AND,
    ANDEQ,
    MULT,
    MULTEQ,
    PLUS,
    PLUSEQ,
    EQ,
    MINUS,
    MINUSEQ,
    LT,
    LTLT,
    LTLTEQ,
    GT,
    GTGT,
    GTGTEQ,
    DIV,
    DIVEQ,
    BACKSLASH,
    XOR,
    XOREQ,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    DOT,

    DOUBLE_QUOTED_STRING,

    LINE_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}