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

package com.blacksquircle.ui.language.kotlin.lexer

enum class KotlinToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    ABSTRACT,
    ACTUAL,
    ANNOTATION_KEYWORD,
    AS,
    AS_QUEST,
    ASSERT,
    BREAK,
    BY,
    CATCH,
    CLASS,
    COMPANION,
    CONST,
    CONSTRUCTOR,
    CONTINUE,
    DATA,
    DO,
    ELSE,
    ENUM,
    EXPECT,
    FINALLY,
    FOR,
    FUN,
    GET,
    IF,
    IMPLEMENTS,
    IMPORT,
    INTERFACE,
    IN,
    INFIX,
    INIT,
    INTERNAL,
    INLINE,
    IS,
    LATEINIT,
    NATIVE,
    OBJECT,
    OPEN,
    OPERATOR,
    OR_KEYWORD,
    OUT,
    OVERRIDE,
    PACKAGE,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    REIFIED,
    RETURN,
    SEALED,
    SET,
    SUPER,
    THIS,
    THROW,
    TRY,
    TYPEALIAS,
    VAL,
    VAR,
    VARARGS,
    WHEN,
    WHERE,
    WHILE,

    TRUE,
    FALSE,
    NULL,

    EQEQ,
    NOTEQ,
    OROR,
    PLUSPLUS,
    MINUSMINUS,

    LT,
    LTLT,
    LTEQ,
    LTLTEQ,

    GT,
    GTGT,
    GTGTGT,
    GTEQ,
    GTGTEQ,

    AND,
    ANDAND,

    PLUSEQ,
    MINUSEQ,
    MULTEQ,
    DIVEQ,
    ANDEQ,
    OREQ,
    XOREQ,
    MODEQ,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    DOT,

    EQ,
    NOT,
    TILDE,
    QUEST,
    COLON,
    PLUS,
    MINUS,
    MULT,
    DIV,
    OR,
    XOR,
    MOD,

    ELVIS,
    ELLIPSIS,
    DOUBLE_COLON,
    ARROW,

    ANNOTATION,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,

    LINE_COMMENT,
    BLOCK_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}