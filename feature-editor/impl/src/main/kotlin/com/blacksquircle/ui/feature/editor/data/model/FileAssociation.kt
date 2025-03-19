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
        associations[".as"] = LanguageType.ACTIONSCRIPT
        associations[".c"] = LanguageType.C
        associations[".h"] = LanguageType.C
        associations[".cpp"] = LanguageType.CPP
        associations[".hpp"] = LanguageType.CPP
        associations[".ino"] = LanguageType.CPP
        associations[".cs"] = LanguageType.CSHARP
        associations[".css"] = LanguageType.CSS
        associations[".scss"] = LanguageType.CSS
        associations[".f90"] = LanguageType.FORTRAN
        associations[".f95"] = LanguageType.FORTRAN
        associations[".f03"] = LanguageType.FORTRAN
        associations[".f08"] = LanguageType.FORTRAN
        associations[".f"] = LanguageType.FORTRAN
        associations[".for"] = LanguageType.FORTRAN
        associations[".ftn"] = LanguageType.FORTRAN
        associations[".go"] = LanguageType.GO
        associations[".groovy"] = LanguageType.GROOVY
        associations[".gvy"] = LanguageType.GROOVY
        associations[".gy"] = LanguageType.GROOVY
        associations[".gsh"] = LanguageType.GROOVY
        associations[".gradle"] = LanguageType.GROOVY
        associations[".htm"] = LanguageType.HTML
        associations[".html"] = LanguageType.HTML
        associations[".ini"] = LanguageType.INI
        associations[".java"] = LanguageType.JAVA
        associations[".js"] = LanguageType.JAVASCRIPT
        associations[".jsx"] = LanguageType.JAVASCRIPT
        associations[".mjs"] = LanguageType.JAVASCRIPT
        associations[".cjs"] = LanguageType.JAVASCRIPT
        associations[".json"] = LanguageType.JSON
        associations[".jl"] = LanguageType.JULIA
        associations[".kt"] = LanguageType.KOTLIN
        associations[".kts"] = LanguageType.KOTLIN
        associations[".tex"] = LanguageType.LATEX
        associations[".lisp"] = LanguageType.LISP
        associations[".lsp"] = LanguageType.LISP
        associations[".cl"] = LanguageType.LISP
        associations[".l"] = LanguageType.LISP
        associations[".lua"] = LanguageType.LUA
        associations[".md"] = LanguageType.MARKDOWN
        associations[".php"] = LanguageType.PHP
        associations[".php3"] = LanguageType.PHP
        associations[".php4"] = LanguageType.PHP
        associations[".php5"] = LanguageType.PHP
        associations[".phps"] = LanguageType.PHP
        associations[".phtml"] = LanguageType.PHP
        associations[".txt"] = LanguageType.TEXT
        associations[".log"] = LanguageType.TEXT
        associations[".py"] = LanguageType.PYTHON
        associations[".pyw"] = LanguageType.PYTHON
        associations[".pyi"] = LanguageType.PYTHON
        associations[".rb"] = LanguageType.RUBY
        associations[".rs"] = LanguageType.RUST
        associations[".sh"] = LanguageType.SHELL
        associations[".ksh"] = LanguageType.SHELL
        associations[".bsh"] = LanguageType.SHELL
        associations[".csh"] = LanguageType.SHELL
        associations[".tcsh"] = LanguageType.SHELL
        associations[".zsh"] = LanguageType.SHELL
        associations[".bash"] = LanguageType.SHELL
        associations[".smali"] = LanguageType.SMALI
        associations[".sql"] = LanguageType.SQL
        associations[".sqlite"] = LanguageType.SQL
        associations[".sqlite2"] = LanguageType.SQL
        associations[".sqlite3"] = LanguageType.SQL
        associations[".toml"] = LanguageType.TOML
        associations[".ts"] = LanguageType.TYPESCRIPT
        associations[".tsx"] = LanguageType.TYPESCRIPT
        associations[".mts"] = LanguageType.TYPESCRIPT
        associations[".cts"] = LanguageType.TYPESCRIPT
        associations[".vb"] = LanguageType.VISUALBASIC
        associations[".bas"] = LanguageType.VISUALBASIC
        associations[".cls"] = LanguageType.VISUALBASIC
        associations[".xhtml"] = LanguageType.XML
        associations[".xht"] = LanguageType.XML
        associations[".xml"] = LanguageType.XML
        associations[".xaml"] = LanguageType.XML
        associations[".xdf"] = LanguageType.XML
        associations[".xmpp"] = LanguageType.XML
        associations[".yaml"] = LanguageType.YAML
        associations[".yml"] = LanguageType.YAML
    }

    fun guessLanguage(extension: String): String {
        return associations[extension] ?: LanguageType.TEXT
    }
}