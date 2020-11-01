/*
 * Copyright 2020 Brackeys IDE contributors.
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

package com.brackeys.ui.data.delegate

import com.brackeys.ui.language.actionscript.ActionScriptLanguage
import com.brackeys.ui.language.base.Language
import com.brackeys.ui.language.c.CLanguage
import com.brackeys.ui.language.cpp.CppLanguage
import com.brackeys.ui.language.csharp.CSharpLanguage
import com.brackeys.ui.language.java.JavaLanguage
import com.brackeys.ui.language.javascript.JavaScriptLanguage
import com.brackeys.ui.language.json.JsonLanguage
import com.brackeys.ui.language.lisp.LispLanguage
import com.brackeys.ui.language.lua.LuaLanguage
import com.brackeys.ui.language.markdown.MarkdownLanguage
import com.brackeys.ui.language.plaintext.PlainTextLanguage
import com.brackeys.ui.language.python.PythonLanguage
import com.brackeys.ui.language.visualbasic.VisualBasicLanguage

object LanguageDelegate {

    fun provideLanguage(fileName: String): Language {
        return when {
            JavaScriptLanguage.supportFormat(fileName) -> JavaScriptLanguage()
            JsonLanguage.supportFormat(fileName) -> JsonLanguage()
            ActionScriptLanguage.supportFormat(fileName) -> ActionScriptLanguage()
            CSharpLanguage.supportFormat(fileName) -> CSharpLanguage()
            CLanguage.supportFormat(fileName) -> CLanguage()
            CppLanguage.supportFormat(fileName) -> CppLanguage()
            JavaLanguage.supportFormat(fileName) -> JavaLanguage()
            LispLanguage.supportFormat(fileName) -> LispLanguage()
            LuaLanguage.supportFormat(fileName) -> LuaLanguage()
            MarkdownLanguage.supportFormat(fileName) -> MarkdownLanguage()
            PythonLanguage.supportFormat(fileName) -> PythonLanguage()
            VisualBasicLanguage.supportFormat(fileName) -> VisualBasicLanguage()
            else -> PlainTextLanguage()
        }
    }
}