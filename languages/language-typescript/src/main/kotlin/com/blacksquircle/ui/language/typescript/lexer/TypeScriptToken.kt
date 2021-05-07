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

package com.blacksquircle.ui.language.typescript.lexer

enum class TypeScriptToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    FUNCTION,
    PROTOTYPE,
    DEBUGGER,
    SUPER,
    ANY,
    THIS,
    ASYNC,
    AWAIT,
    EXPORT,
    FROM,
    EXTENDS,
    DECLARE,
    FINAL,
    IMPLEMENTS,
    NATIVE,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    STATIC,
    SYNCHRONIZED,
    CONSTRUCTOR,
    THROWS,
    TRANSIENT,
    VOLATILE,
    YIELD,
    DELETE,
    NEW,
    IN,
    INSTANCEOF,
    TYPEOF,
    OF,
    KEYOF,
    TYPE,
    WITH,
    AS,
    IS,
    BREAK,
    CASE,
    CATCH,
    CONTINUE,
    DEFAULT,
    DO,
    ELSE,
    FINALLY,
    FOR,
    GOTO,
    IF,
    IMPORT,
    PACKAGE,
    READONLY,
    RETURN,
    SWITCH,
    THROW,
    TRY,
    WHILE,

    CLASS,
    INTERFACE,
    ENUM,
    MODULE,
    UNKNOWN,
    OBJECT,
    BOOLEAN,
    STRING,
    NUMBER,
    BIGINT,
    VOID,
    CONST,
    VAR,
    LET,

    TRUE,
    FALSE,
    NULL,
    NAN,
    UNDEFINED,

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
    GTGTGTEQ,

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
    ELLIPSIS,

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

    ARROW,

    REQUIRE,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,
    SINGLE_BACKTICK_STRING,

    LINE_COMMENT,
    BLOCK_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}