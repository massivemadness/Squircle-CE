/*
 * Copyright 2023 Squircle CE contributors.
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

package com.blacksquircle.ui.language.lisp.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.SyntaxHighlightResult
import com.blacksquircle.ui.language.base.model.TextStructure
import com.blacksquircle.ui.language.base.model.TokenType
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.lisp.lexer.LispLexer
import com.blacksquircle.ui.language.lisp.lexer.LispToken
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class LispStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "LispStyler"

        private val METHOD = Pattern.compile(
            "(?<=(defclass|defconstant" +
                "|defgeneric|defmacro|defmethod|defpackage|defparameter|defsetf|defstruct" +
                "|deftype|defun|defvar)) ([\\w-]+)",
        )

        private var lispStyler: LispStyler? = null

        fun getInstance(): LispStyler {
            return lispStyler ?: LispStyler().also {
                lispStyler = it
            }
        }
    }

    override fun execute(structure: TextStructure): List<SyntaxHighlightResult> {
        val source = structure.text.toString()
        val syntaxHighlightResults = mutableListOf<SyntaxHighlightResult>()
        val sourceReader = StringReader(source)
        val lexer = LispLexer(sourceReader)

        // FIXME flex doesn't support positive lookbehind
        val matcher = METHOD.matcher(source)
        matcher.region(0, source.length)
        while (matcher.find()) {
            val tokenType = TokenType.METHOD
            val syntaxHighlightResult = SyntaxHighlightResult(tokenType, matcher.start(), matcher.end())
            syntaxHighlightResults.add(syntaxHighlightResult)
        }

        while (true) {
            try {
                when (lexer.advance()) {
                    LispToken.LONG_LITERAL,
                    LispToken.INTEGER_LITERAL,
                    LispToken.FLOAT_LITERAL,
                    LispToken.DOUBLE_LITERAL -> {
                        val tokenType = TokenType.NUMBER
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LispToken.EQEQ,
                    LispToken.NOTEQ,
                    LispToken.OROR,
                    LispToken.PLUSPLUS,
                    LispToken.MINUSMINUS,
                    LispToken.LT,
                    LispToken.LTLT,
                    LispToken.LTEQ,
                    LispToken.LTLTEQ,
                    LispToken.GT,
                    LispToken.GTGT,
                    LispToken.GTGTGT,
                    LispToken.GTEQ,
                    LispToken.GTGTEQ,
                    LispToken.GTGTGTEQ,
                    LispToken.AND,
                    LispToken.ANDAND,
                    LispToken.PLUSEQ,
                    LispToken.MINUSEQ,
                    LispToken.MULTEQ,
                    LispToken.DIVEQ,
                    LispToken.ANDEQ,
                    LispToken.OREQ,
                    LispToken.XOREQ,
                    LispToken.MODEQ,
                    LispToken.LPAREN,
                    LispToken.RPAREN,
                    LispToken.LBRACE,
                    LispToken.RBRACE,
                    LispToken.LBRACK,
                    LispToken.RBRACK,
                    LispToken.EQ,
                    LispToken.NOT,
                    LispToken.TILDE,
                    LispToken.QUEST,
                    LispToken.COLON,
                    LispToken.PLUS,
                    LispToken.MINUS,
                    LispToken.MULT,
                    LispToken.DIV,
                    LispToken.OR,
                    LispToken.XOR,
                    LispToken.MOD,
                    LispToken.AT,
                    LispToken.BACKTICK,
                    LispToken.SINGLE_QUOTE -> {
                        val tokenType = TokenType.OPERATOR
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LispToken.COMMA,
                    LispToken.DOT -> {
                        continue // skip
                    }
                    LispToken.DEFCLASS,
                    LispToken.DEFCONSTANT,
                    LispToken.DEFGENERIC,
                    LispToken.DEFINE_COMPILER_MACRO,
                    LispToken.DEFINE_CONDITION,
                    LispToken.DEFINE_METHOD_COMBINATION,
                    LispToken.DEFINE_MODIFY_MACRO,
                    LispToken.DEFINE_SETF_EXPANDER,
                    LispToken.DEFINE_SYMBOL_MACRO,
                    LispToken.DEFMACRO,
                    LispToken.DEFMETHOD,
                    LispToken.DEFPACKAGE,
                    LispToken.DEFPARAMETER,
                    LispToken.DEFSETF,
                    LispToken.DEFSTRUCT,
                    LispToken.DEFTYPE,
                    LispToken.DEFUN,
                    LispToken.DEFVAR,
                    LispToken.ABORT,
                    LispToken.ASSERT,
                    LispToken.BLOCK,
                    LispToken.BREAK,
                    LispToken.CASE,
                    LispToken.CATCH,
                    LispToken.CCASE,
                    LispToken.CERROR,
                    LispToken.COND,
                    LispToken.CTYPECASE,
                    LispToken.DECLAIM,
                    LispToken.DECLARE,
                    LispToken.DO,
                    LispToken.DO_S,
                    LispToken.DO_ALL_SYMBOLS,
                    LispToken.DO_EXTERNAL_SYMBOLS,
                    LispToken.DO_SYMBOLS,
                    LispToken.DOLIST,
                    LispToken.DOTIMES,
                    LispToken.ECASE,
                    LispToken.ERROR,
                    LispToken.ETYPECASE,
                    LispToken.EVAL_WHEN,
                    LispToken.FLET,
                    LispToken.HANDLER_BIND,
                    LispToken.HANDLER_CASE,
                    LispToken.IF,
                    LispToken.IGNORE_ERRORS,
                    LispToken.IN_PACKAGE,
                    LispToken.LABELS,
                    LispToken.LAMBDA,
                    LispToken.LET,
                    LispToken.LET_S,
                    LispToken.LOCALLY,
                    LispToken.LOOP,
                    LispToken.MACROLET,
                    LispToken.MULTIPLE_VALUE_BIND,
                    LispToken.PROCLAIM,
                    LispToken.PROG,
                    LispToken.PROG_S,
                    LispToken.PROG1,
                    LispToken.PROG2,
                    LispToken.PROGN,
                    LispToken.PROGV,
                    LispToken.PROVIDE,
                    LispToken.REQUIRE,
                    LispToken.RESTART_BIND,
                    LispToken.RESTART_CASE,
                    LispToken.RESTART_NAME,
                    LispToken.RETURN,
                    LispToken.RETURN_FROM,
                    LispToken.SIGNAL,
                    LispToken.SYMBOL_MACROLET,
                    LispToken.TAGBODY,
                    LispToken.THE,
                    LispToken.THROW,
                    LispToken.TYPECASE,
                    LispToken.UNLESS,
                    LispToken.UNWIND_PROTECT,
                    LispToken.WHEN,
                    LispToken.WITH_ACCESSORS,
                    LispToken.WITH_COMPILATION_UNIT,
                    LispToken.WITH_CONDITION_RESTARTS,
                    LispToken.WITH_HASH_TABLE_ITERATOR,
                    LispToken.WITH_INPUT_FROM_STRING,
                    LispToken.WITH_OPEN_FILE,
                    LispToken.WITH_OPEN_STREAM,
                    LispToken.WITH_OUTPUT_TO_STRING,
                    LispToken.WITH_PACKAGE_ITERATOR,
                    LispToken.WITH_SIMPLE_RESTART,
                    LispToken.WITH_SLOTS,
                    LispToken.WITH_STANDARD_IO_SYNTAX -> {
                        syntaxHighlightResults.add(
                            SyntaxHighlightResult(
                                tokenType = TokenType.KEYWORD,
                                start = lexer.tokenStart,
                                end = lexer.tokenEnd
                            )
                        )
                    }
                    LispToken.TRUE,
                    LispToken.FALSE,
                    LispToken.NULL -> {
                        val tokenType = TokenType.LANG_CONST
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LispToken.DOUBLE_QUOTED_STRING -> {
                        val tokenType = TokenType.STRING
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LispToken.LINE_COMMENT,
                    LispToken.BLOCK_COMMENT -> {
                        val tokenType = TokenType.COMMENT
                        val syntaxHighlightResult = SyntaxHighlightResult(tokenType, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightResults.add(syntaxHighlightResult)
                    }
                    LispToken.IDENTIFIER,
                    LispToken.WHITESPACE,
                    LispToken.BAD_CHARACTER -> {
                        continue
                    }
                    LispToken.EOF -> {
                        break
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message, e)
                break
            }
        }
        return syntaxHighlightResults
    }
}