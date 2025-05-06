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
        associations[".c"] = LanguageScope.C
        associations[".h"] = LanguageScope.C
        associations[".cpp"] = LanguageScope.CPP
        associations[".hpp"] = LanguageScope.CPP
        associations[".ino"] = LanguageScope.CPP
        associations[".cs"] = LanguageScope.CSHARP
        associations[".css"] = LanguageScope.CSS
        associations[".scss"] = LanguageScope.CSS
        associations[".Dockerfile"] = LanguageScope.DOCKER
        associations[".f03"] = LanguageScope.FORTRAN
        associations[".f08"] = LanguageScope.FORTRAN
        associations[".f18"] = LanguageScope.FORTRAN
        associations[".f77"] = LanguageScope.FORTRAN
        associations[".f90"] = LanguageScope.FORTRAN
        associations[".f95"] = LanguageScope.FORTRAN
        associations[".f"] = LanguageScope.FORTRAN
        associations[".fpp"] = LanguageScope.FORTRAN
        associations[".for"] = LanguageScope.FORTRAN
        associations[".ftn"] = LanguageScope.FORTRAN
        associations[".pf"] = LanguageScope.FORTRAN
        associations[".go"] = LanguageScope.GO
        associations[".groovy"] = LanguageScope.GROOVY
        associations[".gvy"] = LanguageScope.GROOVY
        associations[".gy"] = LanguageScope.GROOVY
        associations[".gsh"] = LanguageScope.GROOVY
        associations[".gradle"] = LanguageScope.GROOVY
        associations[".htm"] = LanguageScope.HTML
        associations[".html"] = LanguageScope.HTML
        associations[".ini"] = LanguageScope.INI
        associations[".properties"] = LanguageScope.INI
        associations[".editorconfig"] = LanguageScope.INI
        associations[".env"] = LanguageScope.INI
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
        associations[".Makefile"] = LanguageScope.MAKE
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
        associations[".rs"] = LanguageScope.RUST
        associations[".sh"] = LanguageScope.SHELL
        associations[".ksh"] = LanguageScope.SHELL
        associations[".bsh"] = LanguageScope.SHELL
        associations[".csh"] = LanguageScope.SHELL
        associations[".tcsh"] = LanguageScope.SHELL
        associations[".zsh"] = LanguageScope.SHELL
        associations[".bash"] = LanguageScope.SHELL
        associations[".smali"] = LanguageScope.SMALI
        associations[".sql"] = LanguageScope.SQL
        associations[".sqlite"] = LanguageScope.SQL
        associations[".sqlite2"] = LanguageScope.SQL
        associations[".sqlite3"] = LanguageScope.SQL
        associations[".toml"] = LanguageScope.TOML
        associations[".ts"] = LanguageScope.TYPESCRIPT
        associations[".tsx"] = LanguageScope.TYPESCRIPT
        associations[".mts"] = LanguageScope.TYPESCRIPT
        associations[".cts"] = LanguageScope.TYPESCRIPT
        associations[".vb"] = LanguageScope.VISUALBASIC
        associations[".bas"] = LanguageScope.VISUALBASIC
        associations[".cls"] = LanguageScope.VISUALBASIC
        associations[".xhtml"] = LanguageScope.XML
        associations[".xht"] = LanguageScope.XML
        associations[".xml"] = LanguageScope.XML
        associations[".xaml"] = LanguageScope.XML
        associations[".xdf"] = LanguageScope.XML
        associations[".xmpp"] = LanguageScope.XML
        associations[".yaml"] = LanguageScope.YAML
        associations[".yml"] = LanguageScope.YAML
    }

    fun guessLanguage(extension: String): String {
        return associations[extension] ?: LanguageScope.TEXT
    }
}