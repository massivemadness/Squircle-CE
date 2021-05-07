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

package com.blacksquircle.ui.language.c.lexer

enum class CToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    AUTO,
    BREAK,
    CASE,
    CONST,
    CONTINUE,
    DEFAULT,
    DO,
    ELSE,
    ENUM,
    EXTERN,
    FOR,
    GOTO,
    IF,
    REGISTER,
    SIZEOF,
    STATIC,
    STRUCT,
    SWITCH,
    TYPEDEF,
    UNION,
    VOLATILE,
    WHILE,
    RETURN,

    NULL,
    TRUE,
    FALSE,

    BOOL,
    CHAR,
    DIV_T,
    DOUBLE,
    FLOAT,
    INT,
    LDIV_T,
    LONG,
    SHORT,
    SIGNED,
    SIZE_T,
    UNSIGNED,
    VOID,
    WCHAR_T,

    FUNCTION,

    TRIGRAPH,
    EQ,
    PLUS,
    MINUS,
    MULT,
    DIV,
    MOD,
    TILDA,
    LT,
    GT,
    LTLT,
    GTGT,
    EQEQ,
    PLUSEQ,
    MINUSEQ,
    MULTEQ,
    DIVEQ,
    MODEQ,
    ANDEQ,
    OREQ,
    XOREQ,
    GTEQ,
    LTEQ,
    NOTEQ,
    GTGTEQ,
    LTLTEQ,
    XOR,
    AND,
    ANDAND,
    OR,
    OROR,
    QUEST,
    COLON,
    COMMA,
    NOT,
    PLUSPLUS,
    MINUSMINUS,
    DOT,
    SEMICOLON,
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,

    __DATE__,
    __TIME__,
    __FILE__,
    __LINE__,
    __STDC__,
    PREPROCESSOR,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,

    LINE_COMMENT,
    BLOCK_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}