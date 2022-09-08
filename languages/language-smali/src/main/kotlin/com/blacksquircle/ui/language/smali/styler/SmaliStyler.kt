/*
 * Copyright 2022 Squircle CE contributors.
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

package com.blacksquircle.ui.language.smali.styler

import android.util.Log
import com.blacksquircle.ui.language.base.model.ColorScheme
import com.blacksquircle.ui.language.base.span.StyleSpan
import com.blacksquircle.ui.language.base.span.SyntaxHighlightSpan
import com.blacksquircle.ui.language.base.styler.LanguageStyler
import com.blacksquircle.ui.language.smali.lexer.SmaliLexer
import com.blacksquircle.ui.language.smali.lexer.SmaliToken
import java.io.IOException
import java.io.StringReader

class SmaliStyler private constructor() : LanguageStyler {

    companion object {

        private const val TAG = "SmaliStyler"

        private var smaliStyler: SmaliStyler? = null

        fun getInstance(): SmaliStyler {
            return smaliStyler ?: SmaliStyler().also {
                smaliStyler = it
            }
        }
    }

    override fun execute(source: String, scheme: ColorScheme): List<SyntaxHighlightSpan> {
        val syntaxHighlightSpans = mutableListOf<SyntaxHighlightSpan>()
        val sourceReader = StringReader(source)
        val lexer = SmaliLexer(sourceReader)

        while (true) {
            try {
                when (lexer.advance()) {
                    SmaliToken.LONG_LITERAL,
                    SmaliToken.INTEGER_LITERAL,
                    SmaliToken.FLOAT_LITERAL,
                    SmaliToken.DOUBLE_LITERAL -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.CLASS_DIRECTIVE,
                    SmaliToken.SUPER_DIRECTIVE,
                    SmaliToken.IMPLEMENTS_DIRECTIVE,
                    SmaliToken.SOURCE_DIRECTIVE,
                    SmaliToken.FIELD_DIRECTIVE,
                    SmaliToken.END_FIELD_DIRECTIVE,
                    SmaliToken.SUBANNOTATION_DIRECTIVE,
                    SmaliToken.END_SUBANNOTATION_DIRECTIVE,
                    SmaliToken.ANNOTATION_DIRECTIVE,
                    SmaliToken.END_ANNOTATION_DIRECTIVE,
                    SmaliToken.ENUM_DIRECTIVE,
                    SmaliToken.METHOD_DIRECTIVE,
                    SmaliToken.END_METHOD_DIRECTIVE,
                    SmaliToken.REGISTERS_DIRECTIVE,
                    SmaliToken.LOCALS_DIRECTIVE,
                    SmaliToken.ARRAY_DATA_DIRECTIVE,
                    SmaliToken.END_ARRAY_DATA_DIRECTIVE,
                    SmaliToken.PACKED_SWITCH_DIRECTIVE,
                    SmaliToken.END_PACKED_SWITCH_DIRECTIVE,
                    SmaliToken.SPARSE_SWITCH_DIRECTIVE,
                    SmaliToken.END_SPARSE_SWITCH_DIRECTIVE,
                    SmaliToken.CATCH_DIRECTIVE,
                    SmaliToken.CATCHALL_DIRECTIVE,
                    SmaliToken.LINE_DIRECTIVE,
                    SmaliToken.PARAMETER_DIRECTIVE,
                    SmaliToken.END_PARAMETER_DIRECTIVE,
                    SmaliToken.LOCAL_DIRECTIVE,
                    SmaliToken.END_LOCAL_DIRECTIVE,
                    SmaliToken.RESTART_LOCAL_DIRECTIVE,
                    SmaliToken.PROLOGUE_DIRECTIVE,
                    SmaliToken.EPILOGUE_DIRECTIVE -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.ANNOTATION_VISIBILITY,
                    SmaliToken.ACCESS_SPEC,
                    SmaliToken.HIDDENAPI_RESTRICTION,
                    SmaliToken.VERIFICATION_ERROR_TYPE -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.INLINE_INDEX,
                    SmaliToken.VTABLE_INDEX,
                    SmaliToken.FIELD_OFFSET -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.PRIMITIVE_TYPE,
                    SmaliToken.VOID_TYPE,
                    SmaliToken.CLASS_TYPE -> {
                        val styleSpan = StyleSpan(scheme.numberColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.METHOD_HANDLE_TYPE_FIELD,
                    SmaliToken.METHOD_HANDLE_TYPE_METHOD -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.INSTRUCTION_FORMAT10t,
                    SmaliToken.INSTRUCTION_FORMAT10x,
                    SmaliToken.INSTRUCTION_FORMAT10x_ODEX,
                    SmaliToken.INSTRUCTION_FORMAT11n,
                    SmaliToken.INSTRUCTION_FORMAT11x,
                    SmaliToken.INSTRUCTION_FORMAT12x_OR_ID,
                    SmaliToken.INSTRUCTION_FORMAT12x,
                    SmaliToken.INSTRUCTION_FORMAT20bc,
                    SmaliToken.INSTRUCTION_FORMAT20t,
                    SmaliToken.INSTRUCTION_FORMAT21c_FIELD,
                    SmaliToken.INSTRUCTION_FORMAT21c_FIELD_ODEX,
                    SmaliToken.INSTRUCTION_FORMAT21c_STRING,
                    SmaliToken.INSTRUCTION_FORMAT21c_TYPE,
                    SmaliToken.INSTRUCTION_FORMAT21c_METHOD_HANDLE,
                    SmaliToken.INSTRUCTION_FORMAT21c_METHOD_TYPE,
                    SmaliToken.INSTRUCTION_FORMAT21ih,
                    SmaliToken.INSTRUCTION_FORMAT21lh,
                    SmaliToken.INSTRUCTION_FORMAT21s,
                    SmaliToken.INSTRUCTION_FORMAT21t,
                    SmaliToken.INSTRUCTION_FORMAT22b,
                    SmaliToken.INSTRUCTION_FORMAT22c_FIELD,
                    SmaliToken.INSTRUCTION_FORMAT22c_FIELD_ODEX,
                    SmaliToken.INSTRUCTION_FORMAT22c_TYPE,
                    SmaliToken.INSTRUCTION_FORMAT22cs_FIELD,
                    SmaliToken.INSTRUCTION_FORMAT22s_OR_ID,
                    SmaliToken.INSTRUCTION_FORMAT22s,
                    SmaliToken.INSTRUCTION_FORMAT22t,
                    SmaliToken.INSTRUCTION_FORMAT22x,
                    SmaliToken.INSTRUCTION_FORMAT23x,
                    SmaliToken.INSTRUCTION_FORMAT30t,
                    SmaliToken.INSTRUCTION_FORMAT31c,
                    SmaliToken.INSTRUCTION_FORMAT31i_OR_ID,
                    SmaliToken.INSTRUCTION_FORMAT31i,
                    SmaliToken.INSTRUCTION_FORMAT31t,
                    SmaliToken.INSTRUCTION_FORMAT32x,
                    SmaliToken.INSTRUCTION_FORMAT35c_CALL_SITE,
                    SmaliToken.INSTRUCTION_FORMAT35c_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT35c_METHOD_OR_METHOD_HANDLE_TYPE,
                    SmaliToken.INSTRUCTION_FORMAT35c_METHOD_ODEX,
                    SmaliToken.INSTRUCTION_FORMAT35c_TYPE,
                    SmaliToken.INSTRUCTION_FORMAT35mi_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT35ms_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT3rc_CALL_SITE,
                    SmaliToken.INSTRUCTION_FORMAT3rc_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT3rc_METHOD_ODEX,
                    SmaliToken.INSTRUCTION_FORMAT3rc_TYPE,
                    SmaliToken.INSTRUCTION_FORMAT3rmi_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT3rms_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT45cc_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT4rcc_METHOD,
                    SmaliToken.INSTRUCTION_FORMAT51l -> {
                        val styleSpan = StyleSpan(scheme.keywordColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.TRUE,
                    SmaliToken.FALSE,
                    SmaliToken.NULL -> {
                        val styleSpan = StyleSpan(scheme.langConstColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.DOTDOT,
                    SmaliToken.ARROW,
                    SmaliToken.EQUAL,
                    SmaliToken.COLON,
                    SmaliToken.OPEN_BRACE,
                    SmaliToken.CLOSE_BRACE,
                    SmaliToken.OPEN_PAREN,
                    SmaliToken.CLOSE_PAREN,
                    SmaliToken.AT -> {
                        val styleSpan = StyleSpan(scheme.operatorColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.SEMICOLON,
                    SmaliToken.COMMA,
                    SmaliToken.DOT -> {
                        continue // skip
                    }
                    SmaliToken.DOUBLE_QUOTED_STRING,
                    SmaliToken.SINGLE_QUOTED_STRING -> {
                        val styleSpan = StyleSpan(scheme.stringColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.LINE_COMMENT -> {
                        val styleSpan = StyleSpan(scheme.commentColor)
                        val syntaxHighlightSpan = SyntaxHighlightSpan(styleSpan, lexer.tokenStart, lexer.tokenEnd)
                        syntaxHighlightSpans.add(syntaxHighlightSpan)
                    }
                    SmaliToken.IDENTIFIER,
                    SmaliToken.WHITESPACE,
                    SmaliToken.BAD_CHARACTER -> {
                        continue
                    }
                    SmaliToken.EOF -> {
                        break
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, e.message, e)
                break
            }
        }
        return syntaxHighlightSpans
    }
}