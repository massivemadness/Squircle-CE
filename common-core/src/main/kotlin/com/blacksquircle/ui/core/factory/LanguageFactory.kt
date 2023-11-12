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

package com.blacksquircle.ui.core.factory

import com.blacksquircle.ui.language.actionscript.ActionScriptLanguage
import com.blacksquircle.ui.language.base.Language
import com.blacksquircle.ui.language.c.CLanguage
import com.blacksquircle.ui.language.cpp.CppLanguage
import com.blacksquircle.ui.language.csharp.CSharpLanguage
import com.blacksquircle.ui.language.css.CssLanguage
import com.blacksquircle.ui.language.fortran.FortranLanguage
import com.blacksquircle.ui.language.go.GoLanguage
import com.blacksquircle.ui.language.groovy.GroovyLanguage
import com.blacksquircle.ui.language.html.HtmlLanguage
import com.blacksquircle.ui.language.ini.IniLanguage
import com.blacksquircle.ui.language.java.JavaLanguage
import com.blacksquircle.ui.language.javascript.JavaScriptLanguage
import com.blacksquircle.ui.language.json.JsonLanguage
import com.blacksquircle.ui.language.julia.JuliaLanguage
import com.blacksquircle.ui.language.kotlin.KotlinLanguage
import com.blacksquircle.ui.language.latex.LatexLanguage
import com.blacksquircle.ui.language.lisp.LispLanguage
import com.blacksquircle.ui.language.lua.LuaLanguage
import com.blacksquircle.ui.language.markdown.MarkdownLanguage
import com.blacksquircle.ui.language.php.PhpLanguage
import com.blacksquircle.ui.language.plaintext.PlainTextLanguage
import com.blacksquircle.ui.language.python.PythonLanguage
import com.blacksquircle.ui.language.ruby.RubyLanguage
import com.blacksquircle.ui.language.rust.RustLanguage
import com.blacksquircle.ui.language.shell.ShellLanguage
import com.blacksquircle.ui.language.smali.SmaliLanguage
import com.blacksquircle.ui.language.sql.SqlLanguage
import com.blacksquircle.ui.language.toml.TomlLanguage
import com.blacksquircle.ui.language.typescript.TypeScriptLanguage
import com.blacksquircle.ui.language.visualbasic.VisualBasicLanguage
import com.blacksquircle.ui.language.xml.XmlLanguage
import com.blacksquircle.ui.language.yaml.YamlLanguage

object LanguageFactory {

    fun create(fileName: String): Language {
        val extension = '.' + fileName.substringAfterLast('.')
        val languageName = FileAssociation.guessLanguage(extension)
        return fromName(languageName)
    }

    fun fromName(languageName: String): Language {
        return when (languageName) {
            ActionScriptLanguage.LANGUAGE_NAME -> ActionScriptLanguage()
            CLanguage.LANGUAGE_NAME -> CLanguage()
            CppLanguage.LANGUAGE_NAME -> CppLanguage()
            CSharpLanguage.LANGUAGE_NAME -> CSharpLanguage()
            CssLanguage.LANGUAGE_NAME -> CssLanguage()
            FortranLanguage.LANGUAGE_NAME -> FortranLanguage()
            GoLanguage.LANGUAGE_NAME -> GoLanguage()
            GroovyLanguage.LANGUAGE_NAME -> GroovyLanguage()
            HtmlLanguage.LANGUAGE_NAME -> HtmlLanguage()
            IniLanguage.LANGUAGE_NAME -> IniLanguage()
            JavaLanguage.LANGUAGE_NAME -> JavaLanguage()
            JavaScriptLanguage.LANGUAGE_NAME -> JavaScriptLanguage()
            JsonLanguage.LANGUAGE_NAME -> JsonLanguage()
            JuliaLanguage.LANGUAGE_NAME -> JuliaLanguage()
            KotlinLanguage.LANGUAGE_NAME -> KotlinLanguage()
            LatexLanguage.LANGUAGE_NAME -> LatexLanguage()
            LispLanguage.LANGUAGE_NAME -> LispLanguage()
            LuaLanguage.LANGUAGE_NAME -> LuaLanguage()
            MarkdownLanguage.LANGUAGE_NAME -> MarkdownLanguage()
            PhpLanguage.LANGUAGE_NAME -> PhpLanguage()
            PlainTextLanguage.LANGUAGE_NAME -> PlainTextLanguage()
            PythonLanguage.LANGUAGE_NAME -> PythonLanguage()
            RubyLanguage.LANGUAGE_NAME -> RubyLanguage()
            RustLanguage.LANGUAGE_NAME -> RustLanguage()
            ShellLanguage.LANGUAGE_NAME -> ShellLanguage()
            SmaliLanguage.LANGUAGE_NAME -> SmaliLanguage()
            SqlLanguage.LANGUAGE_NAME -> SqlLanguage()
            TomlLanguage.LANGUAGE_NAME -> TomlLanguage()
            TypeScriptLanguage.LANGUAGE_NAME -> TypeScriptLanguage()
            VisualBasicLanguage.LANGUAGE_NAME -> VisualBasicLanguage()
            XmlLanguage.LANGUAGE_NAME -> XmlLanguage()
            YamlLanguage.LANGUAGE_NAME -> YamlLanguage()
            else -> PlainTextLanguage()
        }
    }
}