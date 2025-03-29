/*
 * Copyright 2025 Squircle CE contributors.
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

package com.blacksquircle.ui.feature.editor.data.model

internal object FileAssociation {

    private val associations = HashMap<String, String>(72)

    init {
        // TODO associations[".as"] = LanguageScope.ACTIONSCRIPT
        associations[".c"] = LanguageScope.C
        associations[".h"] = LanguageScope.C
        associations[".cpp"] = LanguageScope.CPP
        associations[".hpp"] = LanguageScope.CPP
        associations[".ino"] = LanguageScope.CPP
        associations[".cs"] = LanguageScope.CSHARP
        associations[".css"] = LanguageScope.CSS
        associations[".scss"] = LanguageScope.CSS
        // TODO associations[".f90"] = LanguageScope.FORTRAN
        // TODO associations[".f95"] = LanguageScope.FORTRAN
        // TODO associations[".f03"] = LanguageScope.FORTRAN
        // TODO associations[".f08"] = LanguageScope.FORTRAN
        // TODO associations[".f"] = LanguageScope.FORTRAN
        // TODO associations[".for"] = LanguageScope.FORTRAN
        // TODO associations[".ftn"] = LanguageScope.FORTRAN
        associations[".go"] = LanguageScope.GO
        associations[".groovy"] = LanguageScope.GROOVY
        associations[".gvy"] = LanguageScope.GROOVY
        associations[".gy"] = LanguageScope.GROOVY
        associations[".gsh"] = LanguageScope.GROOVY
        associations[".gradle"] = LanguageScope.GROOVY
        associations[".htm"] = LanguageScope.HTML
        associations[".html"] = LanguageScope.HTML
        associations[".ini"] = LanguageScope.INI
        associations[".java"] = LanguageScope.JAVA
        associations[".js"] = LanguageScope.JAVASCRIPT
        associations[".jsx"] = LanguageScope.JAVASCRIPT
        associations[".mjs"] = LanguageScope.JAVASCRIPT
        associations[".cjs"] = LanguageScope.JAVASCRIPT
        associations[".json"] = LanguageScope.JSON
        associations[".jl"] = LanguageScope.JULIA
        associations[".kt"] = LanguageScope.KOTLIN
        associations[".kts"] = LanguageScope.KOTLIN
        associations[".tex"] = LanguageScope.LATEX
        associations[".lisp"] = LanguageScope.LISP
        associations[".lsp"] = LanguageScope.LISP
        associations[".cl"] = LanguageScope.LISP
        associations[".l"] = LanguageScope.LISP
        associations[".lua"] = LanguageScope.LUA
        associations[".md"] = LanguageScope.MARKDOWN
        associations[".php"] = LanguageScope.PHP
        associations[".php3"] = LanguageScope.PHP
        associations[".php4"] = LanguageScope.PHP
        associations[".php5"] = LanguageScope.PHP
        associations[".phps"] = LanguageScope.PHP
        associations[".phtml"] = LanguageScope.PHP
        associations[".txt"] = LanguageScope.TEXT
        associations[".log"] = LanguageScope.TEXT
        associations[".py"] = LanguageScope.PYTHON
        associations[".pyw"] = LanguageScope.PYTHON
        associations[".pyi"] = LanguageScope.PYTHON
        associations[".rb"] = LanguageScope.RUBY
        // TODO associations[".rs"] = LanguageScope.RUST
        // TODO associations[".sh"] = LanguageScope.SHELL
        // TODO associations[".ksh"] = LanguageScope.SHELL
        // TODO associations[".bsh"] = LanguageScope.SHELL
        // TODO associations[".csh"] = LanguageScope.SHELL
        // TODO associations[".tcsh"] = LanguageScope.SHELL
        // TODO associations[".zsh"] = LanguageScope.SHELL
        // TODO associations[".bash"] = LanguageScope.SHELL
        // TODO associations[".smali"] = LanguageScope.SMALI
        // TODO associations[".sql"] = LanguageScope.SQL
        // TODO associations[".sqlite"] = LanguageScope.SQL
        // TODO associations[".sqlite2"] = LanguageScope.SQL
        // TODO associations[".sqlite3"] = LanguageScope.SQL
        // TODO associations[".toml"] = LanguageScope.TOML
        // TODO associations[".ts"] = LanguageScope.TYPESCRIPT
        // TODO associations[".tsx"] = LanguageScope.TYPESCRIPT
        // TODO associations[".mts"] = LanguageScope.TYPESCRIPT
        // TODO associations[".cts"] = LanguageScope.TYPESCRIPT
        // TODO associations[".vb"] = LanguageScope.VISUALBASIC
        // TODO associations[".bas"] = LanguageScope.VISUALBASIC
        // TODO associations[".cls"] = LanguageScope.VISUALBASIC
        // TODO associations[".xhtml"] = LanguageScope.XML
        // TODO associations[".xht"] = LanguageScope.XML
        // TODO associations[".xml"] = LanguageScope.XML
        // TODO associations[".xaml"] = LanguageScope.XML
        // TODO associations[".xdf"] = LanguageScope.XML
        // TODO associations[".xmpp"] = LanguageScope.XML
        // TODO associations[".yaml"] = LanguageScope.YAML
        // TODO associations[".yml"] = LanguageScope.YAML
    }

    fun guessLanguage(extension: String): String {
        return associations[extension] ?: LanguageScope.TEXT
    }
}