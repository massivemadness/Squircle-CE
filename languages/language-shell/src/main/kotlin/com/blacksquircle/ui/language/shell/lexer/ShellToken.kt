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

package com.blacksquircle.ui.language.shell.lexer

enum class ShellToken {
    INTEGER_LITERAL,
    DOUBLE_LITERAL,

    BREAK,
    CASE,
    CONTINUE,
    ECHO,
    ESAC,
    EVAL,
    ELIF,
    ELSE,
    EXIT,
    EXEC,
    EXPORT,
    DONE,
    DO,
    FI,
    FOR,
    IN,
    FUNCTION,
    IF,
    SET,
    SELECT,
    SHIFT,
    TRAP,
    THEN,
    ULIMIT,
    UMASK,
    UNSET,
    UNTIL,
    WAIT,
    WHILE,
    LET,
    LOCAL,
    READ,
    READONLY,
    RETURN,
    TEST,

    TRUE,
    FALSE,

    MULTEQ,
    DIVEQ,
    MODEQ,
    PLUSEQ,
    MINUSEQ,
    SHIFT_RIGHT_EQ,
    SHIFT_LEFT_EQ,
    BIT_AND_EQ,
    BIT_OR_EQ,
    BIT_XOR_EQ,
    NOTEQ,
    EQEQ,
    REGEXP,
    GTEQ,
    LTEQ,

    PLUS_PLUS,
    MINUS_MINUS,
    EXPONENT,

    BANG,
    TILDE,
    PLUS,
    MINUS,
    MULT,
    DIV,
    MOD,

    SHIFT_LEFT,
    SHIFT_RIGHT,
    LT,
    GT,

    AND_AND,
    OR_OR,
    AND,
    XOR,
    OR,
    DOLLAR,
    EQ,
    BACKTICK,
    QUEST,
    COLON,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACK,
    RBRACK,
    SEMICOLON,
    COMMA,
    DOT,

    EVAL_CONTENT,

    SHEBANG,
    COMMENT,

    DOUBLE_QUOTED_STRING,
    SINGLE_QUOTED_STRING,

    IDENTIFIER,
    WHITESPACE,
    BAD_CHARACTER,
    EOF
}