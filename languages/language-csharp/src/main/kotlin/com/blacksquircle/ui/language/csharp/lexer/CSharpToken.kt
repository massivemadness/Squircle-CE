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

package com.blacksquircle.ui.language.csharp.lexer

enum class CSharpToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    ABSTRACT,
    AS,
    ASYNC,
    AWAIT,
    BASE,
    BREAK,
    CASE,
    CATCH,
    CHECKED,
    CLASS,
    CONST,
    CONTINUE,
    DECIMAL,
    DEFAULT,
    DELEGATE,
    DO,
    DYNAMIC,
    ELSE,
    ENUM,
    EVENT,
    EXPLICIT,
    EXTERN,
    FINALLY,
    FIXED,
    FOR,
    FOREACH,
    GOTO,
    IF,
    IMPLICIT,
    IN,
    INTERFACE,
    INTERNAL,
    IS,
    LOCK,
    NAMESPACE,
    NEW,
    OPERATOR,
    OUT,
    OVERRIDE,
    PARAMS,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    READONLY,
    REF,
    RETURN,
    SEALED,
    SIZEOF,
    STACKALLOC,
    STATIC,
    STRUCT,
    SWITCH,
    THIS,
    THROW,
    TYPEOF,
    UNCHECKED,
    UNSAFE,
    USING,
    VAR,
    VIRTUAL,
    VOID,
    VOLATILE,
    WHILE,

    NULL,
    TRUE,
    FALSE,

    BOOL,
    BYTE,
    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    OBJECT,
    SBYTE,
    SHORT,
    STRING,
    UINT,
    ULONG,
    USHORT,

    PLUS,
    MINUSMINUS,
    DIV,
    PLUSPLUS,
    MOD,
    MULT,
    MINUS,

    PLUSEQ,
    DIVEQ,
    MODEQ,
    MULTEQ,
    MINUSEQ,

    EQ,

    AND,
    LTLT,
    TILDE,
    OR,
    GTGT,
    XOR,

    ANDEQ,
    LTLTEQ,
    OREQ,
    GTGTEQ,
    XOREQ,

    EQEQ,
    GT,
    GTEQ,
    NOTEQ,
    LT,
    LTEQ,

    ANDAND,
    NOT,
    OROR,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    DOT,
    QUEST,
    COLON,

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