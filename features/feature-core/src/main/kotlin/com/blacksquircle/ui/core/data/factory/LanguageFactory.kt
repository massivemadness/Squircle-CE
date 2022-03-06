/*
 * Copyright 2022 Squircle IDE contributors.
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

package com.blacksquircle.ui.core.data.factory

import com.blacksquircle.ui.language.actionscript.ActionScriptLanguage
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.c.CLanguage
import com.blacksquircle.ui.language.cpp.CppLanguage
import com.blacksquircle.ui.language.csharp.CSharpLanguage
import com.blacksquircle.ui.language.groovy.GroovyLanguage
import com.blacksquircle.ui.language.html.HtmlLanguage
import com.blacksquircle.ui.language.java.JavaLanguage
import com.blacksquircle.ui.language.javascript.JavaScriptLanguage
import com.blacksquircle.ui.language.json.JsonLanguage
import com.blacksquircle.ui.language.julia.JuliaLanguage
import com.blacksquircle.ui.language.kotlin.KotlinLanguage
import com.blacksquircle.ui.language.lisp.LispLanguage
import com.blacksquircle.ui.language.lua.LuaLanguage
import com.blacksquircle.ui.language.markdown.MarkdownLanguage
import com.blacksquircle.ui.language.php.PhpLanguage
import com.blacksquircle.ui.language.plaintext.PlainTextLanguage
import com.blacksquircle.ui.language.python.PythonLanguage
import com.blacksquircle.ui.language.ruby.RubyLanguage
import com.blacksquircle.ui.language.shell.ShellLanguage
import com.blacksquircle.ui.language.sql.SqlLanguage
import com.blacksquircle.ui.language.typescript.TypeScriptLanguage
import com.blacksquircle.ui.language.visualbasic.VisualBasicLanguage
import com.blacksquircle.ui.language.xml.XmlLanguage
import com.blacksquircle.ui.language.yaml.YamlLanguage

object LanguageFactory {

    fun create(fileName: String): Language {
        return when {
            ActionScriptLanguage.supportFormat(fileName) -> ActionScriptLanguage()
            CLanguage.supportFormat(fileName) -> CLanguage()
            CppLanguage.supportFormat(fileName) -> CppLanguage()
            CSharpLanguage.supportFormat(fileName) -> CSharpLanguage()
            GroovyLanguage.supportFormat(fileName) -> GroovyLanguage()
            HtmlLanguage.supportFormat(fileName) -> HtmlLanguage()
            JavaLanguage.supportFormat(fileName) -> JavaLanguage()
            JavaScriptLanguage.supportFormat(fileName) -> JavaScriptLanguage()
            JsonLanguage.supportFormat(fileName) -> JsonLanguage()
            JuliaLanguage.supportFormat(fileName) -> JuliaLanguage()
            KotlinLanguage.supportFormat(fileName) -> KotlinLanguage()
            LispLanguage.supportFormat(fileName) -> LispLanguage()
            LuaLanguage.supportFormat(fileName) -> LuaLanguage()
            MarkdownLanguage.supportFormat(fileName) -> MarkdownLanguage()
            PhpLanguage.supportFormat(fileName) -> PhpLanguage()
            PythonLanguage.supportFormat(fileName) -> PythonLanguage()
            RubyLanguage.supportFormat(fileName) -> RubyLanguage()
            ShellLanguage.supportFormat(fileName) -> ShellLanguage()
            SqlLanguage.supportFormat(fileName) -> SqlLanguage()
            TypeScriptLanguage.supportFormat(fileName) -> TypeScriptLanguage()
            VisualBasicLanguage.supportFormat(fileName) -> VisualBasicLanguage()
            XmlLanguage.supportFormat(fileName) -> XmlLanguage()
            YamlLanguage.supportFormat(fileName) -> YamlLanguage()
            else -> PlainTextLanguage()
        }
    }
}