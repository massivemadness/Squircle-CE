/*
 * Licensed to the Light Team Software (Light Team) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Light Team licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightteam.language.javascript.styler.lexer

enum class JavaScriptToken {
    LONG_LITERAL,
    INTEGER_LITERAL,
    FLOAT_LITERAL,
    DOUBLE_LITERAL,

    FUNCTION,
    PROTOTYPE,
    DEBUGGER,
    SUPER,
    THIS,
    ASYNC,
    AWAIT,
    EXPORT,
    FROM,
    EXTENDS,
    FINAL,
    IMPLEMENTS,
    NATIVE,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    STATIC,
    SYNCHRONIZED,
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
    WITH,
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
    RETURN,
    SWITCH,
    THROW,
    TRY,
    WHILE,

    CLASS,
    INTERFACE,
    ENUM,
    BOOLEAN,
    BYTE,
    CHAR,
    DOUBLE,
    FLOAT,
    INT,
    LONG,
    SHORT,
    VOID,
    CONST,
    VAR,
    LET,

    TRUE,
    FALSE,
    NULL,
    NAN,

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

    STRING_LITERAL,
    COMMENT,
    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}