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

package com.lightteam.javascript.language

import com.lightteam.javascript.parser.JavaScriptParser
import com.lightteam.javascript.styler.JavaScriptStyler
import com.lightteam.language.language.Language
import com.lightteam.language.parser.LanguageParser
import com.lightteam.language.styler.LanguageStyler

class JavaScriptLanguage : Language {

    private var javaScriptStyler: JavaScriptStyler? = null
    private var javaScriptParser: JavaScriptParser? = null

    override fun getName(): String {
        return "javascript"
    }

    override fun getStyler(): LanguageStyler {
        if (javaScriptStyler != null) {
            javaScriptStyler?.cancelStyler()
        }
        return JavaScriptStyler()/*javaScriptStyler ?: JavaScriptStyler()
            .also { javaScriptStyler = it }*/
    }

    override fun getParser(): LanguageParser {
        return javaScriptParser ?: JavaScriptParser()
            .also { javaScriptParser = it }
    }

//    override fun getLineComment(): String {
//        return "//"
//    }
//
//    override fun getBlockCommentStart(): String {
//        return "/*"
//    }
//
//    override fun getBlockCommentEnd(): String {
//        return "*/"
//    }

    /*override fun getPatternOfNumbers(): Pattern {
        return Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)")
    }*/

    /*override fun getPatternOfSymbols(): Pattern {
        return Pattern.compile("([!+\\-*<>=?|:%&])")
    }*/

    /*override fun getPatternOfBrackets(): Pattern {
        return Pattern.compile("([(){}\\[\\]])")
    }*/

    /*override fun getPatternOfKeywords(): Pattern {
        return Pattern.compile(
            "(?<=\\b)((break)|(continue)|(else)|(for)|(function)|(if)|(in)|(new)" +
            "|(this)|(var)|(while)|(return)|(case)|(catch)|(of)|(typeof)" +
            "|(const)|(default)|(do)|(switch)|(try)|(null)|(true)" +
            "|(false)|(eval)|(let))(?=\\b)"
        )
    }*/

    /*override fun getPatternOfMethods(): Pattern {
        return Pattern.compile("(?<=(function) )(\\w+)", Pattern.CASE_INSENSITIVE)
    }

    override fun getPatternOfStrings(): Pattern {
        return Pattern.compile("\"(.*?)\"|'(.*?)'")
    }*/

//    override fun getPatternOfComments(): Pattern {
//        return Pattern.compile("/\\*(?:.|[\\n\\r])*?\\*/|//.*")
//    }
}