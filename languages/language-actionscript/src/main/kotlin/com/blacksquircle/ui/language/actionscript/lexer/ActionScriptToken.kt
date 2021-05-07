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

package com.blacksquircle.ui.language.actionscript.lexer

enum class ActionScriptToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    BREAK,
    CASE,
    CONTINUE,
    DEFAULT,
    DO,
    WHILE,
    ELSE,
    FOR,
    IN,
    EACH,
    IF,
    LABEL,
    RETURN,
    SUPER,
    SWITCH,
    THROW,
    TRY,
    CATCH,
    FINALLY,
    WITH,
    DYNAMIC,
    FINAL,
    INTERNAL,
    NATIVE,
    OVERRIDE,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    STATIC,
    PARAMETER,
    CLASS,
    CONST,
    EXTENDS,
    FUNCTION,
    GET,
    IMPLEMENTS,
    INTERFACE,
    NAMESPACE,
    PACKAGE,
    TYPEOF,
    SET,
    THIS,
    INCLUDE,
    INSTANCEOF,
    IMPORT,
    USE,
    AS,
    NEW,
    VAR,

    ARRAY,
    OBJECT,
    BOOLEAN,
    NUMBER,
    STRING,
    VOID,
    VECTOR,
    INT,
    UINT,

    TRUE,
    FALSE,
    NULL,
    UNDEFINED,
    NAN,

    // Arithmetic
    PLUS,
    MINUSMINUS,
    DIV,
    PLUSPLUS,
    MOD,
    MULT,
    MINUS,

    // Arithmetic compound assignment
    PLUSEQ,
    DIVEQ,
    MODEQ,
    MULTEQ,
    MINUSEQ,

    // Assignment
    EQ,

    // Bitwise
    AND,
    LTLT,
    TILDE,
    OR,
    GTGT,
    GTGTGT,
    XOR,

    // Bitwise compound assignment
    ANDEQ,
    LTLTEQ,
    OREQ,
    GTGTEQ,
    GTGTGTEQ,
    XOREQ,

    // Comparison
    EQEQ,
    GT,
    GTEQ,
    NOTEQ,
    LT,
    LTEQ,
    EQEQEQ,
    NOTEQEQ,

    // Logical
    ANDAND,
    ANDANDEQ,
    NOT,
    OROR,
    OROREQ,

    // Other
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