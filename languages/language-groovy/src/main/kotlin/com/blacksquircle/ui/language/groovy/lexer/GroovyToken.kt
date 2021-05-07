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

package com.blacksquircle.ui.language.groovy.lexer

enum class GroovyToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    PACKAGE,
    STRICTFP,
    IMPORT,
    STATIC,
    DEF,
    VAR,
    CLASS,
    INTERFACE,
    ENUM,
    TRAIT,
    EXTENDS,
    SUPER,
    VOID,
    AS,
    PRIVATE,
    ABSTRACT,
    PUBLIC,
    PROTECTED,
    TRANSIENT,
    NATIVE,
    SYNCHRONIZED,
    VOLATILE,
    DEFAULT,
    DO,
    THROWS,
    IMPLEMENTS,
    THIS,
    IF,
    ELSE,
    WHILE,
    SWITCH,
    FOR,
    IN,
    RETURN,
    BREAK,
    CONTINUE,
    THROW,
    ASSERT,
    CASE,
    TRY,
    FINALLY,
    CATCH,
    INSTANCEOF,
    NEW,
    FINAL,

    NOT_IN,
    NOT_INSTANCEOF,

    BOOLEAN,
    BYTE,
    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    SHORT,

    TRUE,
    FALSE,
    NULL,

    EQEQ,
    NOTEQ,
    EQEQEQ,
    NOTEQEQEQ,
    OROR,
    PLUSPLUS,
    MINUSMINUS,
    POW,
    LTEQGT,

    LT,
    LTEQ,
    LTLTEQ,

    GT,
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
    QUESTEQ,
    POWEQ,

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
    RANGE,

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
    SPREAD_DOT,
    SAFE_DOT,
    METHOD_CLOSURE,
    REGEX_FIND,
    REGEX_MATCH,
    DOUBLE_COLON,
    ARROW,

    ANNOTATION,

    SINGLE_QUOTED_STRING,
    DOUBLE_QUOTED_STRING,
    TRIPLE_QUOTED_STRING,

    SHEBANG_COMMENT,
    LINE_COMMENT,
    BLOCK_COMMENT,
    DOC_COMMENT,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}