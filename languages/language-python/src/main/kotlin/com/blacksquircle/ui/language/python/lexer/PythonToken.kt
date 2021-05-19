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

package com.blacksquircle.ui.language.python.lexer

enum class PythonToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    IMAGINARY_LITERAL,

    AND_KEYWORD,
    AS,
    ASSERT,
    BREAK,
    CLASS,
    CONTINUE,
    DEF,
    DEL,
    ELIF,
    ELSE,
    EXCEPT,
    EXEC,
    FINALLY,
    FOR,
    FROM,
    GLOBAL,
    IF,
    IMPORT,
    IN,
    IS,
    LAMBDA,
    NOT_KEYWORD,
    OR_KEYWORD,
    PASS,
    PRINT,
    RAISE,
    RETURN,
    TRY,
    WHILE,
    YIELD,

    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    SHORT,
    SIGNED,
    UNSIGNED,
    VOID,

    METHOD,

    TRUE,
    FALSE,
    NONE,

    PLUSEQ,
    MINUSEQ,
    EXPEQ,
    MULTEQ,
    ATEQ,
    FLOORDIVEQ,
    DIVEQ,
    MODEQ,
    ANDEQ,
    OREQ,
    XOREQ,
    GTGTEQ,
    LTLTEQ,
    LTLT,
    GTGT,
    EXP,
    FLOORDIV,
    LTEQ,
    GTEQ,
    EQEQ,
    NOTEQ,
    NOTEQ_OLD,
    RARROW,
    PLUS,
    MINUS,
    MULT,
    DIV,
    MOD,
    AND,
    OR,
    XOR,
    TILDE,
    LT,
    GT,
    AT,
    COLON,
    TICK,
    EQ,
    COLONEQ,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    DOT,

    DECORATOR,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,
    LONG_DOUBLE_QUOTED_STRING,
    LONG_SINGLE_QUOTED_STRING,

    LINE_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}