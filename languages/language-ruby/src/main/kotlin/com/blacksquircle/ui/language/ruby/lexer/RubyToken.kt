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

package com.blacksquircle.ui.language.ruby.lexer

enum class RubyToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    INSTANCE_VARIABLE,
    _ENCODING,
    _LINE,
    _FILE,

    ALIAS,
    DEFINED,
    SUPER,
    SELF,
    UNDEF,
    CLASS,
    DEF,
    MODULE,
    RETURN,
    BEGIN,
    BREAK,
    DO,
    ENSURE,
    FOR,
    IN,
    NEXT,
    NOT,
    OR,
    REDO,
    RESCUE,
    RETRY,
    YIELD,
    UNLESS,
    WHILE,
    IF,
    CASE,
    WHEN,
    THEN,
    ELSE,
    ELSIF,
    END,
    UNTIL,
    METHOD,
    AND_KEYWORD,
    OR_KEYWORD,
    NOT_KEYWORD,

    NIL,
    TRUE,
    FALSE,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,

    PLUS,
    MINUS,
    MULT,
    POW,
    DIV,
    MOD,
    LT,
    GT,
    EQ,

    OROR,
    ANDAND,
    LTLT,
    GTGT,
    AND,
    XOR,
    TILDE,

    LTEQ,
    GTEQ,
    EQEQ,
    NOTEQ,

    LTGT,
    COMMA,
    COLON,
    DOT,
    RANGE,
    BACKTICK,
    SEMICOLON,

    PLUSEQ,
    MINUSEQ,
    MULTEQ,
    DIVEQ,
    MODEQ,
    ANDEQ,
    OREQ,
    XOREQ,
    GTGTEQ,
    LTLTEQ,
    POWEQ,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,
    EMBEDDED_LITERAL,

    LINE_COMMENT,
    BLOCK_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}