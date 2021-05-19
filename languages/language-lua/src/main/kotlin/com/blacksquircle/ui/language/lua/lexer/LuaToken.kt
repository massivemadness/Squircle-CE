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

package com.blacksquircle.ui.language.lua.lexer

enum class LuaToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    BREAK,
    DO,
    ELSE,
    ELSEIF,
    END,
    FOR,
    FUNCTION,
    GOTO,
    IF,
    IN,
    LOCAL,
    NIL,
    REPEAT,
    RETURN,
    THEN,
    UNTIL,
    WHILE,
    AND,
    OR,
    NOT,

    TRUE,
    FALSE,
    NULL,

    _G,
    _VERSION,
    ASSERT,
    COLLECTGARBAGE,
    DOFILE,
    ERROR,
    GETFENV,
    GETMETATABLE,
    IPAIRS,
    LOAD,
    LOADFILE,
    LOADSTRING,
    MODULE,
    NEXT,
    PAIRS,
    PCALL,
    PRINT,
    RAWEQUAL,
    RAWGET,
    RAWSET,
    REQUIRE,
    SELECT,
    SETFENV,
    SETMETATABLE,
    TONUMBER,
    TOSTRING,
    TYPE,
    UNPACK,
    XPCALL,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    DOT,

    LT,
    GT,
    LTEQ,
    GTEQ,
    EQEQ,
    TILDEEQ,
    CONCAT,
    EQ,
    NOT_OPERATOR,
    TILDE,
    COLON,
    PLUS,
    MINUS,
    MULT,
    DIV,
    OR_OPERATOR,
    XOR,
    MOD,
    QUEST,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,

    LINE_COMMENT,
    BLOCK_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}